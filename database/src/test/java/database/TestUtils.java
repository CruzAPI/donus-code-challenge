package database;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.springframework.beans.BeanUtils;

import database.dto.UserAttributeMap;
import database.dto.UserEntry;
import database.models.RoleModel;
import database.models.UserModel;
import database.security.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TestUtils
{
	private static final String ENCODED_PASSWORD;
	
	private static final UserEntry USER_ENTRY;
	private static final UserModel USER_MODEL;
	private static final UserAttributeMap USER_ATTRIBUTE_MAP;
	
	private static final UserEntry OTHER_USER_ENTRY;
	private static final UserModel OTHER_USER_MODEL;
	private static final UserAttributeMap OTHER_USER_ATTRIBUTE_MAP;
	
	private static final RoleModel ROLE_MODEL_USER;
	private static final BigDecimal DEFAULT_BALANCE;
	
	static
	{
		DEFAULT_BALANCE = new BigDecimal("10000");
		
		ROLE_MODEL_USER = new RoleModel();
		ROLE_MODEL_USER.setRole(Role.ROLE_USER);
		
		ENCODED_PASSWORD = "$2a$10$f/BTNuJD9yD66fX2XHDTU.OBZ8m3EhM.R827Jp8hyMdE2eVS/ovIK";
		
		
		USER_ENTRY = new UserEntry("87450600021", "cruzapi", "test123", "Guilherme Cruz");
		
		USER_MODEL = new UserModel();
		BeanUtils.copyProperties(USER_ENTRY, USER_MODEL);
		USER_MODEL.setPassword(ENCODED_PASSWORD);
		USER_MODEL.setBalance(DEFAULT_BALANCE);
		USER_MODEL.setRoles(List.of(ROLE_MODEL_USER));
		
		USER_ATTRIBUTE_MAP = new UserAttributeMap();
		USER_ATTRIBUTE_MAP.setCpf(USER_MODEL.getCpf());
		
		
		OTHER_USER_ENTRY = new UserEntry("73589234059", "cruzapi1", "test123", "Murilo Davi Rocha");
		
		OTHER_USER_MODEL = new UserModel();
		BeanUtils.copyProperties(OTHER_USER_ENTRY, OTHER_USER_MODEL);
		OTHER_USER_MODEL.setPassword(ENCODED_PASSWORD);
		OTHER_USER_MODEL.setBalance(DEFAULT_BALANCE);
		OTHER_USER_MODEL.setRoles(List.of(ROLE_MODEL_USER));
		
		OTHER_USER_ATTRIBUTE_MAP = new UserAttributeMap();
		OTHER_USER_ATTRIBUTE_MAP.setCpf(OTHER_USER_MODEL.getCpf());
	}
	
	public static String getEncodedPassword()
	{
		return ENCODED_PASSWORD;
	}
	
	public static UserAttributeMap getUserAttributeMap()
	{
		return USER_ATTRIBUTE_MAP.clone();
	}
	
	public static BigDecimal getDefaultBalance()
	{
		return DEFAULT_BALANCE;
	}
	
	public static RoleModel getRoleModelUser()
	{
		return ROLE_MODEL_USER.clone();
	}
	
	public static UserEntry getUserEntry()
	{
		return USER_ENTRY.clone();
	}
	
	public static UserModel getUserModel()
	{
		return USER_MODEL.clone();
	}
	
	public static UserAttributeMap getOtherUserAttributeMap()
	{
		return OTHER_USER_ATTRIBUTE_MAP.clone();
	}
	
	public static UserEntry getOtherUserEntry()
	{
		return OTHER_USER_ENTRY.clone();
	}
	
	public static UserModel getOtherUserModel()
	{
		return OTHER_USER_MODEL.clone();
	}
	
	public static String randomCpf(boolean punctuated)
	{
		String[] cpfArray = new String[] { "99874122030", "03542269085", "40769039006", "55776700043", "14767137071",
				"02929129000", "55978335028", "28925033020", "87450600021", "73589234059", "87450600021" };
		
		Random r = new Random();
		String cpf = cpfArray[r.nextInt(cpfArray.length)];
		
		return punctuated ? Util.getCpfWithPunctuation(cpf) : cpf;
	}
}