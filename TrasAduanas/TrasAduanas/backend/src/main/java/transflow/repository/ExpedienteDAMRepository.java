package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.ExpedienteDAM;

import java.util.Optional;

@Repository
public interface ExpedienteDAMRepository extends JpaRepository<ExpedienteDAM, Integer> {

    Optional<ExpedienteDAM> findByOrdenServicio_IdOrden(Integer idOrden);

    Optional<ExpedienteDAM> findByNumeroDam(String numeroDam);
}