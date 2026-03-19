package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {
    // Ejemplo de método útil para el futuro: buscar procesos de una empresa específica
    List<Process> findByCompanyId(Long companyId);
}