package database.security;

import java.security.Principal;

import org.springframework.stereotype.Component;

import database.dto.UserAttributeMap;
import database.models.UserModel;
import database.services.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthComponent
{
	private final UserService userService;
	
	public boolean equals(UserAttributeMap map, Principal principal)
	{
		if(map.isEmpty())
		{
			return true;
		}
		
		UserModel user = map.getUser(userService);
		
		if(user == null)
		{
			return true;
		}
		
		return user.getUsername() != null 
				&& principal != null
				&& user.getUsername().equals(principal.getName());
	}
}
