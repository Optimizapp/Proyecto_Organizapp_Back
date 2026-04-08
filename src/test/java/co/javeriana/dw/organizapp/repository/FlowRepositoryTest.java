package co.javeriana.dw.organizapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;
@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class FlowRepositoryTest {

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ProcessVersionRepository processVersionRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;

    @BeforeEach
    void setup() {
        company = buildCompany();
    }

    @Test
    void shouldReturnFlowsByVersionId() {
        ProcessVersion version = buildVersion();

        buildFlow(version);
        buildFlow(version);

        List<Flow> result = flowRepository.findByVersionId(version.getId());

        assertEquals(2, result.size());
    }

    @Test
    void shouldNotReturnFlowsFromOtherVersions() {
        ProcessVersion version1 = buildVersion();
        ProcessVersion version2 = buildVersion();

        buildFlow(version1);
        buildFlow(version2);

        List<Flow> result = flowRepository.findByVersionId(version1.getId());

        assertEquals(1, result.size());
    }


    private Company buildCompany() {
        Company persistedCompany = new Company();
        persistedCompany.setName("Company " + Math.random());
        persistedCompany.setNit(String.valueOf(System.nanoTime()));
        persistedCompany.setIndustry("Tech");
        return companyRepository.save(persistedCompany);
    }

    private User buildUser() {
        User user = new User();
        user.setName("Juan");
        user.setEmail("user" + Math.random() + "@test.com");
        user.setCompany(company);
        return userRepository.save(user);
    }

    private Process buildProcess(User user) {
        Process process = new Process();
        process.setName("Test Process " + Math.random());
        process.setCompany(company);
        process.setUser(user);
        return processRepository.save(process);
    }

    private ProcessVersion buildVersion() {
        User user = buildUser();
        Process process = buildProcess(user);

        ProcessVersion version = new ProcessVersion();
        version.setNumeroVersion(1);
        version.setProceso(process);
        version.setCreadoPor(user);

        return processVersionRepository.save(version);
    }

    private Flow buildFlow(ProcessVersion version) {
        Node nodoOrigen = buildNode(version);
        Node nodoDestino = buildNode(version);

        Flow flow = new Flow();
        flow.setVersion(version);
        flow.setNodoOrigen(nodoOrigen);   
        flow.setNodoDestino(nodoDestino); 

        return flowRepository.save(flow);
    }
    private Node buildNode(ProcessVersion version) {
        Node node = new Node();

        node.setVersion(version);

        node.setTipo(NodeType.TAREA); 

        node.setNombre("Nodo " + Math.random());

        node.setDescripcion("Descripción de prueba"); 

        node.setX(100f);
        node.setY(200f);

        return nodeRepository.save(node);
    }
}
