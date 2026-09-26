package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.ServicioCotizado;

import java.util.List;

@Repository
public interface ServicioCotizadoRepository extends JpaRepository<ServicioCotizado, Integer> {

    List<ServicioCotizado> findByCotizacion_IdCotizacion(Integer idCotizacion);
}