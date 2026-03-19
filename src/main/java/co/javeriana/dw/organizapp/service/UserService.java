package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.UserRequestDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();
    UserResponseDto findById(Long id);
    UserResponseDto create(UserRequestDto userDto);
    UserResponseDto update(Long id, UserRequestDto userDto);
    void delete(Long id);
}