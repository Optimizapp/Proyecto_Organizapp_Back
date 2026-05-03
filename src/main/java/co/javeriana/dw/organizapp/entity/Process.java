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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    name = "processes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_processes_company_id_nombre", columnNames = {"company_id", "name"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proceso es obligatorio")
    @Size(max = 150, message = "El nombre del proceso no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    @Column(length = 1000)
    private String description;

    @Size(max = 100, message = "La categoria no puede superar los 100 caracteres")
    @Column(length = 100)
    private String category;

    @NotNull(message = "El estado del proceso es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProcessStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Se usa LAZY para evitar cargar la empresa en cada consulta del proceso.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Se usa LAZY para evitar cargar el usuario en cada consulta del proceso.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_pool_id")
    private Pool mainPool;

    @ToString.Exclude
    @OneToMany(mappedBy = "proceso", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<ProcessVersion> versiones = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "proceso", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }

        if (status == null) {
            status = ProcessStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addVersion(ProcessVersion version) {
        versiones.add(version);
        version.setProceso(this);
    }

    public void removeVersion(ProcessVersion version) {
        versiones.remove(version);
        version.setProceso(null);
    }

    public void addRole(Role role) {
        roles.add(role);
        role.setProceso(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.setProceso(null);
    }
}
