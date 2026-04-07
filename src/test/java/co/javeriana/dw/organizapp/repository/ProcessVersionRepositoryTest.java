package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.entity.Process;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class ProcessVersionRepositoryTest {

    @Autowired
    private ProcessVersionRepository processVersionRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

@Test
void shouldReturnVersionsByProcessId() {
    Process process = buildProcess();
    User user = buildUser(process.getCompany()); 

    buildVersion(process, user, 1);
    buildVersion(process, user, 2);

    List<ProcessVersion> result =
            processVersionRepository.findByProcesoId(process.getId());

    assertNotNull(result);
    assertEquals(2, result.size());
}

    @Test
    void shouldReturnEmptyListWhenNoVersions() {
        Process process = buildProcess();

        List<ProcessVersion> result =
                processVersionRepository.findByProcesoId(process.getId());

        assertTrue(result.isEmpty());
    }
    private ProcessVersion buildVersion(Process process, User user, int numero) {
        ProcessVersion version = new ProcessVersion();

        version.setProceso(process);
        version.setNumeroVersion(numero);
        version.setEstado(ProcessVersionStatus.BORRADOR);
        version.setCreadoPor(user); 

        return processVersionRepository.save(version);
    }
    private Process buildProcess() {
        Company company = buildCompany();
        User user = buildUser(company);

        Process process = new Process();
        process.setName("Proceso " + System.nanoTime());
        process.setStatus(ProcessStatus.DRAFT);
        process.setCompany(company);
        process.setUser(user);
        process.setCreatedAt(LocalDateTime.now());
        process.setUpdatedAt(LocalDateTime.now());

        return processRepository.save(process);
    }

    private Company buildCompany() {
        Company company = new Company();
        company.setName("Company " + System.nanoTime());
        company.setNit("NIT-" + System.nanoTime()); // 🔥 evita duplicados
        company.setIndustry("Tech");

        return companyRepository.save(company);
    }

    private User buildUser(Company company) {
        User user = new User();
        user.setName("User " + System.nanoTime());
        user.setEmail("user" + System.nanoTime() + "@test.com");
        user.setCompany(company);

        return userRepository.save(user);
    }
}