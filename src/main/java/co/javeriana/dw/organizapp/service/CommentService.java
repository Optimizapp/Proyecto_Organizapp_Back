package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CommentRequestDto;
import co.javeriana.dw.organizapp.dto.CommentResponseDto;
import java.util.List;

public interface CommentService {
    List<CommentResponseDto> findAll();
    List<CommentResponseDto> findByVersionId(Long versionId);
    CommentResponseDto findById(Long id);
    CommentResponseDto create(CommentRequestDto commentDto);
    CommentResponseDto update(Long id, CommentRequestDto commentDto);
    void delete(Long id);
}
