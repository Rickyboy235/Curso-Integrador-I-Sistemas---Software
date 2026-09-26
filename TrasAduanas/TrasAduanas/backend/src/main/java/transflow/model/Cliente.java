package transflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    @EqualsAndHashCode.Include
    private Integer idCliente;

    @NotBlank
    @Size(max = 10)
    @Column(name = "tipo_doc", length = 10, nullable = false)
    private String tipoDoc;

    @NotBlank
    @Size(max = 15)
    @Column(name = "num_doc", length = 15, nullable = false, unique = true)
    private String numDoc;

    @NotBlank
    @Size(max = 150)
    @Column(name = "razon_social", length = 150, nullable = false)
    private String razonSocial;

    @Email
    @Size(max = 100)
    @Column(name = "email_principal", length = 100)
    private String emailPrincipal;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estadoCliente;

    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ContactoCliente> contactos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<DireccionCliente> direcciones = new ArrayList<>();

    /** Valida coherencia basica entre tipoDoc y numDoc (RUC=11 digitos, DNI=8 digitos). */
    public boolean validarDocumento() {
        if (tipoDoc == null || numDoc == null) {
            return false;
        }
        return switch (tipoDoc.trim().toUpperCase()) {
            case "RUC" -> numDoc.matches("\\d{11}");
            case "DNI" -> numDoc.matches("\\d{8}");
            case "CE" -> numDoc.matches("\\d{9,12}");
            default -> !numDoc.isBlank();
        };
    }

    public void addContacto(ContactoCliente contacto) {
        contactos.add(contacto);
        contacto.setCliente(this);
    }

    public void removeContacto(ContactoCliente contacto) {
        contactos.remove(contacto);
        contacto.setCliente(null);
    }

    public void addDireccion(DireccionCliente direccion) {
        direcciones.add(direccion);
        direccion.setCliente(this);
    }

    public void removeDireccion(DireccionCliente direccion) {
        direcciones.remove(direccion);
        direccion.setCliente(null);
    }
}