package co.javeriana.dw.organizapp.entity;

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
import jakarta.validation.constraints.PositiveOrZero;
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
    name = "lanes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_lanes_pool_id_name", columnNames = {"pool_id", "name"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la lane es obligatorio")
    @Size(max = 150, message = "El nombre de la lane no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    @Column(length = 1000)
    private String description;

    @PositiveOrZero(message = "La posicion debe ser positiva o cero")
    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(nullable = false)
    private Boolean active = true;

    @NotNull(message = "El pool asociado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @ToString.Exclude
    @OneToMany(mappedBy = "lane")
    private Set<Node> nodes = new HashSet<>();
}
