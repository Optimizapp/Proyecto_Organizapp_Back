package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.UserRequestDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, 
                           CompanyRepository companyRepository, 
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto userDto) {
        // 1. Validar que la empresa existe
        Company company = companyRepository.findById(userDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("La empresa especificada no existe"));

        // 2. Mapear DTO a Entidad
        User user = modelMapper.map(userDto, User.class);
        user.setCompany(company); // Seteamos la relación manualmente

        // 3. Guardar y retornar
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Company company = companyRepository.findById(userDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("La empresa no existe"));

        // Actualizamos los campos
        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRole(userDto.getRole());
        existingUser.setCompany(company);

        return convertToDto(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    // Método privado para centralizar el mapeo de salida
    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        dto.setCompanyId(user.getCompany().getId()); // Aseguramos que solo vaya el ID
        return dto;
    }
}