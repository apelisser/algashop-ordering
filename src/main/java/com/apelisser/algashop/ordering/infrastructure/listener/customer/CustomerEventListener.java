package com.apelisser.algashop.ordering.infrastructure.listener.customer;

import com.apelisser.algashop.ordering.application.customer.notification.CustomerNotificationService;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);

    private final CustomerNotificationService customerNotificationService;

    public CustomerEventListener(CustomerNotificationService customerNotificationService) {
        this.customerNotificationService = customerNotificationService;
    }

    @EventListener
    public void listen(CustomerRegisteredEvent event) {
        log.info("Received CustomerRegisteredEvent [listen 01) {}", event);
        var input = new CustomerNotificationService.NotifyNewRegistrationInput(
            event.customerId().value(),
            event.fullName().firstName(),
            event.email().value());

        customerNotificationService.notifyNewRegistration(input);
    }

    @EventListener
    public void listen(CustomerArchivedEvent event) {
        log.info("Received CustomerArchivedEvent [listen 01) {}", event);
    }

}
