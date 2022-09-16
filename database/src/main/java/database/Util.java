package database;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Util
{
	public static String getCpfWithPunctuation(String cpf)
	{
		if(!cpf.matches("[\\d]{11}"))
		{
			throw new IllegalArgumentException();
		}
		
		return 	  cpf.substring(0, 3) + "."
				+ cpf.substring(3, 6) + "."
				+ cpf.substring(6, 9) + "-"
				+ cpf.substring(9);
	}
}
