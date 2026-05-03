package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Permission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByRolId(Long roleId);

    boolean existsByRolIdAndCodigo(Long roleId, String codigo);
}
