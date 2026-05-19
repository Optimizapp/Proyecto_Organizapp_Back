package co.javeriana.dw.organizapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class RoleServiceImplTest {

    private RoleRepository roleRepository;
    private CompanyRepository companyRepository;
    private ProcessRepository processRepository;
    private UserRepository userRepository;
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        companyRepository = mock(CompanyRepository.class);
        processRepository = mock(ProcessRepository.class);
        userRepository = mock(UserRepository.class);
        roleService = new RoleServiceImpl(
                roleRepository,
                companyRepository,
                processRepository,
                userRepository,
                new ModelMapper());
    }

    @Test
    void createCompanyRoleReturnsResponseWhenRequestIsValid() {
        Company company = company(1L);
        RoleRequestDto request = roleRequest("AUDITOR", 1L, null);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(roleRepository.existsByCompanyIdAndProcesoIsNullAndNombre(1L, "AUDITOR")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(10L);
            return role;
        });

        RoleResponseDto response = roleService.create(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getNombre()).isEqualTo("AUDITOR");
        assertThat(response.getCompanyId()).isEqualTo(1L);
        assertThat(response.getProcessId()).isNull();
    }

    @Test
    void createCompanyRoleRejectsDuplicatedNameInSameCompanyScope() {
        RoleRequestDto request = roleRequest("ADMIN", 1L, null);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
        when(roleRepository.existsByCompanyIdAndProcesoIsNullAndNombre(1L, "ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> roleService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Ya existe un rol con nombre: ADMIN");
    }

    @Test
    void createRoleRejectsMissingCompany() {
        RoleRequestDto request = roleRequest("AUDITOR", 99L, null);
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Empresa no encontrada con ID: 99");
    }

    @Test
    void createProcessRoleRejectsProcessFromAnotherCompany() {
        Company targetCompany = company(1L);
        Process process = new Process();
        process.setId(20L);
        process.setCompany(company(2L));
        RoleRequestDto request = roleRequest("PROCESS_EDITOR", 1L, 20L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(targetCompany));
        when(processRepository.findById(20L)).thenReturn(Optional.of(process));

        assertThatThrownBy(() -> roleService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El proceso no pertenece a la empresa indicada");
    }

    private static Company company(Long id) {
        Company company = new Company();
        company.setId(id);
        company.setName("Company " + id);
        return company;
    }

    private static RoleRequestDto roleRequest(String name, Long companyId, Long processId) {
        RoleRequestDto request = new RoleRequestDto();
        request.setNombre(name);
        request.setDescripcion("Role " + name);
        request.setCompanyId(companyId);
        request.setProcessId(processId);
        return request;
    }
}
