package database.services;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import database.models.DepositModel;
import database.repository.DepositRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepositService
{
	private final DepositRepository depositRepository;
	
	@Transactional
	public DepositModel save(DepositModel depositModel)
	{
		return depositRepository.save(depositModel);
	}
}