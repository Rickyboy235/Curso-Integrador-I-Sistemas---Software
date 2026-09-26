package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.Cotizacion;

import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {

    Optional<Cotizacion> findByCodigoCotizacion(String codigoCotizacion);

    List<Cotizacion> findByCliente_IdCliente(Integer idCliente);

    List<Cotizacion> findByUsuario_IdUsuario(Integer idUsuario);

    List<Cotizacion> findByEstadoCotizacion(String estadoCotizacion);
}