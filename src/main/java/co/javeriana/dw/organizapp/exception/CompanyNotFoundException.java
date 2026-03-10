package co.javeriana.dw.organizapp.exception;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(Long id) {
        super("Empresa no encontrada con ID: " + id);
    }
}
