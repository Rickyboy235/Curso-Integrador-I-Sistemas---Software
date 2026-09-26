package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transflow.dto.ClienteDTO;
import transflow.model.Cliente;
import transflow.repository.ClienteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO obtenerPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        return toDTO(cliente);
    }

    public ClienteDTO crear(ClienteDTO dto) {
        if (clienteRepository.findByNumDoc(dto.getNumDoc()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con el documento: " + dto.getNumDoc());
        }

        Cliente cliente = Cliente.builder()
                .tipoDoc(dto.getTipoDoc())
                .numDoc(dto.getNumDoc())
                .razonSocial(dto.getRazonSocial())
                .emailPrincipal(dto.getEmailPrincipal())
                .estadoCliente(dto.getEstadoCliente())
                .build();

        if (!cliente.validarDocumento()) {
            throw new RuntimeException("El número de documento no es válido para el tipo: " + dto.getTipoDoc());
        }

        return toDTO(clienteRepository.save(cliente));
    }

    public ClienteDTO actualizar(Integer id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));

        cliente.setTipoDoc(dto.getTipoDoc());
        cliente.setNumDoc(dto.getNumDoc());
        cliente.setRazonSocial(dto.getRazonSocial());
        cliente.setEmailPrincipal(dto.getEmailPrincipal());
        cliente.setEstadoCliente(dto.getEstadoCliente());

        return toDTO(clienteRepository.save(cliente));
    }

    public void eliminar(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteDTO toDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .idCliente(cliente.getIdCliente())
                .tipoDoc(cliente.getTipoDoc())
                .numDoc(cliente.getNumDoc())
                .razonSocial(cliente.getRazonSocial())
                .emailPrincipal(cliente.getEmailPrincipal())
                .estadoCliente(cliente.getEstadoCliente())
                .build();
    }
}