package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CreateUserRequest;
import co.javeriana.dw.organizapp.dto.UpdateUserRequest;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();
    UserResponseDto findById(Long id);
    UserResponseDto create(CreateUserRequest userDto);
    UserResponseDto update(Long id, UpdateUserRequest userDto);
    void delete(Long id);
}
