package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.PermissionRequestDto;
import co.javeriana.dw.organizapp.dto.PermissionResponseDto;
import co.javeriana.dw.organizapp.service.PermissionService;
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
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponseDto>> getPermissions(@RequestParam(required = false) Long roleId) {
        List<PermissionResponseDto> permissions = roleId == null
                ? permissionService.findAll()
                : permissionService.findByRoleId(roleId);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping
    public ResponseEntity<PermissionResponseDto> createPermission(
            @Valid @RequestBody PermissionRequestDto permissionDto) {
        return new ResponseEntity<>(permissionService.create(permissionDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequestDto permissionDto) {
        return ResponseEntity.ok(permissionService.update(id, permissionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
