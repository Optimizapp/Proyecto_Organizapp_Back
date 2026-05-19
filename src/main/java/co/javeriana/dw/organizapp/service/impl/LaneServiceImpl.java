package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CreateLaneRequest;
import co.javeriana.dw.organizapp.dto.LaneResponse;
import co.javeriana.dw.organizapp.dto.UpdateLaneRequest;
import co.javeriana.dw.organizapp.entity.Lane;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.LaneRepository;
import co.javeriana.dw.organizapp.repository.PoolRepository;
import co.javeriana.dw.organizapp.service.LaneService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaneServiceImpl implements LaneService {

    private static final String POOL_NOT_FOUND_MESSAGE = "Pool no encontrado con ID: ";
    private static final String LANE_NOT_FOUND_MESSAGE = "Lane no encontrada con ID: ";

    private final LaneRepository laneRepository;
    private final PoolRepository poolRepository;
    private final ModelMapper modelMapper;

    public LaneServiceImpl(
            LaneRepository laneRepository,
            PoolRepository poolRepository,
            ModelMapper modelMapper) {
        this.laneRepository = laneRepository;
        this.poolRepository = poolRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LaneResponse> findAll(Long poolId) {
        if (poolId != null) {
            findPool(poolId);
            return laneRepository.findByPoolId(poolId).stream()
                    .map(this::toDto)
                    .toList();
        }

        return laneRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LaneResponse findById(Long id) {
        return toDto(findLane(id));
    }

    @Override
    @Transactional
    public LaneResponse create(CreateLaneRequest request) {
        Pool pool = findPool(request.getPoolId());
        validateLaneNameAvailable(pool.getId(), request.getName());

        Lane lane = new Lane();
        lane.setName(request.getName());
        lane.setDescription(request.getDescription());
        lane.setOrderIndex(request.getOrderIndex());
        lane.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
        lane.setPool(pool);

        return toDto(laneRepository.save(lane));
    }

    @Override
    @Transactional
    public LaneResponse update(Long id, UpdateLaneRequest request) {
        Lane existingLane = findLane(id);
        Pool pool = findPool(request.getPoolId());
        if (!existingLane.getPool().getId().equals(pool.getId())
                || !existingLane.getName().equals(request.getName())) {
            validateLaneNameAvailable(pool.getId(), request.getName());
        }

        existingLane.setName(request.getName());
        existingLane.setDescription(request.getDescription());
        existingLane.setOrderIndex(request.getOrderIndex());
        existingLane.setActive(request.getActive());
        existingLane.setPool(pool);

        return toDto(laneRepository.save(existingLane));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Lane lane = findLane(id);
        lane.setActive(false);
        laneRepository.save(lane);
    }

    private LaneResponse toDto(Lane lane) {
        LaneResponse dto = modelMapper.map(lane, LaneResponse.class);
        dto.setPoolId(lane.getPool().getId());
        return dto;
    }

    private Lane findLane(Long id) {
        return laneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LANE_NOT_FOUND_MESSAGE + id));
    }

    private Pool findPool(Long poolId) {
        return poolRepository.findById(poolId)
                .orElseThrow(() -> new ResourceNotFoundException(POOL_NOT_FOUND_MESSAGE + poolId));
    }

    private void validateLaneNameAvailable(Long poolId, String name) {
        if (laneRepository.existsByPoolIdAndName(poolId, name)) {
            throw new DuplicateResourceException("Ya existe una lane con nombre: " + name);
        }
    }
}
