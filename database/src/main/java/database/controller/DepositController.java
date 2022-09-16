package database.controller;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import database.dto.DepositEntry;
import database.models.DepositModel;
import database.models.UserModel;
import database.services.DepositService;
import database.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/deposit")
@CrossOrigin(value = "*", maxAge = 3600L)
@RequiredArgsConstructor
public class DepositController
{
	private final Clock clock;
	private final UserService userService;
	private final DepositService depositService;
	
	@PutMapping
	public ResponseEntity<Object> put(Principal principal, @RequestBody @Valid DepositEntry depositEntry)
	{
		UserModel user = userService.findByUsername(principal.getName()).orElse(null);
		
		if(user == null)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		DepositModel depositModel = new DepositModel();
		BeanUtils.copyProperties(depositEntry, depositModel);
		
		depositModel.setUser(user);
		depositModel.setDate(LocalDateTime.now(clock));
		
		user.setBalance(user.getBalance().add(depositModel.getAmount()));
		
		userService.save(user);
		
		return ResponseEntity.status(HttpStatus.OK).body(depositService.save(depositModel));
	}
}
