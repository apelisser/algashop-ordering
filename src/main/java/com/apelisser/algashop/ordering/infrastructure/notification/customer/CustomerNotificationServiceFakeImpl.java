package com.apelisser.algashop.ordering.infrastructure.notification.customer;

import com.apelisser.algashop.ordering.application.customer.notification.CustomerNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CustomerNotificationServiceFakeImpl.class);

    @Override
    public void notifyNewRegistration(NotifyNewRegistrationInput input) {
        log.info("Welcome {}", input.firstName());
        log.info("Use your email to access your account {}", input.email());
    }

}
