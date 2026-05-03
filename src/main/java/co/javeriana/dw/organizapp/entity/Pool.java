package co.javeriana.dw.organizapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "pools",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_pools_company_id_name", columnNames = {"company_id", "name"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del pool es obligatorio")
    @Size(max = 150, message = "El nombre del pool no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @NotNull(message = "La empresa asociada es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ToString.Exclude
    @OneToMany(mappedBy = "pool", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lane> lanes = new HashSet<>();

    public void addLane(Lane lane) {
        lanes.add(lane);
        lane.setPool(this);
    }

    public void removeLane(Lane lane) {
        lanes.remove(lane);
        lane.setPool(null);
    }
}
