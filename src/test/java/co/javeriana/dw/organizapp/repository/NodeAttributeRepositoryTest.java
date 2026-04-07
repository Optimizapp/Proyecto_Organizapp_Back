package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.entity.Process;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = ThymeleafApplication.class)
@Transactional
class NodeAttributeRepositoryTest {

    @Autowired
    private NodeAttributeRepository nodeAttributeRepository;

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
    @DisplayName("Debe retornar atributos por id del nodo")
    void shouldReturnAttributesByNodeId() {
        ProcessVersion version = buildVersion();
        Node node = buildNode(version);

        buildNodeAttribute(node, "attr1");
        buildNodeAttribute(node, "attr2");

        List<NodeAttribute> result =
                nodeAttributeRepository.findByNodoId(node.getId());

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("No debe retornar atributos de otros nodos")
    void shouldNotReturnAttributesFromOtherNodes() {
        ProcessVersion version = buildVersion();

        Node node1 = buildNode(version);
        Node node2 = buildNode(version);

        buildNodeAttribute(node1, "attr1");
        buildNodeAttribute(node2, "attr2");

        List<NodeAttribute> result =
                nodeAttributeRepository.findByNodoId(node1.getId());

        assertEquals(1, result.size());
    }

    private NodeAttribute buildNodeAttribute(Node node, String clave) {
        NodeAttribute attr = new NodeAttribute();

        attr.setNodo(node);
        attr.setClave(clave); 
        attr.setValor("valor " + Math.random());
        attr.setTipo(NodeAttributeType.TEXTO); 

        return nodeAttributeRepository.save(attr);
    }

    private Node buildNode(ProcessVersion version) {
        Node node = new Node();

        node.setVersion(version);
        node.setTipo(NodeType.TAREA); // ajusta si tu enum es distinto
        node.setNombre("Nodo " + Math.random());
        node.setDescripcion("desc");
        node.setX(100f);
        node.setY(200f);

        return nodeRepository.save(node);
    }

    private ProcessVersion buildVersion() {
        Process process = buildProcess();
        User user = buildUser();

        ProcessVersion version = new ProcessVersion();

        version.setProceso(process);

        version.setNumeroVersion(1);

        version.setEstado(ProcessVersionStatus.BORRADOR); 
        version.setCreadoPor(user);

        version.setCreatedAt(LocalDateTime.now());

        return versionRepository.save(version);
    }
    private Process buildProcess() {
        Company company = buildCompany();
        User user = buildUser();

        Process process = new Process();
        process.setCompany(company);
        process.setName("Process " + Math.random());
        process.setDescription("desc");
        process.setUser(user);

        return processRepository.save(process);
    }

    private Company buildCompany() {
        Company company = new Company();
        company.setName("Company " + Math.random()); 
        company.setNit(String.valueOf(System.nanoTime()));
        company.setIndustry("Tech");
        return companyRepository.save(company);
    }

    private User buildUser() {
        User user = new User();
        Company company = buildCompany();
        user.setName("Juan");
        user.setEmail("user" + Math.random() + "@test.com");
        user.setCompany(company);
        return userRepository.save(user);
    }
}