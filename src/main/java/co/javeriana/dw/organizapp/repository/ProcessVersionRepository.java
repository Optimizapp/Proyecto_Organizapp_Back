package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.ProcessVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessVersionRepository extends JpaRepository<ProcessVersion, Long> {
}
