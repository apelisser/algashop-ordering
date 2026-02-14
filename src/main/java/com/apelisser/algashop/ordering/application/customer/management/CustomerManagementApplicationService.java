package com.apelisser.algashop.ordering.application.customer.management;

import com.apelisser.algashop.ordering.application.commons.AddressData;
import com.apelisser.algashop.ordering.application.utility.Mapper;
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

    @Transactional
    public void update(UUID rawCustomerId, CustomerUpdateInput input) {
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(input);
        Customer customer = customers.ofId(new CustomerId(rawCustomerId)).orElseThrow(CustomerNotFoundException::new);

        customer.changeName(new FullName(input.getFirstName(), input.getLastName()));
        customer.changePhone(new Phone(input.getPhone()));

        if (Boolean.TRUE.equals(input.getPromotionNotificationsAllowed())) {
            customer.enablePromotionNotifications();
        } else {
            customer.disablePromotionNotifications();
        }

        AddressData inputAddress = input.getAddress();
        customer.changeAddress(Address.builder()
            .street(inputAddress.getStreet())
            .complement(inputAddress.getComplement())
            .neighborhood(inputAddress.getNeighborhood())
            .number(inputAddress.getNumber())
            .city(inputAddress.getCity())
            .state(inputAddress.getState())
            .zipCode(new ZipCode(inputAddress.getZipCode()))
            .build());

        customers.add(customer);
    }

    @Transactional
    public void archive(UUID customerId) {
        Objects.requireNonNull(customerId);
        Customer customer = customers.ofId(new CustomerId(customerId)).orElseThrow(CustomerNotFoundException::new);
        customer.archive();
        customers.add(customer);
    }

    @Transactional
    public void changeEmail(UUID customerId, String email) {
        Objects.requireNonNull(customerId);
        Customer customer = customers.ofId(new CustomerId(customerId)).orElseThrow(CustomerNotFoundException::new);
        customerRegistration.changeEmail(customer, new Email(email));
        customers.add(customer);
    }

}
