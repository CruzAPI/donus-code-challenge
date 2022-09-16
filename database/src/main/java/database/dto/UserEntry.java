package database.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntry implements Cloneable
{
	@CPF
	private String cpf;
	
	@NotBlank
	@Length(max = 100)
	private String username;
	
	@NotBlank
	@Pattern(regexp = "[\\p{Punct}\\w]*")
	@Length(min = 4)
	private String password;
	
	@NotBlank
	@Length(max = 130)
	private String fullName;
	
	public String getCpf()
	{
		return cpf.replaceAll("[\\p{Punct}]*", "");
	}
	
	@Override
	public UserEntry clone()
	{
		try
		{
			return (UserEntry) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}
}