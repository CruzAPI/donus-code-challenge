package database.security;

public enum Role
{
	ROLE_ADMIN,
	ROLE_USER,
	;
	
	public String getRole()
	{
		return name().startsWith("ROLE_") ? name().substring(5) : name();
	}
	
	public String getAuthority()
	{
		return name();
	}
}
