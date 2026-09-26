package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.Insumo;

import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    Optional<Insumo> findByCodigo(String codigo);
}