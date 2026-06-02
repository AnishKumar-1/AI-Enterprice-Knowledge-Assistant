package ai.assistance.repositories;

import ai.assistance.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<Users,Long> {
    Users findByEmail(String username);
}
