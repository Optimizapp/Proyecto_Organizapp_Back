package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.UserRequestDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll_shouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        verify(userRepository).findAll();
    }

    @Test
    void findById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(new User()));

        UserResponseDto result = userService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create_shouldSaveUser() {
        UserRequestDto request = new UserRequestDto();

        when(userRepository.save(any())).thenReturn(new User());

        UserResponseDto result = userService.create(request);

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void delete_shouldCallRepository() {
        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}
