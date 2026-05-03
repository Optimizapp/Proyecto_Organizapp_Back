package co.javeriana.dw.organizapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Lane;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.ResourceInUseException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.FlowRepository;
import co.javeriana.dw.organizapp.repository.LaneRepository;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class NodeServiceImplTest {

    private NodeRepository nodeRepository;
    private ProcessVersionRepository processVersionRepository;
    private LaneRepository laneRepository;
    private FlowRepository flowRepository;
    private NodeServiceImpl nodeService;

    @BeforeEach
    void setUp() {
        nodeRepository = mock(NodeRepository.class);
        processVersionRepository = mock(ProcessVersionRepository.class);
        laneRepository = mock(LaneRepository.class);
        flowRepository = mock(FlowRepository.class);
        nodeService = new NodeServiceImpl(
                nodeRepository,
                processVersionRepository,
                laneRepository,
                flowRepository,
                new ModelMapper());
    }

    @Test
    void createTaskNodeReturnsResponseWithLaneId() {
        Company company = company(1L);
        Pool pool = pool(10L, company);
        ProcessVersion version = version(1L, process(company, pool));
        Lane lane = lane(10L, pool);
        NodeRequestDto request = nodeRequest("TASK");
        request.setLaneId(10L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(laneRepository.findById(10L)).thenReturn(Optional.of(lane));
        when(nodeRepository.save(any(Node.class))).thenAnswer(invocation -> {
            Node node = invocation.getArgument(0);
            node.setId(20L);
            return node;
        });

        NodeResponseDto response = nodeService.create(request);

        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getTipo()).isEqualTo("TASK");
        assertThat(response.getLaneId()).isEqualTo(10L);
    }

    @Test
    void createGatewayNodeRejectsMissingGatewayType() {
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version(1L, process(company(1L), null))));

        assertThatThrownBy(() -> nodeService.create(nodeRequest("GATEWAY")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("gatewayType es obligatorio");
    }

    @Test
    void createNonGatewayNodeRejectsGatewayType() {
        NodeRequestDto request = nodeRequest("TASK");
        request.setGatewayType("EXCLUSIVE");
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version(1L, process(company(1L), null))));

        assertThatThrownBy(() -> nodeService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("gatewayType solo es permitido");
    }

    @Test
    void createNodeRejectsMissingLane() {
        NodeRequestDto request = nodeRequest("TASK");
        request.setLaneId(99L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version(1L, process(company(1L), null))));
        when(laneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nodeService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lane no encontrada con ID: 99");
    }

    @Test
    void createNodeRejectsLaneFromAnotherCompany() {
        Company processCompany = company(1L);
        Company laneCompany = company(2L);
        Pool processPool = pool(10L, processCompany);
        Pool lanePool = pool(20L, laneCompany);
        ProcessVersion version = version(1L, process(processCompany, processPool));
        Lane lane = lane(30L, lanePool);
        NodeRequestDto request = nodeRequest("TASK");
        request.setLaneId(30L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(laneRepository.findById(30L)).thenReturn(Optional.of(lane));

        assertThatThrownBy(() -> nodeService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("La lane no pertenece a la misma empresa del proceso");
    }

    @Test
    void updateNodeRejectsLaneOutsideMainPool() {
        Company company = company(1L);
        Pool mainPool = pool(10L, company);
        Pool otherPool = pool(20L, company);
        ProcessVersion version = version(1L, process(company, mainPool));
        Lane lane = lane(30L, otherPool);
        Node existingNode = new Node();
        existingNode.setId(40L);
        NodeRequestDto request = nodeRequest("TASK");
        request.setLaneId(30L);
        when(nodeRepository.findById(40L)).thenReturn(Optional.of(existingNode));
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(laneRepository.findById(30L)).thenReturn(Optional.of(lane));

        assertThatThrownBy(() -> nodeService.update(40L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("La lane no pertenece al pool principal del proceso");
    }

    @Test
    void deleteNodeRejectsConnectedFlows() {
        Node node = new Node();
        node.setId(20L);
        when(nodeRepository.findById(20L)).thenReturn(Optional.of(node));
        when(flowRepository.existsByNodoOrigenIdOrNodoDestinoId(20L, 20L)).thenReturn(true);

        assertThatThrownBy(() -> nodeService.delete(20L))
                .isInstanceOf(ResourceInUseException.class)
                .hasMessageContaining("No se puede eliminar el nodo porque tiene flujos conectados");
    }

    private static NodeRequestDto nodeRequest(String type) {
        NodeRequestDto request = new NodeRequestDto();
        request.setVersionId(1L);
        request.setTipo(type);
        request.setNombre("Actividad");
        request.setDescripcion("Actividad visual");
        request.setX(10F);
        request.setY(20F);
        request.setWidth(120F);
        request.setHeight(80F);
        return request;
    }

    private static ProcessVersion version(Long id, Process process) {
        ProcessVersion version = new ProcessVersion();
        version.setId(id);
        version.setProceso(process);
        return version;
    }

    private static Lane lane(Long id, Pool pool) {
        Lane lane = new Lane();
        lane.setId(id);
        lane.setPool(pool);
        return lane;
    }

    private static Company company(Long id) {
        Company company = new Company();
        company.setId(id);
        return company;
    }

    private static Pool pool(Long id, Company company) {
        Pool pool = new Pool();
        pool.setId(id);
        pool.setCompany(company);
        return pool;
    }

    private static Process process(Company company, Pool mainPool) {
        Process process = new Process();
        process.setId(100L);
        process.setCompany(company);
        process.setMainPool(mainPool);
        return process;
    }
}
