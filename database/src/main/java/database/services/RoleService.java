package database.services;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import database.models.RoleModel;
import database.repository.RoleRepository;
import database.security.Role;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService
{
	private final RoleRepository roleRepository;
	
	@PostConstruct
	@Transactional
	private void saveRoles()
	{
		Iterator<RoleModel> iterator = Arrays.stream(Role.values()).filter(x -> !roleRepository.existsByRole(x))
				.map(x ->
				{
					RoleModel roleModel = new RoleModel();
					roleModel.setRole(x);
					return roleModel;
				}).iterator();
		
		roleRepository.saveAll(() -> iterator);
	}
	
	public Optional<RoleModel> findByRole(Role role)
	{
		return roleRepository.findByRole(role);
	}
	
	public List<RoleModel> findAll()
	{
		return roleRepository.findAll();
	}
}
