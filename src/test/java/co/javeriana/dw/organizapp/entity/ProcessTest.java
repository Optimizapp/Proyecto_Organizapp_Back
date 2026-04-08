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

class ProcessTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidProcess() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        Set<ConstraintViolation<Process>> violations = validator.validate(process);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Process process = new Process();
        process.setName("");
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        Set<ConstraintViolation<Process>> violations = validator.validate(process);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameTooLong() {
        Process process = new Process();
        process.setName("a".repeat(151));
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        Set<ConstraintViolation<Process>> violations = validator.validate(process);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowNullDescription() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setDescription(null);
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        Set<ConstraintViolation<Process>> violations = validator.validate(process);

        assertTrue(violations.isEmpty());
        assertNull(process.getDescription());
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setDescription("a".repeat(1001));
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        Set<ConstraintViolation<Process>> violations = validator.validate(process);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenStatusIsNullBeforePersist() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setCompany(new Company());
        process.setUser(new User());

        Set<ConstraintViolation<Process>> violations = validator.validate(process);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldSetDefaultStatusOnPrePersist() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setCompany(new Company());
        process.setUser(new User());

        process.prePersist();

        assertNotNull(process.getStatus());
    }

    @Test
    void shouldSetTimestampsOnPrePersist() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        process.prePersist();

        assertNotNull(process.getCreatedAt());
        assertNotNull(process.getUpdatedAt());
    }

    @Test
    void shouldNotOverrideExistingCreatedAt() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        process.setCreatedAt(fixedTime);

        process.prePersist();

        assertEquals(fixedTime, process.getCreatedAt());
    }

    @Test
    void shouldUpdateOnlyUpdatedAtOnPreUpdate() {
        Process process = new Process();
        process.setName("Proceso X");
        process.setStatus(ProcessStatus.values()[0]);
        process.setCompany(new Company());
        process.setUser(new User());

        process.prePersist();
        LocalDateTime createdAt = process.getCreatedAt();

        process.preUpdate();

        assertEquals(createdAt, process.getCreatedAt());
        assertNotNull(process.getUpdatedAt());
    }

    @Test
    void shouldAddVersionCorrectly() {
        Process process = new Process();
        ProcessVersion version = new ProcessVersion();

        process.addVersion(version);

        assertTrue(process.getVersiones().contains(version));
        assertEquals(process, version.getProceso());
    }

    @Test
    void shouldRemoveVersionCorrectly() {
        Process process = new Process();
        ProcessVersion version = new ProcessVersion();

        process.addVersion(version);
        process.removeVersion(version);

        assertFalse(process.getVersiones().contains(version));
        assertNull(version.getProceso());
    }

    @Test
    void shouldAddRoleCorrectly() {
        Process process = new Process();
        Role role = new Role();

        process.addRole(role);

        assertTrue(process.getRoles().contains(role));
        assertEquals(process, role.getProceso());
    }

    @Test
    void shouldRemoveRoleCorrectly() {
        Process process = new Process();
        Role role = new Role();

        process.addRole(role);
        process.removeRole(role);

        assertFalse(process.getRoles().contains(role));
        assertNull(role.getProceso());
    }
}
