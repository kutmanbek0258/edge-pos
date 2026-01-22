package kg.edgepos.authservice.dao.repository;

import kg.edgepos.authservice.dao.entity.FileStoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileStoreRepository extends JpaRepository<FileStoreEntity, UUID> {
}
