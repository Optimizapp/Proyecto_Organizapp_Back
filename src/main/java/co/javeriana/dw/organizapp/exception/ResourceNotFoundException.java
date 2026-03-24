package co.javeriana.dw.organizapp.exception;

// Esta excepción servirá para cualquier entidad no encontrada
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}