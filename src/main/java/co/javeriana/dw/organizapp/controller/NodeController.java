package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import co.javeriana.dw.organizapp.service.NodeService;
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
@RequestMapping("/api/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public ResponseEntity<List<NodeResponseDto>> getNodes(@RequestParam(required = false) Long versionId) {
        List<NodeResponseDto> nodes = versionId == null
                ? nodeService.findAll()
                : nodeService.findByVersionId(versionId);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeResponseDto> getNodeById(@PathVariable Long id) {
        return ResponseEntity.ok(nodeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<NodeResponseDto> createNode(@Valid @RequestBody NodeRequestDto nodeDto) {
        return new ResponseEntity<>(nodeService.create(nodeDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NodeResponseDto> updateNode(@PathVariable Long id, @Valid @RequestBody NodeRequestDto nodeDto) {
        return ResponseEntity.ok(nodeService.update(id, nodeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        nodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
