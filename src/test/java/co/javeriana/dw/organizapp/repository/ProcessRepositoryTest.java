package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class ProcessRepositoryTest {

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByNameShouldReturnTrue() {
        String name = "ProcesoTest";

        buildProcess(name);

        boolean exists = processRepository.existsByName(name);

        assertTrue(exists);
    }

    @Test
    void existsByNameShouldReturnFalse() {
        boolean exists = processRepository.existsByName("NoExiste");

        assertFalse(exists);
    }

 
    private Process buildProcess(String name) {
        Company company = buildCompany();
        User user = buildUser();

        Process process = new Process();
        process.setName(name);
        process.setStatus(ProcessStatus.DRAFT); 
        process.setCompany(company);
        process.setUser(user);
        process.setCreatedAt(LocalDateTime.now());
        process.setUpdatedAt(LocalDateTime.now());

        return processRepository.save(process);
    }

    private Company buildCompany() {
        Company company = new Company();
        company.setName("Company " + Math.random());
        company.setNit(String.valueOf(Math.random()));
        company.setIndustry("Tech");

        return companyRepository.save(company);
    }

    private User buildUser() {
        Company company = buildCompany();

        User user = new User();
        user.setName("User " + Math.random());
        user.setEmail("user" + Math.random() + "@test.com");
        user.setCompany(company);

        return userRepository.save(user);
    }
}