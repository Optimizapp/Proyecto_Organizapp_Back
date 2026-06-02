package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.CommentRequestDto;
import co.javeriana.dw.organizapp.dto.CommentResponseDto;
import co.javeriana.dw.organizapp.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @RequestParam(required = false) Long versionId) {
        List<CommentResponseDto> comments = versionId == null
                ? commentService.findAll()
                : commentService.findByVersionId(versionId);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestBody CommentRequestDto dto) {
        return new ResponseEntity<>(commentService.create(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
