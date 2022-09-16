package database.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import database.models.RoleModel;
import database.security.Role;

public interface RoleRepository extends JpaRepository<RoleModel, UUID>
{
	boolean existsByRole(Role role);
	Optional<RoleModel> findByRole(Role role);
}
