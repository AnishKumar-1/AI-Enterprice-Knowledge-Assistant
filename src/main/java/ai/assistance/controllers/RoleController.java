package ai.assistance.controllers;

import ai.assistance.dtos.roleDto.RoleResponseDto;
import ai.assistance.services.roleService.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    //admin only
    //get all roles
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponseDto> fetch_all_roles() throws RoleNotFoundException {
        return roleService.userRoles();
    }

    @PutMapping("/{userId}/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update_role(@PathVariable Long userId, @PathVariable Long roleId) {
        return roleService.update_role(userId, roleId);
    }
}
