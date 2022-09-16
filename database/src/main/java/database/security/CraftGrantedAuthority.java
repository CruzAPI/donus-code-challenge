package database.security;

import org.springframework.security.core.GrantedAuthority;

import database.models.RoleModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CraftGrantedAuthority implements GrantedAuthority
{
	private static final long serialVersionUID = 1L;
	
	private final RoleModel roleModel;
	
	@Override
	public String getAuthority()
	{
		return roleModel.getRole().toString();
	}
}
