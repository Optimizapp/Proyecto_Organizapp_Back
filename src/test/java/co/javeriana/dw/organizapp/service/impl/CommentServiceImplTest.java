package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CommentRequestDto;
import co.javeriana.dw.organizapp.dto.CommentResponseDto;
import co.javeriana.dw.organizapp.entity.Comment;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CommentRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    private CommentRepository commentRepository;
    private ProcessVersionRepository processVersionRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    private CommentServiceImpl service;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        processVersionRepository = mock(ProcessVersionRepository.class);
        userRepository = mock(UserRepository.class);
        modelMapper = mock(ModelMapper.class);

        service = new CommentServiceImpl(
                commentRepository,
                processVersionRepository,
                userRepository,
                modelMapper
        );
    }

    @Test
    void findAll() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion());
        comment.getVersion().setId(1L);
        comment.setUser(new User());
        comment.getUser().setId(1L);

        when(commentRepository.findAll()).thenReturn(List.of(comment));
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class)))
                .thenReturn(new CommentResponseDto());

        List<CommentResponseDto> result = service.findAll();

        assertNotNull(result);
    }

    @Test
    void findByVersionId() {
        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        Comment comment = new Comment();
        comment.setVersion(version);
        comment.setUser(new User());
        comment.getUser().setId(1L);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(commentRepository.findByVersionId(1L)).thenReturn(List.of(comment));
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class)))
                .thenReturn(new CommentResponseDto());

        List<CommentResponseDto> result = service.findByVersionId(1L);

        assertNotNull(result);
    }

    @Test
    void findById() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion());
        comment.getVersion().setId(1L);
        comment.setUser(new User());
        comment.getUser().setId(1L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class)))
                .thenReturn(new CommentResponseDto());

        CommentResponseDto result = service.findById(1L);

        assertNotNull(result);
    }

    @Test
    void findById_notFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create() {
        CommentRequestDto request = new CommentRequestDto();
        request.setVersionId(1L);
        request.setUserId(1L);
        request.setContenido("Test");

        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        User user = new User();
        user.setId(1L);

        Comment mapped = new Comment();
        Comment saved = new Comment();
        saved.setVersion(version);
        saved.setUser(user);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(request, Comment.class)).thenReturn(mapped);
        when(commentRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class)))
                .thenReturn(new CommentResponseDto());

        CommentResponseDto result = service.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        CommentRequestDto request = new CommentRequestDto();
        request.setVersionId(1L);
        request.setUserId(1L);
        request.setContenido("Updated");

        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        User user = new User();
        user.setId(1L);

        Comment existing = new Comment();
        existing.setId(1L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(existing);
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class)))
                .thenReturn(new CommentResponseDto());

        CommentResponseDto result = service.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        Comment comment = new Comment();
        comment.setId(1L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        assertDoesNotThrow(() -> service.delete(1L));
    }

    @Test
    void delete_notFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}