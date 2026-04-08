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

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User buildValidUser() {
        User user = new User();
        user.setName("Juan");
        user.setEmail("juan@test.com");
        user.setCompany(new Company());
        user.setRol(new Role());
        return user;
    }

    @Test
    void shouldCreateValidUser() {
        User user = buildValidUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        User user = buildValidUser();
        user.setName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        User user = buildValidUser();
        user.setEmail("correo-invalido");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmailTooLong() {
        User user = buildValidUser();
        user.setEmail("a".repeat(151) + "@test.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowNullPassword() {
        User user = buildValidUser();
        user.setContrasenaHash(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
        assertNull(user.getContrasenaHash());
    }

    @Test
    void shouldFailWhenPasswordTooLong() {
        User user = buildValidUser();
        user.setContrasenaHash("a".repeat(256));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldSetDatesAndActivoOnPrePersist() {
        User user = buildValidUser();
        user.setActivo(null);

        user.prePersist();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getActivo());
    }

    @Test
    void shouldUpdateUpdatedAtOnPreUpdate() {
        User user = buildValidUser();
        user.prePersist();

        LocalDateTime before = user.getUpdatedAt();

        user.preUpdate();

        assertTrue(user.getUpdatedAt().isAfter(before) || user.getUpdatedAt().isEqual(before));
    }

    @Test
    void shouldAddProcessCorrectly() {
        User user = buildValidUser();
        Process process = new Process();

        user.addProcess(process);

        assertTrue(user.getProcesses().contains(process));
        assertEquals(user, process.getUser());
    }

    @Test
    void shouldRemoveProcessCorrectly() {
        User user = buildValidUser();
        Process process = new Process();

        user.addProcess(process);
        user.removeProcess(process);

        assertFalse(user.getProcesses().contains(process));
        assertNull(process.getUser());
    }

    @Test
    void shouldAddCreatedVersionCorrectly() {
        User user = buildValidUser();
        ProcessVersion version = new ProcessVersion();

        user.addCreatedVersion(version);

        assertTrue(user.getVersionesCreadas().contains(version));
        assertEquals(user, version.getCreadoPor());
    }

    @Test
    void shouldRemoveCreatedVersionCorrectly() {
        User user = buildValidUser();
        ProcessVersion version = new ProcessVersion();

        user.addCreatedVersion(version);
        user.removeCreatedVersion(version);

        assertFalse(user.getVersionesCreadas().contains(version));
        assertNull(version.getCreadoPor());
    }

    @Test
    void shouldAddCommentCorrectly() {
        User user = buildValidUser();
        Comment comment = new Comment();

        user.addComment(comment);

        assertTrue(user.getComentarios().contains(comment));
        assertEquals(user, comment.getUser());
    }

    @Test
    void shouldRemoveCommentCorrectly() {
        User user = buildValidUser();
        Comment comment = new Comment();

        user.addComment(comment);
        user.removeComment(comment);

        assertFalse(user.getComentarios().contains(comment));
        assertNull(comment.getUser());
    }
}
