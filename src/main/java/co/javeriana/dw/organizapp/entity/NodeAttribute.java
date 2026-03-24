package co.javeriana.dw.organizapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "node_attributes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_node_attributes_node_id_clave", columnNames = {"node_id", "clave"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El nodo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_id", nullable = false)
    private Node nodo;

    @NotBlank(message = "La clave del atributo es obligatoria")
    @Size(max = 100, message = "La clave del atributo no puede superar los 100 caracteres")
    @Column(name = "clave", nullable = false, length = 100)
    private String clave;

    @Size(max = 2000, message = "El valor del atributo no puede superar los 2000 caracteres")
    @Column(name = "valor", length = 2000)
    private String valor;

    @NotNull(message = "El tipo del atributo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private NodeAttributeType tipo;
}
