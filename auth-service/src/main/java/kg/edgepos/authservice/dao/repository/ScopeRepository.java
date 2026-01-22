package kg.edgepos.authservice.dao.repository;

import kg.edgepos.authservice.dao.entity.ScopeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScopeRepository extends JpaRepository<ScopeEntity, UUID> {

}
