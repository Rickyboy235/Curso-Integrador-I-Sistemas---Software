package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.ValeSalida;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValeSalidaRepository extends JpaRepository<ValeSalida, Integer> {

    Optional<ValeSalida> findByNumeroVale(String numeroVale);

    List<ValeSalida> findByOrdenServicio_IdOrden(Integer idOrden);
}