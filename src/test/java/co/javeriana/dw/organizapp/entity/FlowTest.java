package co.javeriana.dw.organizapp.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlowTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidFlow() {
        Flow flow = new Flow();
        flow.setVersion(new ProcessVersion());
        flow.setNodoOrigen(new Node());
        flow.setNodoDestino(new Node());
        flow.setCondicion("Condición válida");
        flow.setEtiqueta("Etiqueta válida");

        Set<ConstraintViolation<Flow>> violations = validator.validate(flow);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenVersionIsNull() {
        Flow flow = new Flow();
        flow.setNodoOrigen(new Node());
        flow.setNodoDestino(new Node());

        Set<ConstraintViolation<Flow>> violations = validator.validate(flow);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNodoOrigenIsNull() {
        Flow flow = new Flow();
        flow.setVersion(new ProcessVersion());
        flow.setNodoDestino(new Node());

        Set<ConstraintViolation<Flow>> violations = validator.validate(flow);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNodoDestinoIsNull() {
        Flow flow = new Flow();
        flow.setVersion(new ProcessVersion());
        flow.setNodoOrigen(new Node());

        Set<ConstraintViolation<Flow>> violations = validator.validate(flow);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenCondicionTooLong() {
        Flow flow = new Flow();
        flow.setVersion(new ProcessVersion());
        flow.setNodoOrigen(new Node());
        flow.setNodoDestino(new Node());

        String longText = "a".repeat(501);
        flow.setCondicion(longText);

        Set<ConstraintViolation<Flow>> violations = validator.validate(flow);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEtiquetaTooLong() {
        Flow flow = new Flow();
        flow.setVersion(new ProcessVersion());
        flow.setNodoOrigen(new Node());
        flow.setNodoDestino(new Node());

        String longText = "a".repeat(101);
        flow.setEtiqueta(longText);

        Set<ConstraintViolation<Flow>> violations = validator.validate(flow);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAllowNullCondicionAndEtiqueta() {
        Flow flow = new Flow();
        flow.setVersion(new ProcessVersion());
        flow.setNodoOrigen(new Node());
        flow.setNodoDestino(new Node());

        Set<ConstraintViolation<Flow>> violations = validator.validate(flow);

        assertTrue(violations.isEmpty());
    }
}