package transflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import transflow.model.OrdenServicio;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Integer> {

    Optional<OrdenServicio> findByNumeroOrden(String numeroOrden);

    List<OrdenServicio> findByCotizacion_IdCotizacion(Integer idCotizacion);

    List<OrdenServicio> findByEstadoServicio(String estadoServicio);

    List<OrdenServicio> findByVehiculo_IdVehiculo(Integer idVehiculo);

    List<OrdenServicio> findByConductor_IdConductor(Integer idConductor);
}