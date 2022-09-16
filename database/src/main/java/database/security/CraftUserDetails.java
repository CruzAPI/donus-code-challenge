package database.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import database.models.UserModel;

public class CraftUserDetails implements UserDetails
{
	private static final long serialVersionUID = 1L;
	
	private final UserModel userModel;
	private final List<? extends GrantedAuthority> roles;
	
	public CraftUserDetails(UserModel userModel)
	{
		this.userModel = userModel;
		this.roles = userModel.getRoles().stream().map(x -> new CraftGrantedAuthority(x)).toList();
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return roles;
	}

	@Override
	public String getPassword()
	{
		return userModel.getPassword();
	}

	@Override
	public String getUsername()
	{
		return userModel.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}
	
	@Override
	public boolean isEnabled()
	{
		return true;
	}
}
