package unoeste.fipp.bomservico.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import unoeste.fipp.bomservico.entities.Categoria;
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {}
