package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidNode() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setTipo(NodeType.INICIO); // usa un valor válido de tu enum
        node.setNombre("Nodo 1");
        node.setX(10f);
        node.setY(20f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenVersionIsNull() {
        Node node = new Node();
        node.setTipo(NodeType.INICIO);
        node.setNombre("Nodo");
        node.setX(1f);
        node.setY(1f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenTipoIsNull() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setNombre("Nodo");
        node.setX(1f);
        node.setY(1f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNombreIsBlank() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setTipo(NodeType.INICIO);
        node.setNombre("");
        node.setX(1f);
        node.setY(1f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNombreTooLong() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setTipo(NodeType.INICIO);
        node.setNombre("a".repeat(151));
        node.setX(1f);
        node.setY(1f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenXIsNegative() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setTipo(NodeType.INICIO);
        node.setNombre("Nodo");
        node.setX(-1f);
        node.setY(1f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenYIsNegative() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setTipo(NodeType.INICIO);
        node.setNombre("Nodo");
        node.setX(1f);
        node.setY(-5f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowDescripcionNull() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setTipo(NodeType.INICIO);
        node.setNombre("Nodo");
        node.setX(0f);
        node.setY(0f);

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescripcionTooLong() {
        Node node = new Node();
        node.setVersion(new ProcessVersion());
        node.setTipo(NodeType.INICIO);
        node.setNombre("Nodo");
        node.setX(1f);
        node.setY(1f);
        node.setDescripcion("a".repeat(1001));

        Set<ConstraintViolation<Node>> violations = validator.validate(node);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddAtributoCorrectly() {
        Node node = new Node();
        NodeAttribute attribute = new NodeAttribute();

        node.addAtributo(attribute);

        assertTrue(node.getAtributos().contains(attribute));
        assertEquals(node, attribute.getNodo());
    }

    @Test
    void shouldRemoveAtributoCorrectly() {
        Node node = new Node();
        NodeAttribute attribute = new NodeAttribute();

        node.addAtributo(attribute);
        node.removeAtributo(attribute);

        assertFalse(node.getAtributos().contains(attribute));
        assertNull(attribute.getNodo());
    }
}