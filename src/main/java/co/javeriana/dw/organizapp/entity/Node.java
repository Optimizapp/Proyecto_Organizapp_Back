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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La version del proceso es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private ProcessVersion version;

    @NotNull(message = "El tipo de nodo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NodeType tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway_type", length = 30)
    private GatewayType gatewayType;

    @NotBlank(message = "El nombre del nodo es obligatorio")
    @Size(max = 150, message = "El nombre del nodo no puede superar los 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @NotNull(message = "La coordenada x es obligatoria")
    @PositiveOrZero(message = "La coordenada x debe ser positiva o cero")
    @Column(nullable = false)
    private Float x;

    @NotNull(message = "La coordenada y es obligatoria")
    @PositiveOrZero(message = "La coordenada y debe ser positiva o cero")
    @Column(nullable = false)
    private Float y;

    @PositiveOrZero(message = "El ancho debe ser positivo o cero")
    @Column
    private Float width;

    @PositiveOrZero(message = "El alto debe ser positivo o cero")
    @Column
    private Float height;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lane_id")
    private Lane lane;

    @ToString.Exclude
    @OneToMany(mappedBy = "nodo", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<NodeAttribute> atributos = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "nodoOrigen")
    private Set<Flow> flujosSalientes = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "nodoDestino")
    private Set<Flow> flujosEntrantes = new HashSet<>();

    public void addAtributo(NodeAttribute attribute) {
        atributos.add(attribute);
        attribute.setNodo(this);
    }

    public void removeAtributo(NodeAttribute attribute) {
        atributos.remove(attribute);
        attribute.setNodo(null);
    }
}
