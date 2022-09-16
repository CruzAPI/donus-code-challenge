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

import database.dto.TransferEntry;
import database.dto.UserAttributeMap;
import database.models.TransferModel;
import database.models.UserModel;
import database.services.TransferService;
import database.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transfer")
@CrossOrigin(value = "*", maxAge = 3600L)
@RequiredArgsConstructor
public class TransferController
{
	private final UserService userService;
	private final TransferService transferService;
	private final Clock clock;
	
	@PutMapping
	public ResponseEntity<Object> put(Principal principal, @Valid UserAttributeMap map,
			@RequestBody @Valid TransferEntry transferEntry)
	{
		UserModel fromUser = userService.findByUsername(principal.getName()).orElse(null);
		
		if(fromUser == null)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		UserModel toUser = map.getUser(userService);
		
		if(toUser == null)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User not found."));
		}
		
		if(fromUser == toUser)
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body("You can't transfer to yourself.");
		}
		
		TransferModel transferModel = new TransferModel();
		BeanUtils.copyProperties(transferEntry, transferModel);
		
		transferModel.setFromUser(fromUser);
		transferModel.setToUser(toUser);
		transferModel.setDate(LocalDateTime.now(clock));
		
		if(fromUser.getBalance().compareTo(transferModel.getAmount()) == -1)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance.");
		}
		
		fromUser.setBalance(fromUser.getBalance().subtract(transferModel.getAmount()));
		toUser.setBalance(toUser.getBalance().add(transferModel.getAmount()));

		userService.save(fromUser);
		userService.save(toUser);
		
		return ResponseEntity.status(HttpStatus.OK).body(transferService.save(transferModel));
	}
}
