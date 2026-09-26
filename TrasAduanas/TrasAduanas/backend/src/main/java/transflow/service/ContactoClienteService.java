package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transflow.dto.ContactoClienteDTO;
import transflow.model.Cliente;
import transflow.model.ContactoCliente;
import transflow.repository.ClienteRepository;
import transflow.repository.ContactoClienteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactoClienteService {

    private final ContactoClienteRepository contactoClienteRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ContactoClienteDTO> listarPorCliente(Integer idCliente) {
        return contactoClienteRepository.findByCliente_IdCliente(idCliente).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContactoClienteDTO agregar(Integer idCliente, ContactoClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + idCliente));

        ContactoCliente contacto = ContactoCliente.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .cargo(dto.getCargo())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .esPrincipal(dto.getEsPrincipal())
                .build();

        cliente.addContacto(contacto);
        clienteRepository.save(cliente);

        return toDTO(contacto);
    }

    @Transactional
    public ContactoClienteDTO actualizar(Integer idCliente, Integer idContacto, ContactoClienteDTO dto) {
        ContactoCliente contacto = contactoClienteRepository.findById(idContacto)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado con id: " + idContacto));

        if (!contacto.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("El contacto no pertenece al cliente: " + idCliente);
        }

        contacto.setNombres(dto.getNombres());
        contacto.setApellidos(dto.getApellidos());
        contacto.setCargo(dto.getCargo());
        contacto.setEmail(dto.getEmail());
        contacto.setTelefono(dto.getTelefono());
        contacto.setEsPrincipal(dto.getEsPrincipal());

        return toDTO(contactoClienteRepository.save(contacto));
    }

    @Transactional
    public void eliminar(Integer idCliente, Integer idContacto) {
        ContactoCliente contacto = contactoClienteRepository.findById(idContacto)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado con id: " + idContacto));

        if (!contacto.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("El contacto no pertenece al cliente: " + idCliente);
        }

        contactoClienteRepository.deleteById(idContacto);
    }

    private ContactoClienteDTO toDTO(ContactoCliente c) {
        return ContactoClienteDTO.builder()
                .idContacto(c.getIdContacto())
                .idCliente(c.getCliente().getIdCliente())
                .nombres(c.getNombres())
                .apellidos(c.getApellidos())
                .cargo(c.getCargo())
                .email(c.getEmail())
                .telefono(c.getTelefono())
                .esPrincipal(c.getEsPrincipal())
                .build();
    }
}