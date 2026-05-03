package co.javeriana.dw.organizapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
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
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar los 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String nit;

    @Size(max = 100, message = "La industria no puede superar los 100 caracteres")
    @Column(length = 100)
    private String industry;

    @Email(message = "El correo de contacto debe tener un formato valido")
    @Size(max = 150, message = "El correo de contacto no puede superar los 150 caracteres")
    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Una empresa puede tener muchos usuarios asociados.
    @ToString.Exclude
    @OneToMany(mappedBy = "company")
    private Set<User> users = new HashSet<>();

    // Una empresa puede definir muchos procesos organizacionales.
    @ToString.Exclude
    @OneToMany(mappedBy = "company")
    private Set<Process> processes = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "company")
    private Set<Pool> pools = new HashSet<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addUser(User user) {
        users.add(user);
        user.setCompany(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setCompany(null);
    }

    public void addProcess(Process process) {
        processes.add(process);
        process.setCompany(this);
    }

    public void removeProcess(Process process) {
        processes.remove(process);
        process.setCompany(null);
    }

    public void addPool(Pool pool) {
        pools.add(pool);
        pool.setCompany(this);
    }

    public void removePool(Pool pool) {
        pools.remove(pool);
        pool.setCompany(null);
    }
}
