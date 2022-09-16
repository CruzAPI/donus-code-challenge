package database.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.validator.constraints.br.CPF;

import database.models.UserModel;
import database.services.UserService;
import lombok.Data;

@Data
public class UserAttributeMap implements Cloneable
{
	private UUID uuid;
	
	@CPF
	private String cpf;
	private String username;
	
	public boolean isEmpty()
	{
		return uuid == null && cpf == null && username == null;
	}
	
	public UserModel getUser(UserService userService)
	{
		List<Optional<UserModel>> list = new ArrayList<>();
		
		list.add(uuid == null ? null : userService.findById(uuid));
		list.add(cpf == null ? null : userService.findByCpf(cpf));
		list.add(username == null ? null : userService.findByUsername(username));
		
		UserModel user = null;
		
		for(Optional<UserModel> optional : list)
		{
			if(optional == null)
			{
				continue;
			}
			
			if(optional.isEmpty())
			{
				return null;
			}
			
			UserModel get = optional.get();
			
			if(user == null)
			{
				user = get;
				continue;
			}
			
			if(user != get)
			{
				return null;
			}
		}
		
		return user;
	}
	
	@Override
	public UserAttributeMap clone()
	{
		try
		{
			return (UserAttributeMap) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}
}