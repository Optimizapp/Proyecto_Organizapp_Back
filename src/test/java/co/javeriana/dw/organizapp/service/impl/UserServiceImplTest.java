package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.UserRequestDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindAll() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);
        dto.setName("Test User");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(dto);

        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
    }

    @Test
    void testFindByIdSuccess() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);
        dto.setName("Test User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(dto);

        UserResponseDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testCreate() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Test User");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Test User");

        when(modelMapper.map(any(UserRequestDto.class), eq(User.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(responseDto);

        UserResponseDto result = userService.create(requestDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository, times(1)).delete(any(User.class));
    }
}
