package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidRole() {
        Role role = new Role();
        role.setNombre("ADMIN");
        role.setDescripcion("Administrador");
        role.setProceso(new Process());

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNombreIsBlank() {
        Role role = new Role();
        role.setNombre("");
        role.setProceso(new Process());

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNombreTooLong() {
        Role role = new Role();
        role.setNombre("a".repeat(101));
        role.setProceso(new Process());

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowNullDescripcion() {
        Role role = new Role();
        role.setNombre("ADMIN");
        role.setProceso(new Process());

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescripcionTooLong() {
        Role role = new Role();
        role.setNombre("ADMIN");
        role.setDescripcion("a".repeat(256));
        role.setProceso(new Process());

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenProcesoIsNull() {
        Role role = new Role();
        role.setNombre("ADMIN");

        Set<ConstraintViolation<Role>> violations = validator.validate(role);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddPermisoCorrectly() {
        Role role = new Role();
        Permission permiso = new Permission();

        role.addPermiso(permiso);

        assertTrue(role.getPermisos().contains(permiso));
        assertEquals(role, permiso.getRol());
    }

    @Test
    void shouldRemovePermisoCorrectly() {
        Role role = new Role();
        Permission permiso = new Permission();

        role.addPermiso(permiso);
        role.removePermiso(permiso);

        assertFalse(role.getPermisos().contains(permiso));
        assertNull(permiso.getRol());
    }

    @Test
    void shouldAddUsuarioCorrectly() {
        Role role = new Role();
        User usuario = new User();

        role.addUsuario(usuario);

        assertTrue(role.getUsuarios().contains(usuario));
        assertEquals(role, usuario.getRol());
    }

    @Test
    void shouldRemoveUsuarioCorrectly() {
        Role role = new Role();
        User usuario = new User();

        role.addUsuario(usuario);
        role.removeUsuario(usuario);

        assertFalse(role.getUsuarios().contains(usuario));
        assertNull(usuario.getRol());
    }
}