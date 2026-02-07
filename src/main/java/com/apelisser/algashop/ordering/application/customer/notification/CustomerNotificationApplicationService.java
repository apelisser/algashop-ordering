package com.apelisser.algashop.ordering.application.customer.notification;

import java.util.UUID;

public interface CustomerNotificationApplicationService {

    void notifyNewRegistration(NotifyNewRegistrationInput input);

    public record NotifyNewRegistrationInput(UUID customerId, String firstName, String email){}

}
