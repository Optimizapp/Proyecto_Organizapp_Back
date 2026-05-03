package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Pool;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolRepository extends JpaRepository<Pool, Long> {
    List<Pool> findByCompanyId(Long companyId);

    java.util.Optional<Pool> findFirstByCompanyIdAndActiveTrueOrderByIdAsc(Long companyId);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
