package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.Conductor;

import java.util.Optional;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

    Optional<Conductor> findByNumDoc(String numDoc);

    Optional<Conductor> findByLicencia(String licencia);
}