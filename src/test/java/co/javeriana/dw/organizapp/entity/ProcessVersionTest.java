package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProcessVersionTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidProcessVersion() {
        ProcessVersion version = new ProcessVersion();
        version.setProceso(new Process());
        version.setNumeroVersion(1);
        version.setEstado(ProcessVersionStatus.values()[0]);
        version.setCreadoPor(new User());

        Set<ConstraintViolation<ProcessVersion>> violations = validator.validate(version);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenProcesoIsNull() {
        ProcessVersion version = new ProcessVersion();
        version.setNumeroVersion(1);
        version.setEstado(ProcessVersionStatus.values()[0]);
        version.setCreadoPor(new User());

        Set<ConstraintViolation<ProcessVersion>> violations = validator.validate(version);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNumeroVersionIsNull() {
        ProcessVersion version = new ProcessVersion();
        version.setProceso(new Process());
        version.setEstado(ProcessVersionStatus.values()[0]);
        version.setCreadoPor(new User());

        Set<ConstraintViolation<ProcessVersion>> violations = validator.validate(version);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEstadoIsNullBeforePersist() {
        ProcessVersion version = new ProcessVersion();
        version.setProceso(new Process());
        version.setNumeroVersion(1);
        version.setCreadoPor(new User());

        Set<ConstraintViolation<ProcessVersion>> violations = validator.validate(version);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenCreadoPorIsNull() {
        ProcessVersion version = new ProcessVersion();
        version.setProceso(new Process());
        version.setNumeroVersion(1);
        version.setEstado(ProcessVersionStatus.values()[0]);

        Set<ConstraintViolation<ProcessVersion>> violations = validator.validate(version);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldSetDefaultEstadoOnPrePersist() {
        ProcessVersion version = new ProcessVersion();
        version.setProceso(new Process());
        version.setNumeroVersion(1);
        version.setCreadoPor(new User());

        version.prePersist();

        assertNotNull(version.getEstado());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {
        ProcessVersion version = new ProcessVersion();
        version.setProceso(new Process());
        version.setNumeroVersion(1);
        version.setEstado(ProcessVersionStatus.values()[0]);
        version.setCreadoPor(new User());

        version.prePersist();

        assertNotNull(version.getCreatedAt());
    }

    @Test
    void shouldNotOverrideExistingCreatedAt() {
        ProcessVersion version = new ProcessVersion();
        version.setProceso(new Process());
        version.setNumeroVersion(1);
        version.setEstado(ProcessVersionStatus.values()[0]);
        version.setCreadoPor(new User());

        version.setCreatedAt(java.time.LocalDateTime.of(2024, 1, 1, 10, 0));

        version.prePersist();

        assertEquals(java.time.LocalDateTime.of(2024, 1, 1, 10, 0), version.getCreatedAt());
    }

    @Test
    void shouldAddNodoCorrectly() {
        ProcessVersion version = new ProcessVersion();
        Node node = new Node();

        version.addNodo(node);

        assertTrue(version.getNodos().contains(node));
        assertEquals(version, node.getVersion());
    }

    @Test
    void shouldRemoveNodoCorrectly() {
        ProcessVersion version = new ProcessVersion();
        Node node = new Node();

        version.addNodo(node);
        version.removeNodo(node);

        assertFalse(version.getNodos().contains(node));
        assertNull(node.getVersion());
    }

    @Test
    void shouldAddFlujoCorrectly() {
        ProcessVersion version = new ProcessVersion();
        Flow flow = new Flow();

        version.addFlujo(flow);

        assertTrue(version.getFlujos().contains(flow));
        assertEquals(version, flow.getVersion());
    }

    @Test
    void shouldRemoveFlujoCorrectly() {
        ProcessVersion version = new ProcessVersion();
        Flow flow = new Flow();

        version.addFlujo(flow);
        version.removeFlujo(flow);

        assertFalse(version.getFlujos().contains(flow));
        assertNull(flow.getVersion());
    }

    @Test
    void shouldAddComentarioCorrectly() {
        ProcessVersion version = new ProcessVersion();
        Comment comment = new Comment();

        version.addComentario(comment);

        assertTrue(version.getComentarios().contains(comment));
        assertEquals(version, comment.getVersion());
    }

    @Test
    void shouldRemoveComentarioCorrectly() {
        ProcessVersion version = new ProcessVersion();
        Comment comment = new Comment();

        version.addComentario(comment);
        version.removeComentario(comment);

        assertFalse(version.getComentarios().contains(comment));
        assertNull(comment.getVersion());
    }
}