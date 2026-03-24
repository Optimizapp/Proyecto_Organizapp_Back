package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.NodeAttribute;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeAttributeRepository extends JpaRepository<NodeAttribute, Long> {
    List<NodeAttribute> findByNodoId(Long nodeId);
}
