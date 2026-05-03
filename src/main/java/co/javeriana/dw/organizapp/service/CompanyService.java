package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.dto.CreateCompanyRequest;
import co.javeriana.dw.organizapp.dto.RegisterCompanyRequest;
import co.javeriana.dw.organizapp.dto.RegisterCompanyResponse;
import co.javeriana.dw.organizapp.dto.UpdateCompanyRequest;
import java.util.List;

public interface CompanyService {
    List<CompanyResponseDto> findAll();
    CompanyResponseDto findById(Long id);
    RegisterCompanyResponse register(RegisterCompanyRequest request);
    CompanyResponseDto create(CreateCompanyRequest companyDto);
    CompanyResponseDto update(Long id, UpdateCompanyRequest companyDto);
    void delete(Long id);
}
