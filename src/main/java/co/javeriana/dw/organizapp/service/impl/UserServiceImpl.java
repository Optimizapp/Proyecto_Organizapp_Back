package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CreateUserRequest;
import co.javeriana.dw.organizapp.dto.UpdateUserRequest;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado con ID: ";
    private static final String COMPANY_NOT_FOUND_MESSAGE = "La empresa especificada no existe con ID: ";
    private static final String ROLE_NOT_FOUND_MESSAGE = "El rol especificado no existe con ID: ";

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           CompanyRepository companyRepository,
                           RoleRepository roleRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + id));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto create(CreateUserRequest userDto) {
        validateEmailAvailable(userDto.getEmail());
        Company company = companyRepository.findById(userDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_MESSAGE + userDto.getCompanyId()));
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE + userDto.getRoleId()));
        validateRoleBelongsToCompany(role, company);

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setCompany(company);
        user.setRol(role);
        user.setContrasenaHash(passwordEncoder.encode(userDto.getPassword()));
        user.setActivo(userDto.getActive() == null ? Boolean.TRUE : userDto.getActive());

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UpdateUserRequest userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + id));

        if (!existingUser.getEmail().equals(userDto.getEmail())) {
            validateEmailAvailable(userDto.getEmail());
        }

        Company company = companyRepository.findById(userDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_MESSAGE + userDto.getCompanyId()));
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE + userDto.getRoleId()));
        validateRoleBelongsToCompany(role, company);

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setCompany(company);
        existingUser.setRol(role);
        if (userDto.getActive() != null) {
            existingUser.setActivo(userDto.getActive());
        }

        return convertToDto(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + id));
        user.setActivo(false);
        userRepository.save(user);
    }

    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        dto.setCompanyId(user.getCompany().getId());
        dto.setRoleId(user.getRol().getId());
        dto.setRoleNombre(user.getRol().getNombre());
        dto.setActive(user.getActivo());
        return dto;
    }

    private void validateEmailAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Ya existe un usuario con correo: " + email);
        }
    }

    private void validateRoleBelongsToCompany(Role role, Company company) {
        if (!role.getCompany().getId().equals(company.getId())) {
            throw new BusinessRuleException("El rol no pertenece a la empresa indicada");
        }
    }
}
