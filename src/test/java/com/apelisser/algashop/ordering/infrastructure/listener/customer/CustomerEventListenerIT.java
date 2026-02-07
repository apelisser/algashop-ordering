package com.apelisser.algashop.ordering.infrastructure.listener.customer;

import com.apelisser.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.apelisser.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.apelisser.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.OrderReadyEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

@SpringBootTest
class CustomerEventListenerIT {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @MockitoSpyBean
    CustomerEventListener customerEventListener;

    @MockitoBean
    CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;

    @MockitoBean
    CustomerNotificationApplicationService notificationApplicationService;

    @Test
    void shouldListenOrderReadyEvent() {
        applicationEventPublisher.publishEvent(new OrderReadyEvent(
            new OrderId(),
            new CustomerId(),
            OffsetDateTime.now())
        );

        Mockito.verify(customerEventListener).listen(Mockito.any(OrderReadyEvent.class));
        Mockito.verify(loyaltyPointsApplicationService).addLoyaltyPoints(
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

        Mockito.verify(notificationApplicationService).notifyNewRegistration(Mockito.any(NotifyNewRegistrationInput.class));
    }

}

