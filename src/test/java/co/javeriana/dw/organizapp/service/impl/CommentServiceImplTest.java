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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ProcessVersionRepository processVersionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl service;

    @Test
    void shouldFindCommentsByVersionId() {
        ProcessVersion version = buildVersion(1L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(commentRepository.findByVersionId(1L)).thenReturn(List.of(buildComment(2L, version, buildUser(3L))));
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class))).thenAnswer(invocation -> {
            Comment source = invocation.getArgument(0);
            CommentResponseDto dto = new CommentResponseDto();
            dto.setId(source.getId());
            dto.setContenido(source.getContenido());
            return dto;
        });

        List<CommentResponseDto> result = service.findByVersionId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getVersionId());
    }

    @Test
    void shouldCreateComment() {
        ProcessVersion version = buildVersion(1L);
        User user = buildUser(3L);
        Comment mappedComment = new Comment();
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(CommentRequestDto.class), eq(Comment.class))).thenReturn(mappedComment);
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class))).thenAnswer(invocation -> {
            Comment source = invocation.getArgument(0);
            CommentResponseDto dto = new CommentResponseDto();
            dto.setId(source.getId());
            dto.setContenido(source.getContenido());
            return dto;
        });
        when(commentRepository.save(any(Comment.class))).thenReturn(buildComment(2L, version, user));

        CommentResponseDto result = service.create(buildRequest());

        assertEquals(1L, result.getVersionId());
        assertEquals(3L, result.getUserId());
    }

    @Test
    void shouldUpdateComment() {
        ProcessVersion version = buildVersion(1L);
        User user = buildUser(3L);
        Comment existing = buildComment(2L, version, user);

        when(commentRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(Comment.class), eq(CommentResponseDto.class))).thenAnswer(invocation -> {
            Comment source = invocation.getArgument(0);
            CommentResponseDto dto = new CommentResponseDto();
            dto.setId(source.getId());
            dto.setContenido(source.getContenido());
            return dto;
        });
        when(commentRepository.save(existing)).thenReturn(existing);

        CommentResponseDto result = service.update(2L, buildRequest());

        assertEquals("Comentario", result.getContenido());
    }

    @Test
    void shouldDeleteComment() {
        Comment existing = buildComment(2L, buildVersion(1L), buildUser(3L));
        when(commentRepository.findById(2L)).thenReturn(Optional.of(existing));

        service.delete(2L);

        verify(commentRepository).delete(existing);
    }

    @Test
    void shouldThrowWhenCommentNotFound() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    private CommentRequestDto buildRequest() {
        CommentRequestDto request = new CommentRequestDto();
        request.setVersionId(1L);
        request.setUserId(3L);
        request.setContenido("Comentario");
        return request;
    }

    private ProcessVersion buildVersion(Long id) {
        ProcessVersion version = new ProcessVersion();
        version.setId(id);
        return version;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Comment buildComment(Long id, ProcessVersion version, User user) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setVersion(version);
        comment.setUser(user);
        comment.setContenido("Comentario");
        return comment;
    }
}
