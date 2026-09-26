package transflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleValeId implements Serializable {

    @Column(name = "id_vale")
    private Integer idVale;

    @Column(name = "id_insumo")
    private Integer idInsumo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleValeId that)) return false;
        return Objects.equals(idVale, that.idVale) && Objects.equals(idInsumo, that.idInsumo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVale, idInsumo);
    }
}