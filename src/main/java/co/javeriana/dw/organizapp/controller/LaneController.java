package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.CreateLaneRequest;
import co.javeriana.dw.organizapp.dto.LaneResponse;
import co.javeriana.dw.organizapp.dto.UpdateLaneRequest;
import co.javeriana.dw.organizapp.service.LaneService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lanes")
public class LaneController {

    private final LaneService laneService;

    public LaneController(LaneService laneService) {
        this.laneService = laneService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<LaneResponse>> getLanes(@RequestParam(required = false) Long poolId) {
        return ResponseEntity.ok(laneService.findAll(poolId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<LaneResponse> getLaneById(@PathVariable Long id) {
        return ResponseEntity.ok(laneService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<LaneResponse> createLane(@Valid @RequestBody CreateLaneRequest request) {
        return new ResponseEntity<>(laneService.create(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<LaneResponse> updateLane(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLaneRequest request) {
        return ResponseEntity.ok(laneService.update(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLane(@PathVariable Long id) {
        laneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
