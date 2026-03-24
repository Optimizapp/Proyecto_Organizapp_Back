package co.javeriana.dw.organizapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "flows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La version del proceso es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private ProcessVersion version;

    @NotNull(message = "El nodo origen es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origin_node_id", nullable = false)
    private Node nodoOrigen;

    @NotNull(message = "El nodo destino es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_node_id", nullable = false)
    private Node nodoDestino;

    @Size(max = 500, message = "La condicion no puede superar los 500 caracteres")
    @Column(name = "condicion", length = 500)
    private String condicion;

    @Size(max = 100, message = "La etiqueta no puede superar los 100 caracteres")
    @Column(name = "etiqueta", length = 100)
    private String etiqueta;
}
