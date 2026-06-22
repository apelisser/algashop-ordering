package com.apelisser.algashop.ordering.infrastructure.adapters.out.notification.customer;

import com.apelisser.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ForNotifyingCustomersFakeImpl implements ForNotifyingCustomers {

    private static final Logger log = LoggerFactory.getLogger(ForNotifyingCustomersFakeImpl.class);

    @Override
    public void notifyNewRegistration(NotifyNewRegistrationInput input) {
        log.info("Welcome {}", input.firstName());
        log.info("Use your email to access your account {}", input.email());
    }

}
