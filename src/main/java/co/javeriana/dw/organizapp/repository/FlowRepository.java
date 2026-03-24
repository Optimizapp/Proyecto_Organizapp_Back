package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Flow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {
    List<Flow> findByVersionId(Long versionId);
}
