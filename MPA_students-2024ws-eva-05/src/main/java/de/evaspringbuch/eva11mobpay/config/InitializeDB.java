package de.evaspringbuch.eva11mobpay.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.evaspringbuch.eva11mobpay.usermobpay.domain.PayUser;
import de.evaspringbuch.eva11mobpay.usermobpay.domain.PayUserRepository;
import de.evaspringbuch.eva11mobpay.usermobpay.domain.State;
import jakarta.annotation.PostConstruct;

@Component
public class InitializeDB {

    private static final Logger log = LoggerFactory.getLogger(InitializeDB.class);

    @Autowired private PayUserRepository payUserRepository;

    @PostConstruct
    public void init()  {

            log.debug("Db initialized");

            PayUser payUser = new PayUser();
            payUser.setName("pizza_bank");
            payUser.setState(State.AVAILABLE);
            payUserRepository.save(payUser);

            PayUser payUser1 = new PayUser();
            payUser1.setName("bnutz");
            payUser1.setState(State.AVAILABLE);
            payUserRepository.save(payUser1);

    }
}
