package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldReturnRolesByProcessId() {
        Process process = buildProcess();

        buildRole(process, "ADMIN");
        buildRole(process, "USER");

        List<Role> result = roleRepository.findByProcesoId(process.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoRoles() {
        Process process = buildProcess();

        List<Role> result = roleRepository.findByProcesoId(process.getId());

        assertTrue(result.isEmpty());
    }

    // ========================
    // BUILDERS
    // ========================

    private Role buildRole(Process process, String nombre) {
        Role role = new Role();

        role.setNombre(nombre);
        role.setProceso(process);

        return roleRepository.save(role);
    }

    private Process buildProcess() {
        Company company = buildCompany();
        User user = buildUser(company);

        Process process = new Process();
        process.setName("Proceso " + System.nanoTime());
        process.setStatus(ProcessStatus.DRAFT);
        process.setCompany(company);
        process.setUser(user);

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