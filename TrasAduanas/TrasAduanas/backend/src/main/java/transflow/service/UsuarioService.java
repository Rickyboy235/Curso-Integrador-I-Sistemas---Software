package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import transflow.dto.UsuarioDTO;
import transflow.model.Rol;
import transflow.model.Usuario;
import transflow.repository.RolRepository;
import transflow.repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Integer id) {
        return toDTO(buscarUsuario(id));
    }

    public UsuarioDTO crear(UsuarioDTO dto) {
        Rol rol = buscarRol(dto.getIdRol());

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria para crear un usuario");
        }

        Usuario usuario = Usuario.builder()
                .rol(rol)
                .email(dto.getEmail())
                .dni(dto.getDni())
                .nombresUsuario(dto.getNombresUsuario())
                .apellidosUsuario(dto.getApellidosUsuario())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .estadoUsuario(dto.getEstadoUsuario() != null ? dto.getEstadoUsuario() : true)
                .build();

        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO actualizar(Integer id, UsuarioDTO dto) {
        Usuario usuario = buscarUsuario(id);
        Rol rol = buscarRol(dto.getIdRol());

        usuario.setRol(rol);
        usuario.setEmail(dto.getEmail());
        usuario.setDni(dto.getDni());
        usuario.setNombresUsuario(dto.getNombresUsuario());
        usuario.setApellidosUsuario(dto.getApellidosUsuario());
        usuario.setEstadoUsuario(dto.getEstadoUsuario());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        return toDTO(usuarioRepository.save(usuario));
    }

    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }

        try {

            usuarioRepository.deleteById(id);

        } catch (org.springframework.dao.DataIntegrityViolationException e) {

            throw new RuntimeException(
                    "No se puede eliminar el usuario porque tiene cotizaciones u otros registros asociados."
            );
        }
    }

    public UsuarioDTO autenticar(String email, String dni, String password) {
        Usuario usuario = usuarioRepository.findByEmailAndDni(email, dni)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!usuario.estaActivo()) {
            throw new RuntimeException("El usuario está inactivo");
        }

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return toDTO(usuario);
    }

    private Usuario buscarUsuario(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    private Rol buscarRol(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + idRol));
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .idRol(usuario.getRol().getIdRol())
                .rolNombre(usuario.getRol().getNombreRol())
                .email(usuario.getEmail())
                .dni(usuario.getDni())
                .nombresUsuario(usuario.getNombresUsuario())
                .apellidosUsuario(usuario.getApellidosUsuario())
                .estadoUsuario(usuario.getEstadoUsuario())
                .build();
    }
}