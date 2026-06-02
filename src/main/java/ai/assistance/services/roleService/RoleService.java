package ai.assistance.services.roleService;

import ai.assistance.dtos.roleDto.RoleResponseDto;
import ai.assistance.models.Roles;
import ai.assistance.models.Users;
import ai.assistance.repositories.RoleRepo;
import ai.assistance.repositories.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    //update role
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private UsersRepo usersRepo;

    //fetch all roles

    public List<RoleResponseDto> userRoles() throws RoleNotFoundException {
        List<Roles> storedRoles = roleRepo.findAll();
        return storedRoles.stream().map(role -> {
                    RoleResponseDto roleResponseDto = new RoleResponseDto();
                    roleResponseDto.setRoleId(role.getRoleId());
                    roleResponseDto.setRoles(role.getRoleName().replace("ROLE_", ""));
                    return roleResponseDto;
                }
        ).toList();
    }


    //updating user role
    public String update_role(Long userId, Long roleId) {

        Optional<Users> users = usersRepo.findById(userId);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("User not found with this email: " + userId);
        }

        Optional<Roles> role = roleRepo.findById(roleId);
        if (role.isEmpty()) {
            throw new IllegalArgumentException("Role not found with this id: " + roleId);
        }

        Users u = users.get();
        u.setRole(role.get());
        usersRepo.save(u);
        return "Role updated successfully";
    }

}
