package com.apelisser.algashop.ordering.infrastructure.adapters.in.listener.customer;

import com.apelisser.algashop.ordering.core.domain.model.commons.Email;
import com.apelisser.algashop.ordering.core.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import com.apelisser.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import com.apelisser.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.AbstractPersistenceIT;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

class CustomerEventListenerIT extends AbstractPersistenceIT {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @MockitoSpyBean
    CustomerEventListener customerEventListener;

    @MockitoBean
    ForAddingLoyaltyPoints forAddingLoyaltyPoints;

    @MockitoBean
    ForConfirmCustomerRegistration forConfirmCustomerRegistration;

    @Test
    void shouldListenOrderReadyEvent() {
        applicationEventPublisher.publishEvent(new OrderReadyEvent(
            new OrderId(),
            new CustomerId(),
            OffsetDateTime.now())
        );

        Mockito.verify(customerEventListener).listen(Mockito.any(OrderReadyEvent.class));
        Mockito.verify(forAddingLoyaltyPoints).addLoyaltyPoints(
            Mockito.any(UUID.class),
            Mockito.any(String.class)
        );
    }

    @Test
    void shouldListenCustomerRegisteredEvent() {
        applicationEventPublisher.publishEvent(new CustomerRegisteredEvent(
            new CustomerId(),
            OffsetDateTime.now(),
            new FullName("John", "Doe"),
            new Email("john_doe@email.com"))
        );

        Mockito.verify(customerEventListener).listen(Mockito.any(CustomerRegisteredEvent.class));

        Mockito.verify(forConfirmCustomerRegistration).confirm(Mockito.any(UUID.class));
    }

}

