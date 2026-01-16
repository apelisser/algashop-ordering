package com.apelisser.algashop.ordering.application.service;

import com.apelisser.algashop.ordering.application.model.AddressData;
import com.apelisser.algashop.ordering.application.model.CustomerInput;
import com.apelisser.algashop.ordering.application.model.CustomerOutput;
import com.apelisser.algashop.ordering.domain.model.commons.Address;
import com.apelisser.algashop.ordering.domain.model.commons.Document;
import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.commons.Phone;
import com.apelisser.algashop.ordering.domain.model.commons.ZipCode;
import com.apelisser.algashop.ordering.domain.model.customer.BirthDate;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class CustomerManagementApplicationService {

    private final CustomerRegistrationService customerRegistration;
    private final Customers customers;

    public CustomerManagementApplicationService(CustomerRegistrationService customerRegistration, Customers customers) {
        this.customerRegistration = customerRegistration;
        this.customers = customers;
    }

    @Transactional
    public UUID create(CustomerInput input) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(input.getAddress());

        AddressData inputAddress = input.getAddress();

        Customer customer = customerRegistration.register(
            new FullName(input.getFirstName(), input.getLastName()),
            new BirthDate(input.getBirthDate()),
            new Email(input.getEmail()),
            new Phone(input.getPhone()),
            new Document(input.getDocument()),
            input.getPromotionNotificationsAllowed(),
            Address.builder()
                .street(inputAddress.getStreet())
                .complement(inputAddress.getComplement())
                .neighborhood(inputAddress.getNeighborhood())
                .number(inputAddress.getNumber())
                .city(inputAddress.getCity())
                .state(inputAddress.getState())
                .zipCode(new ZipCode(inputAddress.getZipCode()))
                .build());

        customers.add(customer);

        return customer.id().value();
    }

    @Transactional(readOnly = true)
    public CustomerOutput findById(UUID customerId) {
        Objects.requireNonNull(customerId);

        Customer customer = customers.ofId(new CustomerId(customerId))
            .orElseThrow(CustomerNotFoundException::new);

        return CustomerOutput.builder()
            .id(customer.id().value())
            .firstName(customer.fullName().firstName())
            .lastName(customer.fullName().lastName())
            .email(customer.email().value())
            .document(customer.document().value())
            .phone(customer.phone().value())
            .birthDate(customer.birthDate() != null ? customer.birthDate().value() : null)
            .promotionNotificationsAllowed(customer.isPromotionNotificationsAllowed())
            .loyaltyPoints(customer.loyaltyPoints().value())
            .registeredAt(customer.registeredAt())
            .archivedAt(customer.archivedAt())
            .address(AddressData.builder()
                .street(customer.address().street())
                .number(customer.address().number())
                .complement(customer.address().complement())
                .neighborhood(customer.address().neighborhood())
                .city(customer.address().city())
                .state(customer.address().state())
                .zipCode(customer.address().zipCode().value())
                .build())
            .build();
    }


}
