package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NodeAttributeTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidNodeAttribute() {
        NodeAttribute attr = new NodeAttribute();
        attr.setNodo(new Node());
        attr.setClave("color");
        attr.setValor("rojo");
        attr.setTipo(NodeAttributeType.values()[0]); // evita problemas con el enum

        Set<ConstraintViolation<NodeAttribute>> violations = validator.validate(attr);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNodoIsNull() {
        NodeAttribute attr = new NodeAttribute();
        attr.setClave("color");
        attr.setTipo(NodeAttributeType.values()[0]);

        Set<ConstraintViolation<NodeAttribute>> violations = validator.validate(attr);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenClaveIsBlank() {
        NodeAttribute attr = new NodeAttribute();
        attr.setNodo(new Node());
        attr.setClave("");
        attr.setTipo(NodeAttributeType.values()[0]);

        Set<ConstraintViolation<NodeAttribute>> violations = validator.validate(attr);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenClaveTooLong() {
        NodeAttribute attr = new NodeAttribute();
        attr.setNodo(new Node());
        attr.setClave("a".repeat(101));
        attr.setTipo(NodeAttributeType.values()[0]);

        Set<ConstraintViolation<NodeAttribute>> violations = validator.validate(attr);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowNullValor() {
        NodeAttribute attr = new NodeAttribute();
        attr.setNodo(new Node());
        attr.setClave("clave");
        attr.setTipo(NodeAttributeType.values()[0]);

        Set<ConstraintViolation<NodeAttribute>> violations = validator.validate(attr);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenValorTooLong() {
        NodeAttribute attr = new NodeAttribute();
        attr.setNodo(new Node());
        attr.setClave("clave");
        attr.setValor("a".repeat(2001));
        attr.setTipo(NodeAttributeType.values()[0]);

        Set<ConstraintViolation<NodeAttribute>> violations = validator.validate(attr);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenTipoIsNull() {
        NodeAttribute attr = new NodeAttribute();
        attr.setNodo(new Node());
        attr.setClave("clave");

        Set<ConstraintViolation<NodeAttribute>> violations = validator.validate(attr);

        assertFalse(violations.isEmpty());
    }
}