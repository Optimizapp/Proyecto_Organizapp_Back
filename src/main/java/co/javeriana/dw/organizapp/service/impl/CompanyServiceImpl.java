package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.exception.CompanyNotFoundException;
import co.javeriana.dw.organizapp.exception.DuplicateCompanyException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Obtiene todas las empresas desde la base de datos
     * @return Lista de todas las empresas
     */
    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    /**
     * Obtiene una empresa por su ID
     * @param id ID de la empresa a buscar
     * @return Empresa encontrada
     * @throws RuntimeException Si no se encuentra la empresa
     */
    @Override
    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
    }

    /**
     * Crea una nueva empresa en la base de datos
     * @param company Empresa a crear
     * @return Empresa creada
     */
    @Override
    public Company create(Company company) {
        validateDuplicatesForCreate(company);
        return companyRepository.save(company);
    }

    /**
     * Actualiza una empresa existente
     * @param id ID de la empresa a actualizar
     * @param company Datos actualizados de la empresa
     * @return Empresa actualizada
     * @throws RuntimeException Si no se encuentra la empresa
     */
    @Override
    public Company update(Long id, Company company) {
        Company existingCompany = findById(id);
        validateDuplicatesForUpdate(existingCompany, company);
        existingCompany.setName(company.getName());
        existingCompany.setNit(company.getNit());
        existingCompany.setIndustry(company.getIndustry());
        return companyRepository.save(existingCompany);
    }

    /**
     * Elimina una empresa por su ID
     * @param id ID de la empresa a eliminar
     * @throws RuntimeException Si no se encuentra la empresa
     */
    @Override
    public void delete(Long id) {
        Company existingCompany = findById(id);
        companyRepository.delete(existingCompany);
    }

    private void validateDuplicatesForCreate(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new DuplicateCompanyException("Ya existe una empresa con nombre: " + company.getName());
        }
        if (companyRepository.existsByNit(company.getNit())) {
            throw new DuplicateCompanyException("Ya existe una empresa con NIT: " + company.getNit());
        }
    }

    private void validateDuplicatesForUpdate(Company existingCompany, Company company) {
        if (!existingCompany.getName().equals(company.getName()) && companyRepository.existsByName(company.getName())) {
            throw new DuplicateCompanyException("Ya existe una empresa con nombre: " + company.getName());
        }
        if (!existingCompany.getNit().equals(company.getNit()) && companyRepository.existsByNit(company.getNit())) {
            throw new DuplicateCompanyException("Ya existe una empresa con NIT: " + company.getNit());
        }
    }
}
