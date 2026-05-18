package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.NodeAttributeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeAttributeResponseDto;
import co.javeriana.dw.organizapp.service.NodeAttributeService;
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
@RequestMapping("/api/node-attributes")
public class NodeAttributeController {

    private final NodeAttributeService nodeAttributeService;

    public NodeAttributeController(NodeAttributeService nodeAttributeService) {
        this.nodeAttributeService = nodeAttributeService;
    }

    @GetMapping
    public ResponseEntity<List<NodeAttributeResponseDto>> getAttributes(
            @RequestParam(required = false) Long nodeId) {
        List<NodeAttributeResponseDto> attributes = nodeId == null
                ? nodeAttributeService.findAll()
                : nodeAttributeService.findByNodeId(nodeId);
        return ResponseEntity.ok(attributes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeAttributeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(nodeAttributeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<NodeAttributeResponseDto> create(
            @Valid @RequestBody NodeAttributeRequestDto dto) {
        return new ResponseEntity<>(nodeAttributeService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NodeAttributeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody NodeAttributeRequestDto dto) {
        return ResponseEntity.ok(nodeAttributeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        nodeAttributeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
