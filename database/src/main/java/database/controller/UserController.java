package database.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import database.dto.UserAttributeMap;
import database.dto.UserEntry;
import database.models.RoleModel;
import database.models.UserModel;
import database.security.Role;
import database.security.SecurityConfig;
import database.services.RoleService;
import database.services.UserService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600L)
@AllArgsConstructor
public class UserController
{
	private RoleService roleService;
	private SecurityConfig securityConfig;
	private UserService userService;
	
	@DeleteMapping("/user")
	@PreAuthorize("hasRole('ADMIN') || @authComponent.equals(#map, #principal)")
	public ResponseEntity<Object> delete(UserAttributeMap map, Principal principal)
	{
		UserModel user = map.getUser(userService);
		
		if(user == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
		
		userService.delete(user);
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(String.format("User %s removed sucessfully.", user.getUsername()));
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('ADMIN') || @authComponent.equals(#map, #principal)")
	public ResponseEntity<UserModel> getOneOrSelf(UserAttributeMap map, Principal principal)
	{
		UserDetails userDetails = (UserDetails) ((Authentication) principal).getPrincipal();
		
		UserModel user = map.isEmpty() ? userService.findByUsername(userDetails.getUsername()).orElse(null)
				: map.getUser(userService);
		
		if(user == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserModel>> getAll()
	{
		return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
	}
	
	@PostMapping
	public ResponseEntity<Object> post(@RequestBody @Valid UserEntry userEntry)
	{
		UserModel userModel = new UserModel();
		BeanUtils.copyProperties(userEntry, userModel);
		userModel.setPassword(securityConfig.passwordEncoder().encode(userEntry.getPassword()));
		
		if(userService.existsByCpf(userModel.getCpf()))
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User already created.");
		}
		
		if(userService.existsByUsername(userModel.getUsername()))
		{
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(String.format("Username \"%s\" already in use.", userModel.getUsername()));
		}
		
		Optional<RoleModel> optional = roleService.findByRole(Role.ROLE_USER);
		
		if(optional.isPresent())
		{
			userModel.setRoles(List.of(optional.get()));
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userModel));
	}
}