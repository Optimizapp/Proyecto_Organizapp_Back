package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldReturnPermissionsByRoleId() {
        Role role = buildRole();

        buildPermission(role,"Read");
        buildPermission(role,"Write");

        List<Permission> result = permissionRepository.findByRolId(role.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    private Permission buildPermission(Role role, String codigo) {
        Permission permission = new Permission();

        permission.setCodigo(codigo);
        permission.setDescripcion("Permiso " + codigo);
        permission.setRol(role);

        return permissionRepository.save(permission);
    }

    private Role buildRole() {
        Process process = buildProcess();

        Role role = new Role();
        role.setNombre("ADMIN_" + Math.random());
        role.setProceso(process);

        return roleRepository.save(role);
    }

    private Process buildProcess() {
        Company company = buildCompany();
        User user = buildUser();

        Process process = new Process();
        process.setName("Proceso " + Math.random());
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
        company.setNit(String.valueOf((int)(Math.random() * 1000000))); // ✅ corto
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