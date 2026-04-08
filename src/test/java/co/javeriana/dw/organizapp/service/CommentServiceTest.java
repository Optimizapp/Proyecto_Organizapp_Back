package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CommentRequestDto;
import co.javeriana.dw.organizapp.dto.CommentResponseDto;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

    @Mock
    private CommentService commentService;

    public CommentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        when(commentService.findAll()).thenReturn(List.of(new CommentResponseDto()));

        List<CommentResponseDto> result = commentService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByVersionId() {
        when(commentService.findByVersionId(1L)).thenReturn(List.of(new CommentResponseDto()));

        List<CommentResponseDto> result = commentService.findByVersionId(1L);

        assertNotNull(result);
    }

    @Test
    void findById() {
        when(commentService.findById(1L)).thenReturn(new CommentResponseDto());

        CommentResponseDto result = commentService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create() {
        CommentRequestDto request = new CommentRequestDto();
        CommentResponseDto response = new CommentResponseDto();

        when(commentService.create(any())).thenReturn(response);

        CommentResponseDto result = commentService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        CommentRequestDto request = new CommentRequestDto();
        CommentResponseDto response = new CommentResponseDto();

        when(commentService.update(eq(1L), any())).thenReturn(response);

        CommentResponseDto result = commentService.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        doNothing().when(commentService).delete(1L);

        assertDoesNotThrow(() -> commentService.delete(1L));
    }
}