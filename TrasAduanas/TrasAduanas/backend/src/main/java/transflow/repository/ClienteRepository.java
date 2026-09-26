package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.Cliente;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByNumDoc(String numDoc);

    List<Cliente> findByRazonSocialContainingIgnoreCase(String razonSocial);

    List<Cliente> findByEstadoCliente(Boolean estadoCliente);
}