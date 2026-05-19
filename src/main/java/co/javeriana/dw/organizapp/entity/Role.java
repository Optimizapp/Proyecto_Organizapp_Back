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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "roles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_roles_company_process_nombre", columnNames = {"company_id", "process_id", "nombre"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 100, message = "El nombre del rol no puede superar los 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 255, message = "La descripcion no puede superar los 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "process_id", nullable = true)
    private Process proceso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ToString.Exclude
    @OneToMany(mappedBy = "rol", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<Permission> permisos = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "rol")
    private Set<User> usuarios = new HashSet<>();

    public void addPermiso(Permission permiso) {
        permisos.add(permiso);
        permiso.setRol(this);
    }

    public void removePermiso(Permission permiso) {
        permisos.remove(permiso);
        permiso.setRol(null);
    }

    public void addUsuario(User usuario) {
        usuarios.add(usuario);
        usuario.setRol(this);
    }

    public void removeUsuario(User usuario) {
        usuarios.remove(usuario);
        usuario.setRol(null);
    }
}
