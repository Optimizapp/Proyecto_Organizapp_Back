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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "process_versions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_process_versions_process_id_numero_version", columnNames = {"process_id", "numero_version"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El proceso es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    private Process proceso;

    @NotNull(message = "El numero de version es obligatorio")
    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @NotNull(message = "El estado de la version es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private ProcessVersionStatus estado;

    @NotNull(message = "El creador de la version es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User creadoPor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "version", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<Node> nodos = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "version", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<Flow> flujos = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "version", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comentarios = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (estado == null) {
            estado = ProcessVersionStatus.BORRADOR;
        }
    }

    public void addNodo(Node node) {
        nodos.add(node);
        node.setVersion(this);
    }

    public void removeNodo(Node node) {
        nodos.remove(node);
        node.setVersion(null);
    }

    public void addFlujo(Flow flow) {
        flujos.add(flow);
        flow.setVersion(this);
    }

    public void removeFlujo(Flow flow) {
        flujos.remove(flow);
        flow.setVersion(null);
    }

    public void addComentario(Comment comment) {
        comentarios.add(comment);
        comment.setVersion(this);
    }

    public void removeComentario(Comment comment) {
        comentarios.remove(comment);
        comment.setVersion(null);
    }
}
