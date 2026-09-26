package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.DireccionClienteDTO;
import transflow.model.Cliente;
import transflow.model.DireccionCliente;
import transflow.repository.ClienteRepository;
import transflow.repository.DireccionClienteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionClienteService {

    private final DireccionClienteRepository direccionClienteRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<DireccionClienteDTO> listarPorCliente(Integer idCliente) {
        return direccionClienteRepository.findByCliente_IdCliente(idCliente).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DireccionClienteDTO agregar(Integer idCliente, DireccionClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + idCliente));

        DireccionCliente direccion = DireccionCliente.builder()
                .tipoDireccion(dto.getTipoDireccion())
                .direccion(dto.getDireccion())
                .distrito(dto.getDistrito())
                .provincia(dto.getProvincia())
                .build();

        cliente.addDireccion(direccion);
        clienteRepository.save(cliente);

        return toDTO(direccion);
    }

    @Transactional
    public DireccionClienteDTO actualizar(Integer idCliente, Integer idDireccion, DireccionClienteDTO dto) {
        DireccionCliente direccion = direccionClienteRepository.findById(idDireccion)
                .orElseThrow(() -> new RuntimeException("Direccion no encontrada con id: " + idDireccion));

        if (!direccion.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("La direccion no pertenece al cliente: " + idCliente);
        }

        direccion.setTipoDireccion(dto.getTipoDireccion());
        direccion.setDireccion(dto.getDireccion());
        direccion.setDistrito(dto.getDistrito());
        direccion.setProvincia(dto.getProvincia());

        return toDTO(direccionClienteRepository.save(direccion));
    }

    @Transactional
    public void eliminar(Integer idCliente, Integer idDireccion) {
        DireccionCliente direccion = direccionClienteRepository.findById(idDireccion)
                .orElseThrow(() -> new RuntimeException("Direccion no encontrada con id: " + idDireccion));

        if (!direccion.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("La direccion no pertenece al cliente: " + idCliente);
        }

        direccionClienteRepository.deleteById(idDireccion);
    }

    private DireccionClienteDTO toDTO(DireccionCliente d) {
        return DireccionClienteDTO.builder()
                .idDireccion(d.getIdDireccion())
                .idCliente(d.getCliente().getIdCliente())
                .tipoDireccion(d.getTipoDireccion())
                .direccion(d.getDireccion())
                .distrito(d.getDistrito())
                .provincia(d.getProvincia())
                .build();
    }
}