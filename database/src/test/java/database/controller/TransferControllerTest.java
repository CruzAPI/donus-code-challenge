package database.controller;

import static database.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import database.dto.TransferEntry;
import database.dto.UserAttributeMap;
import database.models.TransferModel;
import database.models.UserModel;
import database.services.TransferService;
import database.services.UserService;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TransferControllerTest
{
	private TransferController transferController;
	
	private UserService userService;
	private TransferService transferService;
	private Clock clock;
	private Principal principal;
	
	
	private UserModel fromUserArg;
	private UUID uuidArg;
	
	@BeforeAll
	public void beforeAll()
	{
	}
	
	@BeforeEach
	public void beforeEach()
	{
		userService = mock(UserService.class);
		transferService = mock(TransferService.class);
		clock = mock(Clock.class);
		principal = mock(Principal.class);
		
		fromUserArg = getUserModel();
		
		transferController = new TransferController(userService, transferService, clock);
		
		when(principal.getName()).thenReturn(fromUserArg.getUsername());
		
		when(clock.instant()).thenReturn(Instant.now());
		when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		when(userService.findByUsername(fromUserArg.getUsername())).thenReturn(Optional.of(fromUserArg));
		
		when(transferService.save(any(TransferModel.class))).thenAnswer(x ->
		{
			TransferModel transferModel = (TransferModel) x.getArgument(0);
			transferModel.setUuid(uuidArg = UUID.randomUUID());
			return transferModel;
		});
	}
	
	private static Stream<Arguments> putOkParameters()
	{
		return Stream.of(Arguments.of("100", "100"), Arguments.of("150", "50"), Arguments.of("200", "10"));
	}
	
	private static Stream<Arguments> putBadRequestParameters()
	{
		return Stream.of(Arguments.of("100", "101"), Arguments.of("0", "1"), Arguments.of("200", "2000"),
				Arguments.of("0", "0.01"), Arguments.of("60", "80"));
	}
	
	@ParameterizedTest
	@MethodSource("putOkParameters")
	public void putOk(String fromUserBalance, String transferEntryAmount)
	{
		fromUserArg.setBalance(new BigDecimal(fromUserBalance));
		
		ArgumentCaptor<UserModel> userCaptor = ArgumentCaptor.forClass(UserModel.class);
		
		UserAttributeMap userAttributeMap = mock(UserAttributeMap.class);
		UserModel toUserArg = getOtherUserModel();
		UserModel clonedToUserArg = toUserArg.clone();
		UserModel clonedFromUserArg = fromUserArg.clone();
		TransferEntry transferEntry = new TransferEntry(new BigDecimal(transferEntryAmount));
		
		when(userAttributeMap.getUser(userService)).thenReturn(toUserArg);
		
		var response = transferController.put(principal, userAttributeMap, transferEntry);
		
		TransferModel transferModel = new TransferModel();
		BeanUtils.copyProperties(transferEntry, transferModel);
		transferModel.setUuid(uuidArg);
		transferModel.setFromUser(fromUserArg);
		transferModel.setToUser(toUserArg);
		transferModel.setDate(LocalDateTime.now(clock));
		
		clonedFromUserArg.setBalance(clonedFromUserArg.getBalance().subtract(transferModel.getAmount()));
		clonedToUserArg.setBalance(clonedToUserArg.getBalance().add(transferModel.getAmount()));
		
		verify(principal).getName();
		verify(userService).findByUsername(principal.getName());
		verify(userAttributeMap).getUser(userService);
		verify(userService, times(2)).save(userCaptor.capture());
		verify(transferService).save(any(TransferModel.class));
		
		List<UserModel> values = userCaptor.getAllValues();
		
		UserModel fromUserCaptured = values.get(0);
		UserModel toUserCaptured = values.get(1);
		
		assertTrue(HttpStatus.OK == response.getStatusCode());
		assertEquals(transferModel, response.getBody());
		assertEquals(clonedFromUserArg, fromUserCaptured);
		assertEquals(clonedToUserArg, toUserCaptured);
	}
	
	@Test
	public void putUnauthorized()
	{
		UserAttributeMap userAttributeMap = mock(UserAttributeMap.class);
		
		when(principal.getName()).thenReturn(null);
		
		var response = transferController.put(principal, userAttributeMap, null);
		
		verify(principal).getName();
		verify(userService).findByUsername(principal.getName());
		verify(userAttributeMap, never()).getUser(any());
		verify(userService, never()).save(any());
		verify(transferService, never()).save(any());
		
		assertTrue(HttpStatus.UNAUTHORIZED == response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@Test
	public void putNotFound()
	{
		UserAttributeMap userAttributeMap = mock(UserAttributeMap.class);
		
		var response = transferController.put(principal, userAttributeMap, null);
		
		verify(principal).getName();
		verify(userService).findByUsername(principal.getName());
		verify(userAttributeMap).getUser(userService);
		verify(userService, never()).save(any());
		verify(transferService, never()).save(any());
		
		assertTrue(HttpStatus.NOT_FOUND == response.getStatusCode());
		assertEquals("User not found.", response.getBody());
	}
	
	@Test
	public void putConflict()
	{
		UserAttributeMap userAttributeMap = mock(UserAttributeMap.class);
		
		when(userAttributeMap.getUser(userService)).thenReturn(fromUserArg);
		
		var response = transferController.put(principal, userAttributeMap, null);
		
		verify(principal).getName();
		verify(userService).findByUsername(principal.getName());
		verify(userAttributeMap).getUser(userService);
		verify(userService, never()).save(any());
		verify(transferService, never()).save(any());
		
		assertTrue(HttpStatus.CONFLICT == response.getStatusCode());
		assertEquals("You can't transfer to yourself.", response.getBody());
	}
	
	@ParameterizedTest
	@MethodSource("putBadRequestParameters")
	@DisplayName("Insufficient balance.")
	public void putBadRequest(String fromUserBalance, String transferEntryAmount)
	{
		fromUserArg.setBalance(new BigDecimal(fromUserBalance));
		
		UserAttributeMap userAttributeMap = mock(UserAttributeMap.class);
		UserModel toUserArg = getOtherUserModel();
		
		TransferEntry transferEntry = new TransferEntry(new BigDecimal(transferEntryAmount));
		
		when(userAttributeMap.getUser(userService)).thenReturn(toUserArg);
		
		var response = transferController.put(principal, userAttributeMap, transferEntry);
		
		assertTrue(HttpStatus.BAD_REQUEST == response.getStatusCode());
		assertEquals("Insufficient balance.", response.getBody());
	}
	
	@Test
	public void putThrowsNullPointerException()
	{
		assertThrows(NullPointerException.class,
				() -> transferController.put(null, mock(UserAttributeMap.class), mock(TransferEntry.class)));
		
		assertThrows(NullPointerException.class,
				() -> transferController.put(principal, null, mock(TransferEntry.class)));
	}
}
