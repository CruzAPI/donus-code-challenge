package database.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import database.models.TransferModel;

public interface TransferRepository extends JpaRepository<TransferModel, UUID>
{
	
}