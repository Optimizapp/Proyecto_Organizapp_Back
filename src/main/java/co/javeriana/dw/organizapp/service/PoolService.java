package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CreatePoolRequest;
import co.javeriana.dw.organizapp.dto.PoolResponse;
import co.javeriana.dw.organizapp.dto.UpdatePoolRequest;
import java.util.List;

public interface PoolService {
    List<PoolResponse> findAll(Long companyId);
    PoolResponse findById(Long id);
    PoolResponse create(CreatePoolRequest request);
    PoolResponse update(Long id, UpdatePoolRequest request);
    void delete(Long id);
}
