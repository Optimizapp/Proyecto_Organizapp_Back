package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {

    boolean existsByName(String name);
}
