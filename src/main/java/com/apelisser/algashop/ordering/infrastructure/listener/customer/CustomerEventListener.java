package com.apelisser.algashop.ordering.infrastructure.listener.customer;

import com.apelisser.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.apelisser.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.apelisser.algashop.ordering.domain.model.order.OrderReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);

    private final CustomerNotificationApplicationService customerNotificationApplicationService;
    private final CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    public CustomerEventListener(CustomerNotificationApplicationService customerNotificationApplicationService,
            CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService) {
        this.customerNotificationApplicationService = customerNotificationApplicationService;
        this.customerLoyaltyPointsApplicationService = customerLoyaltyPointsApplicationService;
    }

    @EventListener
    public void listen(CustomerRegisteredEvent event) {
        log.info("Received CustomerRegisteredEvent [listen 01) {}", event);
        var input = new CustomerNotificationApplicationService.NotifyNewRegistrationInput(
            event.customerId().value(),
            event.fullName().firstName(),
            event.email().value());

        customerNotificationApplicationService.notifyNewRegistration(input);
    }

    @EventListener
    public void listen(CustomerArchivedEvent event) {
        log.info("Received CustomerArchivedEvent [listen 01) {}", event);
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        customerLoyaltyPointsApplicationService.addLoyaltyPoints(
            event.customerId().value(),
            event.orderId().toString());
    }

}
