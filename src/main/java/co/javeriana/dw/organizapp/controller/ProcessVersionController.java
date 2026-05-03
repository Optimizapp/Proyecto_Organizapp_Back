package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.ProcessVersionRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessVersionResponseDto;
import co.javeriana.dw.organizapp.service.ProcessVersionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/process-versions")
public class ProcessVersionController {

    private final ProcessVersionService processVersionService;

    public ProcessVersionController(ProcessVersionService processVersionService) {
        this.processVersionService = processVersionService;
    }

    @GetMapping
    public ResponseEntity<List<ProcessVersionResponseDto>> getProcessVersions(
            @RequestParam(required = false) Long processId) {
        List<ProcessVersionResponseDto> versions = processId == null
                ? processVersionService.findAll()
                : processVersionService.findByProcessId(processId);
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessVersionResponseDto> getProcessVersionById(@PathVariable Long id) {
        return ResponseEntity.ok(processVersionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProcessVersionResponseDto> createProcessVersion(
            @Valid @RequestBody ProcessVersionRequestDto request) {
        return new ResponseEntity<>(processVersionService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessVersionResponseDto> updateProcessVersion(
            @PathVariable Long id,
            @Valid @RequestBody ProcessVersionRequestDto request) {
        return ResponseEntity.ok(processVersionService.update(id, request));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ProcessVersionResponseDto> publishProcessVersion(@PathVariable Long id) {
        return ResponseEntity.ok(processVersionService.publish(id));
    }
}
