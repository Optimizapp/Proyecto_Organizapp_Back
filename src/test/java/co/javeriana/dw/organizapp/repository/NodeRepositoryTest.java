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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class NodeRepositoryTest {

    private static final Random RANDOM = new Random();

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ProcessVersionRepository versionRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldReturnNodesByVersionId() {
        ProcessVersion version = buildVersion();

        buildNode(version);
        buildNode(version);

        List<Node> result = nodeRepository.findByVersionId(version.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    private Node buildNode(ProcessVersion version) {
        Node node = new Node();
        node.setVersion(version);
        node.setTipo(NodeType.TAREA);
        node.setNombre("Nodo " + Math.random());
        node.setDescripcion("Descripción");
        node.setX(10f);
        node.setY(20f);

        return nodeRepository.save(node);
    }

    private ProcessVersion buildVersion() {
        Process process = buildProcess();
        User user = buildUser();

        ProcessVersion version = new ProcessVersion();
        version.setProceso(process);
        version.setNumeroVersion(RANDOM.nextInt(1000));
        version.setEstado(ProcessVersionStatus.BORRADOR);
        version.setCreadoPor(user);
        version.setCreatedAt(LocalDateTime.now());

        return versionRepository.save(version);
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
