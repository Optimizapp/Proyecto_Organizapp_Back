package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CreateLaneRequest;
import co.javeriana.dw.organizapp.dto.LaneResponse;
import co.javeriana.dw.organizapp.dto.UpdateLaneRequest;
import java.util.List;

public interface LaneService {
    List<LaneResponse> findAll(Long poolId);
    LaneResponse findById(Long id);
    LaneResponse create(CreateLaneRequest request);
    LaneResponse update(Long id, UpdateLaneRequest request);
    void delete(Long id);
}
