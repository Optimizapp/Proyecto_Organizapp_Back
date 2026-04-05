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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Size(max = 255, message = "La contraseña no puede superar los 255 caracteres")
    @Column(name = "password_hash", length = 255)
    private String contrasenaHash;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "role_id", nullable = true)
    //@NotNull(message = "El rol es obligatorio")
    private Role rol;

    // Un usuario puede ser responsable/creador de muchos procesos.
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private Set<Process> processes = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "creadoPor")
    private Set<ProcessVersion> versionesCreadas = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private Set<Comment> comentarios = new HashSet<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addProcess(Process process) {
        processes.add(process);
        process.setUser(this);
    }

    public void removeProcess(Process process) {
        processes.remove(process);
        process.setUser(null);
    }

    public void addCreatedVersion(ProcessVersion version) {
        versionesCreadas.add(version);
        version.setCreadoPor(this);
    }

    public void removeCreatedVersion(ProcessVersion version) {
        versionesCreadas.remove(version);
        version.setCreadoPor(null);
    }

    public void addComment(Comment comment) {
        comentarios.add(comment);
        comment.setUser(this);
    }

    public void removeComment(Comment comment) {
        comentarios.remove(comment);
        comment.setUser(null);
    }
}
