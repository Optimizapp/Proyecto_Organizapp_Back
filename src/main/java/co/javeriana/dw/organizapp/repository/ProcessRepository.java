package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {

    boolean existsByName(String name);

    boolean existsByCompanyIdAndName(Long companyId, String name);

    List<Process> findByCompanyId(Long companyId);

    List<Process> findByStatus(ProcessStatus status);

    List<Process> findByCompanyIdAndStatus(Long companyId, ProcessStatus status);
}
