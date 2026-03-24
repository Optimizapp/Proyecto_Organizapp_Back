package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.NodeAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeAttributeRepository extends JpaRepository<NodeAttribute, Long> {
}
