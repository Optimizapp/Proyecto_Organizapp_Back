package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidComment() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion()); 
        comment.setUser(new User());              
        comment.setContenido("yo no debo pizza");

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenContenidoIsBlank() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion());
        comment.setUser(new User());
        comment.setContenido("");

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenContenidoTooLong() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion());
        comment.setUser(new User());

        String longText = "a".repeat(2001);
        comment.setContenido(longText);

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenVersionIsNull() {
        Comment comment = new Comment();
        comment.setUser(new User());
        comment.setContenido("Texto válido");

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenUserIsNull() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion());
        comment.setContenido("Texto válido");

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion());
        comment.setUser(new User());
        comment.setContenido("Texto válido");

        comment.prePersist();

        assertNotNull(comment.getCreatedAt());
    }

    @Test
    void shouldNotOverrideCreatedAtIfAlreadySet() {
        Comment comment = new Comment();
        comment.setVersion(new ProcessVersion());
        comment.setUser(new User());
        comment.setContenido("Texto válido");

        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        comment.setCreatedAt(fixedTime);

        comment.prePersist();

        assertEquals(fixedTime, comment.getCreatedAt());
    }
}