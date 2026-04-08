package co.javeriana.dw.organizapp.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionConstructorsTest {

    @Test
    void shouldCreateDuplicateCompanyExceptionWithMessage() {
        DuplicateCompanyException exception = new DuplicateCompanyException("Empresa duplicada");

        assertEquals("Empresa duplicada", exception.getMessage());
    }

    @Test
    void shouldCreateInvalidRequestExceptionWithMessage() {
        InvalidRequestException exception = new InvalidRequestException("Solicitud invalida");

        assertEquals("Solicitud invalida", exception.getMessage());
    }

    @Test
    void shouldCreateResourceInUseExceptionWithMessage() {
        ResourceInUseException exception = new ResourceInUseException("Recurso en uso");

        assertEquals("Recurso en uso", exception.getMessage());
    }
}
