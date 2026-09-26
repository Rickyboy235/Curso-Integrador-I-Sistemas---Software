package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.ContactoCliente;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactoClienteRepository extends JpaRepository<ContactoCliente, Integer> {

    List<ContactoCliente> findByCliente_IdCliente(Integer idCliente);

    Optional<ContactoCliente> findByCliente_IdClienteAndEsPrincipalTrue(Integer idCliente);
}