package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.CreateProcessRequest;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.dto.UpdateProcessRequest;
import co.javeriana.dw.organizapp.service.ProcessService;
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
@RequestMapping("/api/processes")
public class ProcessController {
    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping
    public ResponseEntity<List<ProcessResponseDto>> getAll(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(processService.findAll(companyId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(processService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProcessResponseDto> create(@Valid @RequestBody CreateProcessRequest dto) {
        return new ResponseEntity<>(processService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProcessRequest dto) {
        return ResponseEntity.ok(processService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        processService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
