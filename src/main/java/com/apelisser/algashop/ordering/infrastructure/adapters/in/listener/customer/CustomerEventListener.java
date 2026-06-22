package com.apelisser.algashop.ordering.infrastructure.adapters.in.listener.customer;

import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerArchivedEvent;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import com.apelisser.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import com.apelisser.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);

    private final ForConfirmCustomerRegistration forConfirmCustomerRegistration;
    private final ForAddingLoyaltyPoints forAddingLoyaltyPoints;

    public CustomerEventListener(ForConfirmCustomerRegistration forConfirmCustomerRegistration,
            ForAddingLoyaltyPoints forAddingLoyaltyPoints) {
        this.forConfirmCustomerRegistration = forConfirmCustomerRegistration;
        this.forAddingLoyaltyPoints = forAddingLoyaltyPoints;
    }

    @EventListener
    public void listen(CustomerRegisteredEvent event) {
        log.info("Received CustomerRegisteredEvent [listen 01) {}", event);
        forConfirmCustomerRegistration.confirm(event.customerId().value());
    }

    @EventListener
    public void listen(CustomerArchivedEvent event) {
        log.info("Received CustomerArchivedEvent [listen 01) {}", event);
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        forAddingLoyaltyPoints.addLoyaltyPoints(
            event.customerId().value(),
            event.orderId().toString());
    }

}
