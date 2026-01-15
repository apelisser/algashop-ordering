package com.apelisser.algashop.ordering.domain.model.customer;

import com.apelisser.algashop.ordering.domain.model.DomainException;
import com.apelisser.algashop.ordering.domain.model.commons.Address;
import com.apelisser.algashop.ordering.domain.model.commons.Document;
import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.commons.Phone;
import com.apelisser.algashop.ordering.domain.model.DomainService;

import java.io.Serial;

public class CustomerAlreadyHaveShoppingCartException extends DomainException {

    @Serial
    private static final long serialVersionUID = -2391769211223564401L;

    @DomainService
    public static class CustomerRegistrationService {

        private final Customers customers;

        public CustomerRegistrationService(Customers customers) {
            this.customers = customers;
        }

        public Customer register(FullName fullName, BirthDate birthDate, Email email, Phone phone, Document document,
                Boolean promotionNotificationsAllowed, Address address) {
            Customer customer = Customer.brandNew()
                .fullName(fullName)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .document(document)
                .promotionNotificationsAllowed(promotionNotificationsAllowed)
                .address(address)
                .build();

            verifyEmailUniqueness(customer.email(), customer.id());

            return customer;
        }

        public void changeEmail(Customer customer, Email newEmail) {
            verifyEmailUniqueness(newEmail, customer.id());
            customer.changeEmail(newEmail);
        }

        private void verifyEmailUniqueness(Email email, CustomerId customerId) {
            if (!customers.isEmailUnique(email, customerId)) {
                throw new CustomerEmailIsInUseException();
            }
        }

    }

}
