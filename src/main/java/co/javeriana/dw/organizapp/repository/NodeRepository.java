package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
}
