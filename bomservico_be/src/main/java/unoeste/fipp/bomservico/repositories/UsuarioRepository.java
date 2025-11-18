package unoeste.fipp.bomservico.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import unoeste.fipp.bomservico.entities.Usuario;
public interface UsuarioRepository extends JpaRepository<Usuario, String> {}
