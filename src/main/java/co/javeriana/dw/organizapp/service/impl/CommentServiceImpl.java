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
import co.javeriana.dw.organizapp.service.CommentService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProcessVersionRepository processVersionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            ProcessVersionRepository processVersionRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.processVersionRepository = processVersionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAll() {
        return commentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findByVersionId(Long versionId) {
        findVersion(versionId);
        return commentRepository.findByVersionId(versionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDto findById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con ID: " + id));
        return toDto(comment);
    }

    @Override
    @Transactional
    public CommentResponseDto create(CommentRequestDto commentDto) {
        ProcessVersion version = findVersion(commentDto.getVersionId());
        User user = findUser(commentDto.getUserId());

        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setVersion(version);
        comment.setUser(user);

        return toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponseDto update(Long id, CommentRequestDto commentDto) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con ID: " + id));
        ProcessVersion version = findVersion(commentDto.getVersionId());
        User user = findUser(commentDto.getUserId());

        existingComment.setVersion(version);
        existingComment.setUser(user);
        existingComment.setContenido(commentDto.getContenido());

        return toDto(commentRepository.save(existingComment));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con ID: " + id));
        commentRepository.delete(comment);
    }

    private CommentResponseDto toDto(Comment comment) {
        CommentResponseDto dto = modelMapper.map(comment, CommentResponseDto.class);
        dto.setVersionId(comment.getVersion().getId());
        dto.setUserId(comment.getUser().getId());
        return dto;
    }

    private ProcessVersion findVersion(Long versionId) {
        return processVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version de proceso no encontrada con ID: " + versionId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
    }
}
