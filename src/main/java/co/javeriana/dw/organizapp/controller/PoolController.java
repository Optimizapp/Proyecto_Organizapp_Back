package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.CreatePoolRequest;
import co.javeriana.dw.organizapp.dto.PoolResponse;
import co.javeriana.dw.organizapp.dto.UpdatePoolRequest;
import co.javeriana.dw.organizapp.service.PoolService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools")
public class PoolController {

    private final PoolService poolService;

    public PoolController(PoolService poolService) {
        this.poolService = poolService;
    }

    @GetMapping
    public ResponseEntity<List<PoolResponse>> getPools(@RequestParam(required = false) Long companyId) {
        return ResponseEntity.ok(poolService.findAll(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoolResponse> getPoolById(@PathVariable Long id) {
        return ResponseEntity.ok(poolService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PoolResponse> createPool(@Valid @RequestBody CreatePoolRequest request) {
        return new ResponseEntity<>(poolService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PoolResponse> updatePool(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePoolRequest request) {
        return ResponseEntity.ok(poolService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePool(@PathVariable Long id) {
        poolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
