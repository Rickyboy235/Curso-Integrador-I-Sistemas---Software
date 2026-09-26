package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndDni(String email, String dni);

    List<Usuario> findByRol_IdRol(Integer idRol);

    List<Usuario> findByEstadoUsuario(Boolean estadoUsuario);
}