package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidPermission() {
        Permission permission = new Permission();
        permission.setCodigo("READ_USERS");
        permission.setDescripcion("Permite leer usuarios");
        permission.setRol(new Role());

        Set<ConstraintViolation<Permission>> violations = validator.validate(permission);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenCodigoIsBlank() {
        Permission permission = new Permission();
        permission.setCodigo("");
        permission.setRol(new Role());

        Set<ConstraintViolation<Permission>> violations = validator.validate(permission);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenCodigoTooLong() {
        Permission permission = new Permission();
        permission.setCodigo("a".repeat(101));
        permission.setRol(new Role());

        Set<ConstraintViolation<Permission>> violations = validator.validate(permission);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowNullDescripcion() {
        Permission permission = new Permission();
        permission.setCodigo("READ_USERS");
        permission.setRol(new Role());

        Set<ConstraintViolation<Permission>> violations = validator.validate(permission);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescripcionTooLong() {
        Permission permission = new Permission();
        permission.setCodigo("READ_USERS");
        permission.setDescripcion("a".repeat(256));
        permission.setRol(new Role());

        Set<ConstraintViolation<Permission>> violations = validator.validate(permission);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenRolIsNull() {
        Permission permission = new Permission();
        permission.setCodigo("READ_USERS");

        Set<ConstraintViolation<Permission>> violations = validator.validate(permission);

        assertFalse(violations.isEmpty());
    }
}