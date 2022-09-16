package database.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import database.models.UserModel;
import database.services.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CraftUserDetailsService implements UserDetailsService
{
	private final UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		UserModel userModel = userService.findByUsername(username).orElseThrow(() -> 
				new UsernameNotFoundException(String.format("User %s not found.", username)));
		
		return new CraftUserDetails(userModel);
	}
}