package database.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import database.models.DepositModel;

public interface DepositRepository extends JpaRepository<DepositModel, UUID>
{

}