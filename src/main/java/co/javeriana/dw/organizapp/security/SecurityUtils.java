package co.javeriana.dw.organizapp.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Long getCurrentCompanyId() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("No hay contexto de request activo");
        }
        Object companyId = attrs.getRequest().getAttribute("companyId");
        if (companyId == null) {
            throw new IllegalStateException("companyId no encontrado en el contexto de seguridad");
        }
        return (Long) companyId;
    }
}
