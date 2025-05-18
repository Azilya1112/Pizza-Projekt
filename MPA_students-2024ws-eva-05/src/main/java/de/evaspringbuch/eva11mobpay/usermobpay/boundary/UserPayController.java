package de.evaspringbuch.eva11mobpay.usermobpay.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.evaspringbuch.eva11mobpay.usermobpay.domain.PayUser;
import de.evaspringbuch.eva11mobpay.usermobpay.domain.State;
import de.evaspringbuch.eva11mobpay.usermobpay.domain.PayUserRepository;
import de.evaspringbuch.eva11mobpay.usermobpay.service.PayUserException;
import de.evaspringbuch.eva11mobpay.usermobpay.service.PayUserService;
import de.evaspringbuch.eva11mobpay.usermobpay.service.dto.AccountResponseDTO;
import de.evaspringbuch.eva11mobpay.usermobpay.service.dto.PayUserResponseDTO;
import de.evaspringbuch.eva11mobpay.usermobpay.service.dto.TransferDTO;

import java.util.List; 
import java.util.stream.Collectors; 
import org.springframework.http.ResponseEntity; 


@RestController
@RequestMapping(value = "/users")
public class UserPayController {

//    private static final Logger log = LoggerFactory.getLogger(UserPayController.class);

	private PayUserService payUserService;
	private final PayUserRepository payUserRepository;

	@Autowired
	public UserPayController(PayUserService payUserService, PayUserRepository payUserRepository) {
		this.payUserService = payUserService;
		this.payUserRepository = payUserRepository;
	}
	


	@GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listPayUser(@PathVariable String userId) {
		PayUser payUser = payUserService.getPayUser(userId);
		if (payUser != null) {
			PayUserResponseDTO payUserResponseDTO = new PayUserResponseDTO(payUser);
//        payUserResponseDTO.add(linkTo(UserPayController.class).slash(userId).withSelfRel());
//        payUserResponseDTO.add(linkTo(methodOn(UserPayController.class).listAccountBalance(userId)).withRel("account"));
//        payUserResponseDTO.add(linkTo(methodOn(UserPayController.class).changeUserToSuspendedState(userId, "suspended")).withRel("suspend"));
			return ResponseEntity.ok(payUserResponseDTO);
		} else
			return ResponseEntity.notFound().build();

	}

	@GetMapping(value = "/{userId}/account", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> listAccountBalance(@PathVariable String userId) {
		State payUserState = payUserService.getState(userId);
			if (payUserState == State.AVAILABLE) {// return new ResponseEntity<Authenticator.Success>(HttpStatus.OK);
				double balance = payUserService.getAccountBalanceByName(userId);
				return ResponseEntity.ok(new AccountResponseDTO("Kontostand betraegt " + balance));
			} 
			else {
				return ResponseEntity.internalServerError()
						.body(new AccountResponseDTO("transferNotAllowed"));
			}
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PayUserResponseDTO>> getAllPayUsers() {
	    List<PayUser> payUsers = payUserRepository.findAll();
	    List<PayUserResponseDTO> response = payUsers.stream()
	            .map(PayUserResponseDTO::new)
	            .collect(Collectors.toList());
	    return ResponseEntity.ok(response);
	}



	@PutMapping("/{userId}/opened")
	public ResponseEntity<?> openAccount(@PathVariable String userId) {
		payUserService.openAccount(userId);
		return ResponseEntity.ok(new AccountResponseDTO("Konto steht nun zur Verfuegung"));
	}

	@PostMapping(value = "/{userId}/payment", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> transfer(@PathVariable String userId, @RequestBody TransferDTO input) {
		System.out.println("   transfer payment ::  " + input.toString());

		String to = input.to();
		double amount = input.amount();
		String returnStatus;
		try {
			returnStatus = payUserService.transfer(userId, to, amount);
		} catch (PayUserException e) {
			if (e.getMessage().contains("Insufficient funds")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new AccountResponseDTO("Insufficient funds in the sender's account"));
			}
			return ResponseEntity.internalServerError()
					.body(new AccountResponseDTO(e.getMessage()));
		}

		if ("okay".equals(returnStatus)) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new AccountResponseDTO("Transfer completed successfully"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AccountResponseDTO("Transfer not allowed"));
		}
	}



	@DeleteMapping(value = "/{userId}/deleted", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteUser(@PathVariable String userId) {
		payUserService.deleteUser(userId);
		return ResponseEntity.ok(new AccountResponseDTO("Nutzer und Konto ist nun geloescht"));
	}

	@PutMapping(value = "/{userId}/suspended", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> changeUserToSuspendedState(@PathVariable String userId, @RequestBody String state) {
		if (State.valueOf(state.toUpperCase()) == State.SUSPENDED) {
			payUserService.changeUserToSuspendedState(userId);
			return ResponseEntity.ok(new AccountResponseDTO("Konto ist deaktiviert"));
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new AccountResponseDTO("Falscher Zustand eingegeben"));
	}

}
