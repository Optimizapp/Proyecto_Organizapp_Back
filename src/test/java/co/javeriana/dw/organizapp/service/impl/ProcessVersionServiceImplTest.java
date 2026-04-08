package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.ProcessVersionRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessVersionResponseDto;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.entity.ProcessVersionStatus;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessVersionServiceImplTest {

    @Mock
    private ProcessVersionRepository processVersionRepository;

    @Mock
    private ProcessRepository processRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ProcessVersionServiceImpl service;

    @Test
    void shouldFindByProcessId() {
        Process process = buildProcess(1L);
        ProcessVersion version = buildVersion(5L, process, buildUser(2L), ProcessVersionStatus.PUBLICADA);
        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        when(processVersionRepository.findByProcesoId(1L)).thenReturn(List.of(version));

        List<ProcessVersionResponseDto> result = service.findByProcessId(1L);

        assertEquals(1, result.size());
        assertEquals("PUBLICADA", result.get(0).getEstado());
    }

    @Test
    void shouldCreateVersion() {
        Process process = buildProcess(1L);
        User user = buildUser(2L);
        ProcessVersion saved = buildVersion(5L, process, user, ProcessVersionStatus.BORRADOR);

        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(processVersionRepository.save(any(ProcessVersion.class))).thenReturn(saved);

        ProcessVersionResponseDto result = service.create(buildRequest("borrador"));

        assertEquals(1L, result.getProcessId());
        assertEquals(2L, result.getCreatedByUserId());
        assertEquals("BORRADOR", result.getEstado());
    }

    @Test
    void shouldDefaultStatusWhenInvalid() {
        Process process = buildProcess(1L);
        User user = buildUser(2L);
        ProcessVersion saved = buildVersion(5L, process, user, ProcessVersionStatus.BORRADOR);

        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(processVersionRepository.save(any(ProcessVersion.class))).thenReturn(saved);

        ProcessVersionResponseDto result = service.create(buildRequest("invalido"));

        assertEquals("BORRADOR", result.getEstado());
    }

    @Test
    void shouldUpdateVersion() {
        Process process = buildProcess(1L);
        User user = buildUser(2L);
        ProcessVersion existing = buildVersion(5L, process, user, ProcessVersionStatus.BORRADOR);
        ProcessVersion saved = buildVersion(5L, process, user, ProcessVersionStatus.PUBLICADA);

        when(processVersionRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(processVersionRepository.save(existing)).thenReturn(saved);

        ProcessVersionResponseDto result = service.update(5L, buildRequest("publicada"));

        assertEquals("PUBLICADA", result.getEstado());
    }

    @Test
    void shouldDeleteVersion() {
        ProcessVersion existing = buildVersion(5L, buildProcess(1L), buildUser(2L), ProcessVersionStatus.BORRADOR);
        when(processVersionRepository.findById(5L)).thenReturn(Optional.of(existing));

        service.delete(5L);

        verify(processVersionRepository).delete(existing);
    }

    @Test
    void shouldThrowWhenVersionNotFound() {
        when(processVersionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    private ProcessVersionRequestDto buildRequest(String status) {
        ProcessVersionRequestDto request = new ProcessVersionRequestDto();
        request.setProcessId(1L);
        request.setNumeroVersion(3);
        request.setEstado(status);
        request.setCreatedByUserId(2L);
        return request;
    }

    private Process buildProcess(Long id) {
        Process process = new Process();
        process.setId(id);
        return process;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private ProcessVersion buildVersion(Long id, Process process, User user, ProcessVersionStatus status) {
        ProcessVersion version = new ProcessVersion();
        version.setId(id);
        version.setProceso(process);
        version.setCreadoPor(user);
        version.setNumeroVersion(1);
        version.setEstado(status);
        return version;
    }
}
