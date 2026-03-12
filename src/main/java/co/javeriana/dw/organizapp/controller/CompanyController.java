package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.CompanyRequestDto;
import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
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
     * Obtiene todas las empresas mapeadas a DTO de respuesta
     * @return Lista de CompanyResponseDto
     */
    @GetMapping
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        List<CompanyResponseDto> companies = companyService.findAll();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    /**
     * Obtiene una empresa por ID mapeada a DTO
     * @param id ID de la empresa
     * @return CompanyResponseDto encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Long id) {
        CompanyResponseDto company = companyService.findById(id);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    /**
     * Crea una nueva empresa recibiendo un DTO de solicitud
     * @param companyDto Datos de la empresa (DTO)
     * @return CompanyResponseDto de la empresa creada
     */
    @PostMapping
    public ResponseEntity<CompanyResponseDto> createCompany(@Valid @RequestBody CompanyRequestDto companyDto) {
        CompanyResponseDto createdCompany = companyService.create(companyDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    /**
     * Actualiza una empresa existente usando DTOs
     * @param id ID de la empresa a actualizar
     * @param companyDto Datos actualizados (DTO)
     * @return CompanyResponseDto actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequestDto companyDto) {
        CompanyResponseDto updatedCompany = companyService.update(id, companyDto);
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