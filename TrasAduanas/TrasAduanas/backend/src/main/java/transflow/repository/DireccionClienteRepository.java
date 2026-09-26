package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.DireccionCliente;

import java.util.List;

@Repository
public interface DireccionClienteRepository extends JpaRepository<DireccionCliente, Integer> {

    List<DireccionCliente> findByCliente_IdCliente(Integer idCliente);
}