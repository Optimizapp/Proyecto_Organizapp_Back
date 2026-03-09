package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Obtiene todas las empresas
     * @return Lista de empresas
     */
    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        List<Company> companies = companyService.findAll();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    /**
     * Obtiene una empresa por ID
     * @param id ID de la empresa
     * @return Empresa encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        Company company = companyService.findById(id);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    /**
     * Crea una nueva empresa
     * @param company Empresa a crear
     * @return Empresa creada
     */
    @PostMapping
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company createdCompany = companyService.create(company);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    /**
     * Actualiza una empresa existente
     * @param id ID de la empresa a actualizar
     * @param company Datos actualizados de la empresa
     * @return Empresa actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long id, @Valid @RequestBody Company company) {
        Company updatedCompany = companyService.update(id, company);
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
    }

    /**
     * Elimina una empresa por ID
     * @param id ID de la empresa a eliminar
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
