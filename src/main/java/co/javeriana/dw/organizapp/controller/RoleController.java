package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.security.SecurityUtils;
import co.javeriana.dw.organizapp.service.RoleService;
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
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getRoles(
            @RequestParam(required = false) Long processId) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        List<RoleResponseDto> roles = roleService.findByCompanyId(companyId, processId);
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto roleDto) {
        return new ResponseEntity<>(roleService.create(roleDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDto roleDto) {
        return ResponseEntity.ok(roleService.update(id, roleDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
