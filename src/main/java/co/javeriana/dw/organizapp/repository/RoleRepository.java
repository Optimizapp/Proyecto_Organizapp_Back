package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByCompanyId(Long companyId);

    List<Role> findByCompanyIdAndProcesoId(Long companyId, Long processId);

    List<Role> findByProcesoId(Long processId);

    boolean existsByProcesoIdAndNombre(Long processId, String nombre);

    boolean existsByCompanyIdAndProcesoIsNullAndNombre(Long companyId, String nombre);

    boolean existsByCompanyIdAndProcesoIdAndNombre(Long companyId, Long processId, String nombre);
}
