package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.dto.CreateCompanyRequest;
import co.javeriana.dw.organizapp.dto.RegisterCompanyRequest;
import co.javeriana.dw.organizapp.dto.RegisterCompanyResponse;
import co.javeriana.dw.organizapp.dto.UpdateCompanyRequest;
import co.javeriana.dw.organizapp.service.CompanyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDto> createCompany(
            @Valid @RequestBody CreateCompanyRequest companyDto) {
        return new ResponseEntity<>(companyService.create(companyDto), HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterCompanyResponse> registerCompany(
            @Valid @RequestBody RegisterCompanyRequest request) {
        return new ResponseEntity<>(companyService.register(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyRequest companyDto) {
        return ResponseEntity.ok(companyService.update(id, companyDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
