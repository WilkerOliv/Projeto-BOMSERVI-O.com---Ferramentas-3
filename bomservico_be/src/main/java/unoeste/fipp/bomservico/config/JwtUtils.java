package unoeste.fipp.bomservico.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário de JWT para gerar e validar tokens contendo:
 *  - sub (login do usuário)
 *  - nivel (Integer do nível do usuário: 1=ADMIN; 0=PRESTADOR)
 *
 * Métodos públicos usados pelo filtro e controllers:
 *  - String generateToken(String login, Integer nivel)
 *  - boolean validateJwtToken(String token)
 *  - String getUserNameFromJwtToken(String token)
 *  - Integer getNivelFromJwtToken(String token)
 */
@Component
public class JwtUtils {

    // Chave secreta (mín. 256 bits para HS256). Configure no application.properties.
    // Ex.: jwt.secret = change-me-change-me-change-me-change-me-32bytes
    @Value("${jwt.secret:change-me-change-me-change-me-change-me-32bytes}")
    private String secret;

    // Expiração em milissegundos (default: 24h)
    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // Cria a chave HMAC a partir do secret
        // OBS: para produção, use um secret aleatório longo (>=32 chars) e seguro.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gera um JWT com claims "sub" (login) e "nivel".
     */
    public String generateToken(String login, Integer nivel) {
        Map<String, Object> claims = new HashMap<>();
        if (nivel != null) {
            claims.put("nivel", nivel);
        }

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(login)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida a assinatura e a expiração do token.
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // expirado
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // inválido por qualquer outro motivo (assinatura, formato, etc.)
            return false;
        }
    }

    /**
     * Lê o "sub" (login) do token.
     */
    public String getUserNameFromJwtToken(String token) {
        Claims claims = parseClaims(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    /**
     * Lê o "nivel" (Integer) do token.
     * Retorna null quando o claim não existir ou não puder ser interpretado como número.
     */
    public Integer getNivelFromJwtToken(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) return null;

        Object raw = claims.get("nivel");
        if (raw instanceof Integer) {
            return (Integer) raw;
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }
        if (raw instanceof String) {
            try {
                return Integer.parseInt((String) raw);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    // --------- helpers ---------

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
