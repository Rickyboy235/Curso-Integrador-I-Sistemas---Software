package transflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transflow.dto.RolDTO;
import transflow.model.Rol;
import transflow.repository.RolRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<RolDTO> listarTodos() {
        return rolRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RolDTO obtenerPorId(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
        return toDTO(rol);
    }

    public RolDTO crear(RolDTO dto) {
        Rol rol = Rol.builder()
                .nombreRol(dto.getNombreRol())
                .descripcion(dto.getDescripcion())
                .build();
        return toDTO(rolRepository.save(rol));
    }

    public RolDTO actualizar(Integer id, RolDTO dto) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));

        rol.setNombreRol(dto.getNombreRol());
        rol.setDescripcion(dto.getDescripcion());

        return toDTO(rolRepository.save(rol));
    }

    public void eliminar(Integer id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con id: " + id);
        }

        try {

            rolRepository.deleteById(id);

        } catch (org.springframework.dao.DataIntegrityViolationException e) {

            throw new RuntimeException(
                    "No se puede eliminar el rol porque hay usuarios asignados a él."
            );
        }
    }

    private RolDTO toDTO(Rol rol) {
        return RolDTO.builder()
                .idRol(rol.getIdRol())
                .nombreRol(rol.getNombreRol())
                .descripcion(rol.getDescripcion())
                .build();
    }
}