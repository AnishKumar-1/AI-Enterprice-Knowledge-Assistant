package ai.assistance.repositories;

import ai.assistance.models.CustomDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepo extends JpaRepository<CustomDocument,Long> {
    Optional<CustomDocument> findByFileHash(String fileHash);
}
