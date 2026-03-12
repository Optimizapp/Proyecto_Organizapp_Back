package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CompanyRequestDto;
import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de empresas (Company).
 * Ahora desacoplada de la entidad JPA mediante el uso de DTOs.
 */
public interface CompanyService {

    /**
     * Obtiene todas las empresas mapeadas a DTO de respuesta.
     * @return Lista de todas las empresas como CompanyResponseDto
     */
    List<CompanyResponseDto> findAll();

    /**
     * Obtiene una empresa por su ID y la transforma en DTO de respuesta.
     * @param id ID de la empresa a buscar
     * @return CompanyResponseDto con la información de la empresa
     * @throws co.javeriana.dw.organizapp.exception.CompanyNotFoundException Si no existe
     */
    CompanyResponseDto findById(Long id);

    /**
     * Crea una nueva empresa a partir de un DTO de solicitud.
     * @param companyDto Datos de la nueva empresa
     * @return CompanyResponseDto con los datos de la empresa creada (incluyendo ID generado)
     */
    CompanyResponseDto create(CompanyRequestDto companyDto);

    /**
     * Actualiza una empresa existente usando los datos de un DTO de solicitud.
     * @param id ID de la empresa a actualizar
     * @param companyDto Datos actualizados
     * @return CompanyResponseDto con los cambios aplicados
     * @throws co.javeriana.dw.organizapp.exception.CompanyNotFoundException Si el ID no existe
     */
    CompanyResponseDto update(Long id, CompanyRequestDto companyDto);

    /**
     * Elimina una empresa por su ID.
     * @param id ID de la empresa a eliminar
     * @throws co.javeriana.dw.organizapp.exception.CompanyNotFoundException Si no se encuentra
     */
    void delete(Long id);
}