package database.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import database.models.UserModel;
import database.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService
{
	private final UserRepository userRepository;
	
	public Optional<UserModel> findByCpf(String cpf)
	{
		return userRepository.findByCpf(cpf);
	}
	
	public List<UserModel> findAll()
	{
		return userRepository.findAll();
	}
	
	@Transactional
	public void delete(UserModel personModel)
	{
		userRepository.delete(personModel);
	}
	
	@Transactional
	public UserModel save(UserModel personModel)
	{
		return userRepository.save(personModel);
	}
	
	public Optional<UserModel> findByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}
	
	public boolean existsByUsername(String username)
	{
		return userRepository.existsByUsername(username);
	}
	
	public boolean existsByCpf(String cpf)
	{
		return userRepository.existsByCpf(cpf);
	}
	
	public <S extends UserModel> boolean exists(Example<S> example)
	{
		return userRepository.exists(example);
	}
	
	public Optional<UserModel> findById(UUID uuid)
	{
		return userRepository.findById(uuid);
	}
}