package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.DetalleVale;
import transflow.model.DetalleValeId;

import java.util.List;

@Repository
public interface DetalleValeRepository extends JpaRepository<DetalleVale, DetalleValeId> {

    List<DetalleVale> findByValeSalida_IdVale(Integer idVale);
}