package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import org.springframework.http.HttpStatus;
import co.javeriana.dw.organizapp.service.ProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;



@RestController
@RequestMapping("/api/processes")
public class ProcessController {
    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping
    public ResponseEntity<List<ProcessResponseDto>> getAll() {
        return ResponseEntity.ok(processService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(processService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProcessResponseDto> create(@Valid @RequestBody ProcessRequestDto dto) {
        return new ResponseEntity<>(processService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessResponseDto> update(@PathVariable Long id, @Valid @RequestBody ProcessRequestDto dto) {
        return ResponseEntity.ok(processService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        processService.delete(id);
        return ResponseEntity.noContent().build();
    }
}