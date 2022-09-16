package database.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import database.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, UUID>
{
	Optional<UserModel> findByUsername(String username);
	boolean existsByUsername(String username);
	Optional<UserModel> findByCpf(String cpf);
	boolean existsByCpf(String cpf);
}