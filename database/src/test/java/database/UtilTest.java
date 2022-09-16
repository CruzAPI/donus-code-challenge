package database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UtilTest
{
	@Test
	public void getCpfWithPunctuation()
	{
		Assertions.assertEquals("455.470.900-00", Util.getCpfWithPunctuation("45547090000"));
		Assertions.assertEquals("000.000.000-00", Util.getCpfWithPunctuation("00000000000"));
	}
	
	@Test
	public void getCpfWithPunctuationThrowsNPE()
	{
		Assertions.assertThrows(NullPointerException.class, () ->
		{
			Util.getCpfWithPunctuation(null);
		});
	}
	
	@Test
	public void getCpfWithPunctuationThrowsIllegalArgumentException()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () ->
		{
			Util.getCpfWithPunctuation("455470900000");
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () ->
		{
			Util.getCpfWithPunctuation("4554709000");
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () ->
		{
			Util.getCpfWithPunctuation("455.470.900-00");
		});
		
		Assertions.assertThrows(IllegalArgumentException.class, () ->
		{
			Util.getCpfWithPunctuation("4554709000O");
		});
	}
}
