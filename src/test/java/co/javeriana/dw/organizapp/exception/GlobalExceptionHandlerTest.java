package co.javeriana.dw.organizapp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleNotFoundExceptions() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new ResourceNotFoundException("No existe"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No existe", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void shouldHandleDuplicateCompanyExceptionAsConflict() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleConflict(new DuplicateCompanyException("Empresa duplicada"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Empresa duplicada", response.getBody().get("message"));
    }

    @Test
    void shouldHandleResourceInUseExceptionAsConflict() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleResourceInUse(new ResourceInUseException("Recurso en uso"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Recurso en uso", response.getBody().get("message"));
    }

    @Test
    void shouldHandleBadRequestExceptions() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleBadRequest(new InvalidRequestException("Peticion invalida"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Peticion invalida", response.getBody().get("message"));
    }

    @Test
    void shouldHandleValidationExceptions() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "name", "es obligatorio"),
                new FieldError("request", "email", "formato invalido")
        ));

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error de validación en los datos", response.getBody().get("error"));
        Object fields = response.getBody().get("fields");
        assertInstanceOf(Map.class, fields);
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) fields;
        assertEquals("es obligatorio", fieldErrors.get("name"));
        assertEquals("formato invalido", fieldErrors.get("email"));
    }

    @Test
    void shouldHandleUnexpectedExceptions() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleGlobalException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ha ocurrido un error interno en el servidor.", response.getBody().get("message"));
    }
}
