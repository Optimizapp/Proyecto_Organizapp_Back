package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {
}
