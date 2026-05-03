package co.javeriana.dw.organizapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.javeriana.dw.organizapp.dto.CreateLaneRequest;
import co.javeriana.dw.organizapp.dto.LaneResponse;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Lane;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.LaneRepository;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import co.javeriana.dw.organizapp.repository.PoolRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class LaneServiceImplTest {

    private LaneRepository laneRepository;
    private PoolRepository poolRepository;
    private NodeRepository nodeRepository;
    private LaneServiceImpl laneService;

    @BeforeEach
    void setUp() {
        laneRepository = mock(LaneRepository.class);
        poolRepository = mock(PoolRepository.class);
        nodeRepository = mock(NodeRepository.class);
        laneService = new LaneServiceImpl(laneRepository, poolRepository, nodeRepository, new ModelMapper());
    }

    @Test
    void createLaneReturnsResponseWhenRequestIsValid() {
        Pool pool = pool(10L);
        CreateLaneRequest request = createLaneRequest("Administracion", 10L);
        when(poolRepository.findById(10L)).thenReturn(Optional.of(pool));
        when(laneRepository.existsByPoolIdAndName(10L, "Administracion")).thenReturn(false);
        when(laneRepository.save(any(Lane.class))).thenAnswer(invocation -> {
            Lane lane = invocation.getArgument(0);
            lane.setId(20L);
            return lane;
        });

        LaneResponse response = laneService.create(request);

        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getName()).isEqualTo("Administracion");
        assertThat(response.getPoolId()).isEqualTo(10L);
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void createLaneRejectsDuplicatedNameInSamePool() {
        CreateLaneRequest request = createLaneRequest("Administracion", 10L);
        when(poolRepository.findById(10L)).thenReturn(Optional.of(pool(10L)));
        when(laneRepository.existsByPoolIdAndName(10L, "Administracion")).thenReturn(true);

        assertThatThrownBy(() -> laneService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Ya existe una lane con nombre: Administracion");
    }

    @Test
    void findAllReturnsLanesFilteredByPoolId() {
        Pool pool = pool(10L);
        Lane lane = lane(20L, "Administracion", pool);
        when(poolRepository.findById(10L)).thenReturn(Optional.of(pool));
        when(laneRepository.findByPoolId(10L)).thenReturn(List.of(lane));

        List<LaneResponse> response = laneService.findAll(10L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getPoolId()).isEqualTo(10L);
        assertThat(response.get(0).getName()).isEqualTo("Administracion");
    }

    @Test
    void createLaneRejectsMissingPool() {
        CreateLaneRequest request = createLaneRequest("Administracion", 99L);
        when(poolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> laneService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pool no encontrado con ID: 99");
    }

    @Test
    void deleteLaneRejectsLaneWithAssociatedNodes() {
        Lane lane = lane(20L, "Administracion", pool(10L));
        when(laneRepository.findById(20L)).thenReturn(Optional.of(lane));
        when(nodeRepository.existsByLaneId(20L)).thenReturn(true);

        assertThatThrownBy(() -> laneService.delete(20L))
                .isInstanceOf(co.javeriana.dw.organizapp.exception.ResourceInUseException.class)
                .hasMessageContaining("No se puede eliminar la lane porque tiene nodos asociados");
    }

    private static CreateLaneRequest createLaneRequest(String name, Long poolId) {
        CreateLaneRequest request = new CreateLaneRequest();
        request.setName(name);
        request.setDescription("Lane visual");
        request.setOrderIndex(0);
        request.setPoolId(poolId);
        return request;
    }

    private static Pool pool(Long id) {
        Company company = new Company();
        company.setId(1L);
        Pool pool = new Pool();
        pool.setId(id);
        pool.setName("Pool principal");
        pool.setActive(true);
        pool.setCompany(company);
        return pool;
    }

    private static Lane lane(Long id, String name, Pool pool) {
        Lane lane = new Lane();
        lane.setId(id);
        lane.setName(name);
        lane.setActive(true);
        lane.setPool(pool);
        return lane;
    }
}
