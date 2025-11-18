package unoeste.fipp.bomservico.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import unoeste.fipp.bomservico.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);

            if (jwtUtils.validateJwtToken(token)) {
                String login = jwtUtils.getUserNameFromJwtToken(token);
                Integer nivel = jwtUtils.getNivelFromJwtToken(token);

                if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Mapeia nivel do banco -> roles
                    List<SimpleGrantedAuthority> roles;
                    if (nivel != null && nivel == 1) {
                        roles = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    } else {
                        roles = List.of(new SimpleGrantedAuthority("ROLE_PRESTADOR"));
                    }

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(login, null, roles);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
