package com.apelisser.algashop.ordering.infrastructure.notification.customer;

import com.apelisser.algashop.ordering.application.customer.notification.CustomerNotificationService;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CustomerNotificationServiceFakeImpl.class);

    private final Customers customers;

    public CustomerNotificationServiceFakeImpl(Customers customers) {
        this.customers = customers;
    }

    @Override
    public void notifyNewRegistration(UUID customerId) {
        Customer customer = customers.ofId(new CustomerId(customerId))
            .orElseThrow(CustomerNotFoundException::new);

        log.info("Welcome {}", customer.fullName().firstName());
        log.info("Use your email to access your account {}", customer.email());
    }

}
