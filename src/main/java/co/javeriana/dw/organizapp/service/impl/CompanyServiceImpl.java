package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CompanyRequestDto;
import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.exception.CompanyNotFoundException;
import co.javeriana.dw.organizapp.exception.DuplicateCompanyException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.service.CompanyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, ModelMapper modelMapper) {
        this.companyRepository = companyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CompanyResponseDto> findAll() {
        return companyRepository.findAll().stream()
                .map(company -> modelMapper.map(company, CompanyResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseDto findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        return modelMapper.map(company, CompanyResponseDto.class);
    }

    @Override
    public CompanyResponseDto create(CompanyRequestDto companyDto) {
        validateDuplicatesForCreate(companyDto);
        
        // Mapeo de DTO de entrada a Entidad
        Company company = modelMapper.map(companyDto, Company.class);
        Company savedCompany = companyRepository.save(company);
        
        // Mapeo de Entidad persistida a DTO de respuesta
        return modelMapper.map(savedCompany, CompanyResponseDto.class);
    }

    @Override
    public CompanyResponseDto update(Long id, CompanyRequestDto companyDto) {
        // Buscamos la entidad real primero
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        
        validateDuplicatesForUpdate(existingCompany, companyDto);
        
        // Actualizamos los campos de la entidad existente con los del DTO
        modelMapper.map(companyDto, existingCompany);
        
        Company updatedCompany = companyRepository.save(existingCompany);
        return modelMapper.map(updatedCompany, CompanyResponseDto.class);
    }

    @Override
    public void delete(Long id) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        companyRepository.delete(existingCompany);
    }

    // --- Métodos de validación ajustados para DTOs ---

    private void validateDuplicatesForCreate(CompanyRequestDto dto) {
        if (companyRepository.existsByName(dto.getName())) {
            throw new DuplicateCompanyException("Ya existe una empresa con nombre: " + dto.getName());
        }
        if (companyRepository.existsByNit(dto.getNit())) {
            throw new DuplicateCompanyException("Ya existe una empresa con NIT: " + dto.getNit());
        }
    }

    private void validateDuplicatesForUpdate(Company existingCompany, CompanyRequestDto dto) {
        if (!existingCompany.getName().equals(dto.getName()) && companyRepository.existsByName(dto.getName())) {
            throw new DuplicateCompanyException("Ya existe una empresa con nombre: " + dto.getName());
        }
        if (!existingCompany.getNit().equals(dto.getNit()) && companyRepository.existsByNit(dto.getNit())) {
            throw new DuplicateCompanyException("Ya existe una empresa con NIT: " + dto.getNit());
        }
    }
}