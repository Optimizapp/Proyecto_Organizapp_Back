package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.ProcessVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessVersionRepository extends JpaRepository<ProcessVersion, Long> {
    List<ProcessVersion> findByProcesoId(Long processId);

    boolean existsByProcesoIdAndNumeroVersion(Long processId, Integer numeroVersion);
}
