package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.LiquidacionTributos;

import java.util.Optional;

@Repository
public interface LiquidacionTributosRepository extends JpaRepository<LiquidacionTributos, Integer> {

    Optional<LiquidacionTributos> findByExpedienteDam_IdExpediente(Integer idExpediente);
}