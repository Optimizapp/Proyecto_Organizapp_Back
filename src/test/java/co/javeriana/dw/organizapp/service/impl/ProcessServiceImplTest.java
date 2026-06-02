package co.javeriana.dw.organizapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.javeriana.dw.organizapp.dto.CreateProcessRequest;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessStatus;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.PoolRepository;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class ProcessServiceImplTest {

    private ProcessRepository processRepository;
    private CompanyRepository companyRepository;
    private UserRepository userRepository;
    private PoolRepository poolRepository;
    private ProcessServiceImpl processService;

    @BeforeEach
    void setUp() {
        processRepository = mock(ProcessRepository.class);
        companyRepository = mock(CompanyRepository.class);
        userRepository = mock(UserRepository.class);
        poolRepository = mock(PoolRepository.class);
        processService = new ProcessServiceImpl(
                processRepository,
                companyRepository,
                userRepository,
                poolRepository,
                new ModelMapper());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("companyId", 1L);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void createProcessReturnsMainPoolWhenMainPoolIsValid() {
        Company company = company(1L);
        User user = user(10L);
        Pool pool = pool(20L, company);
        CreateProcessRequest request = createProcessRequest(1L, 10L, 20L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(poolRepository.findById(20L)).thenReturn(Optional.of(pool));
        when(processRepository.existsByCompanyIdAndName(1L, "Onboarding")).thenReturn(false);
        when(processRepository.save(any(Process.class))).thenAnswer(invocation -> {
            Process process = invocation.getArgument(0);
            process.setId(30L);
            return process;
        });

        ProcessResponseDto response = processService.create(request);

        assertThat(response.getId()).isEqualTo(30L);
        assertThat(response.getMainPoolId()).isEqualTo(20L);
        assertThat(response.getCompanyId()).isEqualTo(1L);
    }

    @Test
    void createProcessRejectsMainPoolFromAnotherCompany() {
        Company processCompany = company(1L);
        Company poolCompany = company(2L);
        CreateProcessRequest request = createProcessRequest(1L, 10L, 20L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(processCompany));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user(10L)));
        when(poolRepository.findById(20L)).thenReturn(Optional.of(pool(20L, poolCompany)));

        assertThatThrownBy(() -> processService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El pool principal no pertenece a la empresa indicada");
    }

    @Test
    void createProcessRejectsUserFromAnotherCompany() {
        Company processCompany = company(1L);
        User user = user(10L);
        user.setCompany(company(2L));
        CreateProcessRequest request = createProcessRequest(1L, 10L, null);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(processCompany));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> processService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El usuario responsable no pertenece a la empresa indicada");
    }

    @Test
    void updateProcessRejectsUserFromAnotherCompany() {
        Company processCompany = company(1L);
        User user = user(10L);
        user.setCompany(company(2L));
        Process existingProcess = new Process();
        existingProcess.setId(30L);
        existingProcess.setName("Onboarding");
        existingProcess.setCompany(processCompany);
        CreateProcessRequest createRequest = createProcessRequest(1L, 10L, null);
        co.javeriana.dw.organizapp.dto.UpdateProcessRequest request =
                new co.javeriana.dw.organizapp.dto.UpdateProcessRequest();
        request.setName(createRequest.getName());
        request.setDescription(createRequest.getDescription());
        request.setCategory(createRequest.getCategory());
        request.setStatus(createRequest.getStatus());
        request.setCompanyId(createRequest.getCompanyId());
        request.setUserId(createRequest.getUserId());
        request.setMainPoolId(createRequest.getMainPoolId());
        when(processRepository.findById(30L)).thenReturn(Optional.of(existingProcess));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(processCompany));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> processService.update(30L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El usuario responsable no pertenece a la empresa indicada");
    }

    private static CreateProcessRequest createProcessRequest(Long companyId, Long userId, Long mainPoolId) {
        CreateProcessRequest request = new CreateProcessRequest();
        request.setName("Onboarding");
        request.setDescription("Proceso de ingreso");
        request.setCategory("HR");
        request.setStatus("DRAFT");
        request.setCompanyId(companyId);
        request.setUserId(userId);
        request.setMainPoolId(mainPoolId);
        return request;
    }

    private static Company company(Long id) {
        Company company = new Company();
        company.setId(id);
        return company;
    }

    private static User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setCompany(company(1L));
        return user;
    }

    private static Pool pool(Long id, Company company) {
        Pool pool = new Pool();
        pool.setId(id);
        pool.setCompany(company);
        return pool;
    }
}
