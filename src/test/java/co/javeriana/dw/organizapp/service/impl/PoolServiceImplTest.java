package co.javeriana.dw.organizapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.javeriana.dw.organizapp.dto.CreatePoolRequest;
import co.javeriana.dw.organizapp.dto.PoolResponse;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.LaneRepository;
import co.javeriana.dw.organizapp.repository.PoolRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class PoolServiceImplTest {

    private PoolRepository poolRepository;
    private CompanyRepository companyRepository;
    private LaneRepository laneRepository;
    private PoolServiceImpl poolService;

    @BeforeEach
    void setUp() {
        poolRepository = mock(PoolRepository.class);
        companyRepository = mock(CompanyRepository.class);
        laneRepository = mock(LaneRepository.class);
        poolService = new PoolServiceImpl(poolRepository, companyRepository, laneRepository, new ModelMapper());
    }

    @Test
    void createPoolReturnsResponseWhenRequestIsValid() {
        Company company = company(1L);
        CreatePoolRequest request = createPoolRequest("Pool principal", 1L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(poolRepository.existsByCompanyIdAndName(1L, "Pool principal")).thenReturn(false);
        when(poolRepository.save(any(Pool.class))).thenAnswer(invocation -> {
            Pool pool = invocation.getArgument(0);
            pool.setId(10L);
            return pool;
        });

        PoolResponse response = poolService.create(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Pool principal");
        assertThat(response.getCompanyId()).isEqualTo(1L);
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void createPoolRejectsDuplicatedNameInSameCompany() {
        CreatePoolRequest request = createPoolRequest("Pool principal", 1L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
        when(poolRepository.existsByCompanyIdAndName(1L, "Pool principal")).thenReturn(true);

        assertThatThrownBy(() -> poolService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Ya existe un pool con nombre: Pool principal");
    }

    @Test
    void findAllReturnsPoolsFilteredByCompanyId() {
        Company company = company(1L);
        Pool pool = pool(10L, "Pool principal", company);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(poolRepository.findByCompanyId(1L)).thenReturn(List.of(pool));

        List<PoolResponse> response = poolService.findAll(1L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getCompanyId()).isEqualTo(1L);
        assertThat(response.get(0).getName()).isEqualTo("Pool principal");
    }

    @Test
    void createPoolRejectsMissingCompany() {
        CreatePoolRequest request = createPoolRequest("Pool principal", 99L);
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poolService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Empresa no encontrada con ID: 99");
    }

    private static CreatePoolRequest createPoolRequest(String name, Long companyId) {
        CreatePoolRequest request = new CreatePoolRequest();
        request.setName(name);
        request.setDescription("Pool visual");
        request.setCompanyId(companyId);
        return request;
    }

    private static Company company(Long id) {
        Company company = new Company();
        company.setId(id);
        company.setName("Company " + id);
        return company;
    }

    private static Pool pool(Long id, String name, Company company) {
        Pool pool = new Pool();
        pool.setId(id);
        pool.setName(name);
        pool.setActive(true);
        pool.setCompany(company);
        return pool;
    }
}
