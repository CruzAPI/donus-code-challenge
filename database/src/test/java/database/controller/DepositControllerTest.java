package database.controller;

import static database.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import database.dto.DepositEntry;
import database.models.DepositModel;
import database.models.UserModel;
import database.services.DepositService;
import database.services.UserService;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class DepositControllerTest
{
	private DepositController depositController;
	
	private UserService userService;
	private DepositService depositService;
	private Clock clock;
	private Principal principal;
	
	private DepositEntry depositEntry;
	
	private UserModel userArgument;
	private UUID uuidArgument;
	
	@Captor
	private ArgumentCaptor<UserModel> userCaptor;
	
	@BeforeAll
	public void beforeAll()
	{
		depositEntry = new DepositEntry();
		depositEntry.setAmount(new BigDecimal("100"));
	}
	
	@BeforeEach
	public void beforeEach()
	{
		userService = mock(UserService.class);
		depositService = mock(DepositService.class);
		clock = mock(Clock.class);
		principal = mock(Principal.class);
		userArgument = getUserModel();
		
		depositController = new DepositController(clock, userService, depositService);
		
		when(principal.getName()).thenReturn(userArgument.getUsername());
		
		when(clock.instant()).thenReturn(Instant.now());
		when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		when(userService.findByUsername(userArgument.getUsername())).thenReturn(Optional.of(userArgument));
		
		when(depositService.save(any(DepositModel.class))).thenAnswer(x ->
		{
			DepositModel depositModel = (DepositModel) x.getArgument(0);
			depositModel.setUuid(uuidArgument = UUID.randomUUID());
			return depositModel;
		});
	}
	
	@Test
	public void putOk()
	{
		UserModel userModel = userArgument.clone();
		
		userModel.setBalance(userModel.getBalance().add(depositEntry.getAmount()));
		
		var response = depositController.put(principal, depositEntry);
		
		DepositModel depositModel = new DepositModel();
		BeanUtils.copyProperties(depositEntry, depositModel);
		
		depositModel.setUuid(uuidArgument);
		depositModel.setDate(LocalDateTime.now(clock));
		depositModel.setUser(userArgument);
		
		verify(principal).getName();
		verify(userService).findByUsername(principal.getName());
		verify(userService).save(userCaptor.capture());
		verify(depositService).save(any(DepositModel.class));
		
		DepositModel body = (DepositModel) response.getBody();
		
		assertTrue(userArgument == userCaptor.getValue());
		assertTrue(userArgument == body.getUser());
		assertTrue(HttpStatus.OK == response.getStatusCode());
		assertEquals(userModel, userArgument);
		assertEquals(depositModel, body);
	}
	
	@Test
	public void putUnauthorized()
	{
		Principal principal = mock(Principal.class);
		
		var response = depositController.put(principal, depositEntry);
		
		verify(principal).getName();
		verify(userService).findByUsername(null);
		verify(userService, never()).save(any());
		verify(depositService, never()).save(any());
		
		assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED);
		assertNull(response.getBody());
	}
	
	@Test
	public void putThrowsException()
	{
		assertThrows(NullPointerException.class, () -> depositController.put(null, depositEntry));
		assertThrows(IllegalArgumentException.class, () -> depositController.put(principal, null));
	}
}
