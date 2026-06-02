package ai.assistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<ai.assistance.models.Roles,Long> {
    Optional<ai.assistance.models.Roles> findByRoleName(String roleName);
}
