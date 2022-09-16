package database.controller;

import static database.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import database.dto.UserAttributeMap;
import database.dto.UserEntry;
import database.models.RoleModel;
import database.models.UserModel;
import database.security.Role;
import database.security.SecurityConfig;
import database.services.RoleService;
import database.services.UserService;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest
{
	private UserController userController;
	
	private UserService userService;
	private RoleService roleService;
	private SecurityConfig securityConfig;
	private PasswordEncoder passwordEncoder;
	
	private UserEntry userEntry;
	private RoleModel userRoleModel;
	private UserModel userModel;
	
	private UUID uuidArg; 
	private Optional<RoleModel> userRoleArg; 
	private String encodedPasswordArg;
	
	@Captor
	private ArgumentCaptor<UserModel> userCaptor;
	
	@BeforeAll
	public void setup()
	{
		userEntry = getUserEntry();
		userRoleModel = getRoleModelUser();
		userModel = getUserModel();
	}
	
	@BeforeEach
	public void beforeEach()
	{
		userService = mock(UserService.class);
		roleService = mock(RoleService.class);
		securityConfig = mock(SecurityConfig.class);
		passwordEncoder = mock(PasswordEncoder.class);
		
		when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
		when(passwordEncoder.encode(userEntry.getPassword())).thenReturn(encodedPasswordArg = userModel.getPassword());
		
		when(userService.save(any(UserModel.class))).thenAnswer(x ->
		{
			UserModel userModel = (UserModel) x.getArgument(0);
			userModel.setUuid(uuidArg = UUID.randomUUID());
			return userModel;
		});
		
		when(roleService.findByRole(Role.ROLE_USER)).thenReturn(userRoleArg = Optional.of(userRoleModel));
		
		userController = new UserController(roleService, securityConfig, userService);
	}
	
	@Test
	@Order(1)
	public void postCreated()
	{
		when(userService.existsByCpf(anyString())).thenReturn(false);
		when(userService.existsByUsername(anyString())).thenReturn(false);
		
		ResponseEntity<?> response = userController.post(userEntry);
		
		UserModel userModel = new UserModel();
		BeanUtils.copyProperties(userEntry, userModel);
		userModel.setUuid(uuidArg);
		userModel.setPassword(encodedPasswordArg);
		
		if(userRoleArg.isPresent())
		{
			userModel.setRoles(List.of(userRoleArg.get()));
		}
		
		UserModel body = (UserModel) response.getBody();
		
		verify(securityConfig).passwordEncoder();
		verify(passwordEncoder).encode(userEntry.getPassword());
		verify(userService).existsByCpf(userEntry.getCpf());
		verify(userService).existsByUsername(userEntry.getUsername());
		verify(roleService).findByRole(Role.ROLE_USER);
		verify(userService).save(any(UserModel.class));
		
		Assertions.assertTrue(response.getStatusCode() == HttpStatus.CREATED);
		Assertions.assertEquals(body, userModel);
	}
	
	@Test
	@Order(2)
	public void postCpfConflict()
	{
		when(userService.existsByCpf(anyString())).thenReturn(true);
		
		ResponseEntity<?> response = userController.post(userEntry);
		
		verify(userService).existsByCpf(userEntry.getCpf());
		verify(userService, never()).existsByUsername(any());
		verify(roleService, never()).findByRole(any());
		verify(userService, never()).save(any());
		
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
		Assertions.assertEquals(response.getBody(), "User already created.");
	}
	
	@Test
	@Order(3)
	public void postUsernameConflict()
	{
		when(userService.existsByCpf(userEntry.getCpf())).thenReturn(false);
		when(userService.existsByUsername(userEntry.getUsername())).thenReturn(true);
		
		ResponseEntity<?> response = userController.post(userEntry);
		
		verify(userService).existsByCpf(userEntry.getCpf());
		verify(userService).existsByUsername(userEntry.getUsername());
		verify(roleService, never()).findByRole(any());
		verify(userService, never()).save(any());
		
		Assertions.assertTrue(response.getStatusCode() == HttpStatus.CONFLICT);
		Assertions.assertEquals(response.getBody(),
				String.format("Username \"%s\" already in use.", userEntry.getUsername()));
	}
	
	@Test
	@Order(4)
	public void postThrowsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () -> userController.post(null));
	}
	
	@Test
	@Order(5)
	public void getAllOk()
	{
		ResponseEntity<List<UserModel>> response = userController.getAll();
		
		Assertions.assertTrue(response.getStatusCode() == HttpStatus.OK);
	}
	
	@Test
	@Order(6)
	public void getSelfOk()
	{
		UserAttributeMap map = mock(UserAttributeMap.class);
		
		Principal principal = mock(Principal.class, withSettings().extraInterfaces(Authentication.class));
		UserDetails userDetails = mock(UserDetails.class);
		Authentication authentication = (Authentication) principal;
		
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(map.isEmpty()).thenReturn(true);
		when(userDetails.getUsername()).thenReturn(userModel.getUsername());
		when(userService.findByUsername(userModel.getUsername())).thenReturn(Optional.of(userModel));
		
		ResponseEntity<UserModel> response = userController.getOneOrSelf(map, principal);
		
		UserModel body = response.getBody();
		
		verify(authentication).getPrincipal();
		verify(map).isEmpty();
		verify(userDetails).getUsername();
		verify(userService).findByUsername(userModel.getUsername());
		verify(map, never()).getUser(any());
		
		Assertions.assertTrue(response.getStatusCode() == HttpStatus.OK);
		Assertions.assertEquals(body, userModel);
	}
	
	@Test
	@Order(7)
	public void getSelfNotFound()
	{
		UserAttributeMap map = mock(UserAttributeMap.class);
		
		Principal principal = mock(Principal.class, withSettings().extraInterfaces(Authentication.class));
		UserDetails userDetails = mock(UserDetails.class);
		Authentication authentication = (Authentication) principal;
		
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(map.isEmpty()).thenReturn(true);
		
		ResponseEntity<UserModel> response = userController.getOneOrSelf(map, principal);
		
		verify(authentication).getPrincipal();
		verify(map).isEmpty();
		verify(userDetails).getUsername();
		verify(userService).findByUsername(any());
		verify(map, never()).getUser(any());
		
		Assertions.assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
		Assertions.assertNull(response.getBody());
	}
	
	@Test
	@Order(8)
	public void getOtherOk()
	{
		UserAttributeMap map = mock(UserAttributeMap.class);
		
		Principal principal = mock(Principal.class, withSettings().extraInterfaces(Authentication.class));
		UserDetails userDetails = mock(UserDetails.class);
		Authentication authentication = (Authentication) principal;
		
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(map.isEmpty()).thenReturn(false);
		when(map.getUser(userService)).thenReturn(userModel);
		
		ResponseEntity<UserModel> response = userController.getOneOrSelf(map, principal);
		
		UserModel body = response.getBody();
		
		verify(authentication).getPrincipal();
		verify(map).isEmpty();
		verify(map).getUser(userService);
		verify(userDetails, never()).getUsername();
		verify(userService, never()).findByUsername(any());
		
		Assertions.assertTrue(response.getStatusCode() == HttpStatus.OK);
		Assertions.assertEquals(body, userModel);
	}
	
	@Test
	@Order(9)
	public void getOtherNotFound()
	{
		UserAttributeMap map = mock(UserAttributeMap.class);
		
		Principal principal = mock(Principal.class, withSettings().extraInterfaces(Authentication.class));
		UserDetails userDetails = mock(UserDetails.class);
		Authentication authentication = (Authentication) principal;
		
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(map.isEmpty()).thenReturn(false);
		
		ResponseEntity<UserModel> response = userController.getOneOrSelf(map, principal);
		
		verify(authentication).getPrincipal();
		verify(map).isEmpty();
		verify(userService, never()).findByUsername(any());
		verify(userDetails, never()).getUsername();
		verify(map).getUser(userService);
		
		Assertions.assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
		Assertions.assertNull(response.getBody());
	}
	
	@Test
	@Order(10)
	public void getSelfOrOtherNPE()
	{
		assertThrows(NullPointerException.class, () ->
				userController.getOneOrSelf(null, mock(Principal.class, withSettings()
						.extraInterfaces(Authentication.class))));
		
		assertThrows(NullPointerException.class, () -> 
				userController.getOneOrSelf(mock(UserAttributeMap.class), null));
	}
	
	@Test
	@Order(11)
	public void deleteOk()
	{
		UserAttributeMap map = mock(UserAttributeMap.class);
		
		when(map.getUser(userService)).thenReturn(userModel);
		
		var response = userController.delete(map, null);
		
		verify(map).getUser(userService);
		verify(userService).delete(userModel);
		
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertEquals(response.getBody(), String.format("User %s removed sucessfully.", userModel.getUsername()));
	}
	
	@Test
	@Order(11)
	public void deleteNotFound()
	{
		UserAttributeMap map = mock(UserAttributeMap.class);
		
		var response = userController.delete(map, null);
		
		verify(map).getUser(userService);
		verify(userService, never()).delete(any());
		
		assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
		assertEquals(response.getBody(), "User not found.");
	}
	
	@Test
	@Order(12)
	public void deleteThrowsNPE()
	{
		assertThrows(NullPointerException.class, () -> userController.delete(null, mock(Principal.class)));
	}
	
	@Test
	@Order(13)
	public void deleteDoesNotThrowsNPE()
	{
		userController.delete(mock(UserAttributeMap.class), null);
	}
}
