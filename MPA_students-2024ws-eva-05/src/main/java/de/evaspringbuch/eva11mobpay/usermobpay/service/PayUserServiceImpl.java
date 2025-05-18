package de.evaspringbuch.eva11mobpay.usermobpay.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.evaspringbuch.eva11mobpay.usermobpay.domain.PayUser;
import de.evaspringbuch.eva11mobpay.usermobpay.domain.PayUserRepository;
import de.evaspringbuch.eva11mobpay.usermobpay.domain.State;

@Service
public class PayUserServiceImpl implements PayUserService {

	private static final Logger log = LoggerFactory.getLogger(PayUserServiceImpl.class);

	@Autowired
	private PayUserRepository payUserRepository;

	@Override
	public double getAccountBalanceByName(String userId) {
		try {
			PayUser payUser = payUserRepository.findByName(userId)
					.orElseThrow(() -> new PayUserException("user cannot be found"));
			return payUser.getAccount().getBalance();
		} catch (PayUserException e) {
			e.printStackTrace();
			return -101010;
		}
	}

	@Override
	public boolean containsAndAvailable(String userId) {
		return payUserRepository.existsByNameAndAvailable(userId);
	}

	@Override
	public State getState(String userId) {
		Optional<PayUser> userPayOptional = payUserRepository.findByName(userId);
		return userPayOptional.map(PayUser::getState).orElse(State.DOES_NOT_EXIST);
	}

	@Override
	public void openAccount(String userId) {
		PayUser payUser = payUserRepository.findByName(userId).orElse(new PayUser(userId));
		payUser.setState(State.AVAILABLE);
		payUserRepository.save(payUser);
	}

	@Override
	@Transactional
	public String transfer(String from, String to, double amount) throws PayUserException {
		System.out.println("     PayUSerServiceImpl  transfer ");
		if (containsAndAvailable(from) && containsAndAvailable(to)) {
			System.out.println(
					"     PayUSerServiceImpl  transfer     containsAndAvailable(from) && containsAndAvailable(to)");

			PayUser payUserFrom = payUserRepository.findByName(from)
					.orElseThrow(() -> new PayUserException("user cannot be found"));

			if (payUserFrom.getAccount().getBalance() < amount) {
				throw new PayUserException("Insufficient funds in the sender's account");
			}

			payUserFrom.getAccount().withdrawBalance(amount);

			PayUser payUserTo = payUserRepository.findByName(to)
					.orElseThrow(() -> new PayUserException("user cannot be found"));
			payUserTo.getAccount().depositBalance(amount);

			payUserRepository.save(payUserFrom);
			payUserRepository.save(payUserTo);
			return "okay";
		} else {
			return "transferNotAllowed";
		}
	}



	@Override
	public boolean deleteUser(String userId) {
		return payUserRepository.findByName(userId).map(payUser -> {
			payUser.setState(State.DOES_NOT_EXIST);
			payUser.getAccount().setBalance(0);
			payUserRepository.save(payUser);
			return true;
		}).orElse(false);
	}

	@Override
	public void changeUserToSuspendedState(String userId) {
		payUserRepository.findByName(userId).ifPresent((userPay) -> {
			userPay.setState(State.SUSPENDED);
			payUserRepository.save(userPay);
		});
	}

	@Override
	public PayUser getPayUser(String userId) {
		return payUserRepository.findByName(userId).orElse(null);
	}
}
