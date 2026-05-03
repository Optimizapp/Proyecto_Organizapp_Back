package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CreatePoolRequest;
import co.javeriana.dw.organizapp.dto.PoolResponse;
import co.javeriana.dw.organizapp.dto.UpdatePoolRequest;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceInUseException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.LaneRepository;
import co.javeriana.dw.organizapp.repository.PoolRepository;
import co.javeriana.dw.organizapp.service.PoolService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PoolServiceImpl implements PoolService {

    private static final String COMPANY_NOT_FOUND_MESSAGE = "Empresa no encontrada con ID: ";
    private static final String POOL_NOT_FOUND_MESSAGE = "Pool no encontrado con ID: ";

    private final PoolRepository poolRepository;
    private final CompanyRepository companyRepository;
    private final LaneRepository laneRepository;
    private final ModelMapper modelMapper;

    public PoolServiceImpl(
            PoolRepository poolRepository,
            CompanyRepository companyRepository,
            LaneRepository laneRepository,
            ModelMapper modelMapper) {
        this.poolRepository = poolRepository;
        this.companyRepository = companyRepository;
        this.laneRepository = laneRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoolResponse> findAll(Long companyId) {
        if (companyId != null) {
            findCompany(companyId);
            return poolRepository.findByCompanyId(companyId).stream()
                    .map(this::toDto)
                    .toList();
        }

        return poolRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PoolResponse findById(Long id) {
        return toDto(findPool(id));
    }

    @Override
    @Transactional
    public PoolResponse create(CreatePoolRequest request) {
        Company company = findCompany(request.getCompanyId());
        validatePoolNameAvailable(company.getId(), request.getName());

        Pool pool = new Pool();
        pool.setName(request.getName());
        pool.setDescription(request.getDescription());
        pool.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        pool.setCompany(company);

        return toDto(poolRepository.save(pool));
    }

    @Override
    @Transactional
    public PoolResponse update(Long id, UpdatePoolRequest request) {
        Pool existingPool = findPool(id);
        Company company = findCompany(request.getCompanyId());
        if (!existingPool.getCompany().getId().equals(company.getId())
                || !existingPool.getName().equals(request.getName())) {
            validatePoolNameAvailable(company.getId(), request.getName());
        }

        existingPool.setName(request.getName());
        existingPool.setDescription(request.getDescription());
        existingPool.setActive(request.getActive());
        existingPool.setCompany(company);

        return toDto(poolRepository.save(existingPool));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Pool pool = findPool(id);
        if (laneRepository.existsByPoolId(id)) {
            throw new ResourceInUseException("No se puede eliminar el pool porque tiene lanes asociadas");
        }
        poolRepository.delete(pool);
    }

    private PoolResponse toDto(Pool pool) {
        PoolResponse dto = modelMapper.map(pool, PoolResponse.class);
        dto.setCompanyId(pool.getCompany().getId());
        return dto;
    }

    private Pool findPool(Long id) {
        return poolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POOL_NOT_FOUND_MESSAGE + id));
    }

    private Company findCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_MESSAGE + companyId));
    }

    private void validatePoolNameAvailable(Long companyId, String name) {
        if (poolRepository.existsByCompanyIdAndName(companyId, name)) {
            throw new DuplicateResourceException("Ya existe un pool con nombre: " + name);
        }
    }
}
