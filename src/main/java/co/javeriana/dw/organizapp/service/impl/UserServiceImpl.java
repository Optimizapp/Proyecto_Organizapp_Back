package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.UserRequestDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.UserService;
import org.modelmapper.ModelMapper;
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

    public UserServiceImpl(UserRepository userRepository,
                           CompanyRepository companyRepository,
                           RoleRepository roleRepository,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
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
    public UserResponseDto create(UserRequestDto userDto) {
        Company company = companyRepository.findById(userDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_MESSAGE + userDto.getCompanyId()));
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE + userDto.getRoleId()));

        User user = modelMapper.map(userDto, User.class);
        user.setCompany(company);
        user.setRol(role);

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + id));

        Company company = companyRepository.findById(userDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_MESSAGE + userDto.getCompanyId()));
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE + userDto.getRoleId()));

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setCompany(company);
        existingUser.setRol(role);

        return convertToDto(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + id));
        userRepository.delete(user);
    }

    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        dto.setCompanyId(user.getCompany().getId());
        dto.setRoleId(user.getRol().getId());
        dto.setRoleNombre(user.getRol().getNombre());
        return dto;
    }
}
