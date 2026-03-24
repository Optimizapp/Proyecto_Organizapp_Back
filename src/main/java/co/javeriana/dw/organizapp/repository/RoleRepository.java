package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByProcesoId(Long processId);
}
