package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.entity.Company;
import java.util.List;

public interface CompanyService {

    /**
     * Obtiene todas las empresas
     * @return Lista de todas las empresas
     */
    List<Company> findAll();

    /**
     * Obtiene una empresa por su ID
     * @param id ID de la empresa a buscar
     * @return Empresa encontrada
     * @throws RuntimeException Si no se encuentra la empresa
     */
    Company findById(Long id);

    /**
     * Crea una nueva empresa
     * @param company Empresa a crear
     * @return Empresa creada
     */
    Company create(Company company);

    /**
     * Actualiza una empresa existente
     * @param id ID de la empresa a actualizar
     * @param company Datos actualizados de la empresa
     * @return Empresa actualizada
     * @throws RuntimeException Si no se encuentra la empresa
     */
    Company update(Long id, Company company);

    /**
     * Elimina una empresa por su ID
     * @param id ID de la empresa a eliminar
     * @throws RuntimeException Si no se encuentra la empresa
     */
    void delete(Long id);
}
