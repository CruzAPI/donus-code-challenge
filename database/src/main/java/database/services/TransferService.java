package database.services;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import database.models.TransferModel;
import database.repository.TransferRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService
{
	private final TransferRepository transferRepository;
	
	@Transactional
	public TransferModel save(TransferModel transferModel)
	{
		return transferRepository.save(transferModel);
	}
}
