package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.dto.CreateCompanyRequest;
import co.javeriana.dw.organizapp.dto.PoolResponse;
import co.javeriana.dw.organizapp.dto.RegisterCompanyRequest;
import co.javeriana.dw.organizapp.dto.RegisterCompanyResponse;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.dto.UpdateCompanyRequest;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.PoolRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.BaseRoleNames;
import co.javeriana.dw.organizapp.service.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PoolRepository poolRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CompanyServiceImpl(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PoolRepository poolRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.poolRepository = poolRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<CompanyResponseDto> findAll() {
        return companyRepository.findAll().stream()
                .map(company -> modelMapper.map(company, CompanyResponseDto.class))
                .toList();
    }

    @Override
    public CompanyResponseDto findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        return modelMapper.map(company, CompanyResponseDto.class);
    }

    @Override
    @Transactional
    public RegisterCompanyResponse register(RegisterCompanyRequest request) {
        validateCompanyRegistrationAvailable(request);

        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setNit(request.getNit());
        company.setIndustry(request.getIndustry());
        company.setContactEmail(request.getContactEmail());
        Company savedCompany = companyRepository.save(company);

        List<Role> roles = createBaseCompanyRoles(savedCompany);
        Pool defaultPool = createDefaultPool(savedCompany);
        Role adminRole = roles.stream()
                .filter(role -> BaseRoleNames.ADMIN.equals(role.getNombre()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("No se pudo crear el rol administrador"));

        User admin = new User();
        admin.setName(request.getAdminName());
        admin.setEmail(request.getAdminEmail());
        admin.setContrasenaHash(passwordEncoder.encode(request.getAdminPassword()));
        admin.setActivo(true);
        admin.setCompany(savedCompany);
        admin.setRol(adminRole);
        User savedAdmin = userRepository.save(admin);

        return new RegisterCompanyResponse(
                toCompanyResponse(savedCompany),
                toUserResponse(savedAdmin),
                roles.stream().map(this::toRoleResponse).toList(),
                toPoolResponse(defaultPool));
    }

    @Override
    public CompanyResponseDto create(CreateCompanyRequest companyDto) {
        validateDuplicatesForCreate(companyDto);
        
        // Mapeo de DTO de entrada a Entidad
        Company company = modelMapper.map(companyDto, Company.class);
        Company savedCompany = companyRepository.save(company);
        
        // Mapeo de Entidad persistida a DTO de respuesta
        return toCompanyResponse(savedCompany);
    }

    @Override
    public CompanyResponseDto update(Long id, UpdateCompanyRequest companyDto) {
        // Buscamos la entidad real primero
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        
        validateDuplicatesForUpdate(existingCompany, companyDto);
        
        // Actualizamos los campos de la entidad existente con los del DTO
        modelMapper.map(companyDto, existingCompany);
        
        Company updatedCompany = companyRepository.save(existingCompany);
        return toCompanyResponse(updatedCompany);
    }

    @Override
    public void delete(Long id) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        companyRepository.delete(existingCompany);
    }

    // --- Métodos de validación ajustados para DTOs ---

    private void validateDuplicatesForCreate(CreateCompanyRequest dto) {
        if (companyRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Ya existe una empresa con nombre: " + dto.getName());
        }
        if (companyRepository.existsByNit(dto.getNit())) {
            throw new DuplicateResourceException("Ya existe una empresa con NIT: " + dto.getNit());
        }
    }

    private void validateCompanyRegistrationAvailable(RegisterCompanyRequest request) {
        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new DuplicateResourceException("Ya existe una empresa con nombre: " + request.getCompanyName());
        }
        if (companyRepository.existsByNit(request.getNit())) {
            throw new DuplicateResourceException("Ya existe una empresa con NIT: " + request.getNit());
        }
        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new DuplicateResourceException("Ya existe un usuario con correo: " + request.getAdminEmail());
        }
    }

    private List<Role> createBaseCompanyRoles(Company company) {
        List<Role> roles = new ArrayList<>();
        roles.add(createCompanyRole(company, BaseRoleNames.ADMIN, "Administrador de empresa"));
        roles.add(createCompanyRole(company, BaseRoleNames.EDITOR, "Editor de procesos"));
        roles.add(createCompanyRole(company, BaseRoleNames.READ_ONLY, "Usuario de solo lectura"));
        return roleRepository.saveAll(roles);
    }

    private Pool createDefaultPool(Company company) {
        String defaultPoolName = company.getName();
        if (poolRepository.existsByCompanyIdAndName(company.getId(), defaultPoolName)) {
            throw new DuplicateResourceException("Ya existe un pool con nombre: " + defaultPoolName);
        }

        Pool pool = new Pool();
        pool.setCompany(company);
        pool.setName(defaultPoolName);
        pool.setDescription("Pool principal de la empresa");
        pool.setActive(true);
        return poolRepository.save(pool);
    }

    private Role createCompanyRole(Company company, String name, String description) {
        if (roleRepository.existsByCompanyIdAndProcesoIsNullAndNombre(company.getId(), name)) {
            throw new DuplicateResourceException("Ya existe un rol con nombre: " + name);
        }
        Role role = new Role();
        role.setCompany(company);
        role.setNombre(name);
        role.setDescripcion(description);
        return role;
    }

    private CompanyResponseDto toCompanyResponse(Company company) {
        return modelMapper.map(company, CompanyResponseDto.class);
    }

    private UserResponseDto toUserResponse(User user) {
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        dto.setCompanyId(user.getCompany().getId());
        dto.setRoleId(user.getRol().getId());
        dto.setRoleNombre(user.getRol().getNombre());
        dto.setActive(user.getActivo());
        return dto;
    }

    private RoleResponseDto toRoleResponse(Role role) {
        RoleResponseDto dto = modelMapper.map(role, RoleResponseDto.class);
        dto.setProcessId(role.getProceso() == null ? null : role.getProceso().getId());
        dto.setCompanyId(role.getCompany() == null ? null : role.getCompany().getId());
        return dto;
    }

    private PoolResponse toPoolResponse(Pool pool) {
        PoolResponse dto = modelMapper.map(pool, PoolResponse.class);
        dto.setCompanyId(pool.getCompany().getId());
        return dto;
    }

    private void validateDuplicatesForUpdate(Company existingCompany, UpdateCompanyRequest dto) {
        if (!existingCompany.getName().equals(dto.getName()) && companyRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Ya existe una empresa con nombre: " + dto.getName());
        }
        if (!existingCompany.getNit().equals(dto.getNit()) && companyRepository.existsByNit(dto.getNit())) {
            throw new DuplicateResourceException("Ya existe una empresa con NIT: " + dto.getNit());
        }
    }
}
