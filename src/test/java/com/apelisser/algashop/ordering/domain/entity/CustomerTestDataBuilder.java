package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.valueobject.Address;
import com.apelisser.algashop.ordering.domain.valueobject.BirthDate;
import com.apelisser.algashop.ordering.domain.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.valueobject.Document;
import com.apelisser.algashop.ordering.domain.valueobject.Email;
import com.apelisser.algashop.ordering.domain.valueobject.FullName;
import com.apelisser.algashop.ordering.domain.valueobject.LoyaltyPoints;
import com.apelisser.algashop.ordering.domain.valueobject.Phone;
import com.apelisser.algashop.ordering.domain.valueobject.ZipCode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerTestDataBuilder {

    public static CustomerId DEFAULT_CUSTOMER_ID = new CustomerId();

    private CustomerTestDataBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Customer.BrandNewCustomerBuilder brandNewCustomer() {
        return Customer.brandNew()
            .fullName(new FullName("John", "Doe"))
            .birthDate(new BirthDate(LocalDate.of(1991, 7, 5)))
            .email(new Email("john.doe@example.com"))
            .phone(new Phone("478-256-2504"))
            .document(new Document("255-08-0578"))
            .promotionNotificationsAllowed(false)
            .address(
                Address.builder()
                    .street("Bourbon Street")
                    .number("1234")
                    .neighborhood("North Ville")
                    .city("York")
                    .state("South California")
                    .zipCode(new ZipCode("12345"))
                    .complement("Apt. 114")
                    .build());
    }

    public static Customer.ExistingCustomerBuilder existingCustomer() {
        return Customer.existing()
            .id(DEFAULT_CUSTOMER_ID)
            .fullName(new FullName("John", "Doe"))
            .birthDate(new BirthDate(LocalDate.of(1991, 7, 5)))
            .email(new Email("john.doe@example.com"))
            .phone(new Phone("478-256-2504"))
            .document(new Document("255-08-0578"))
            .promotionNotificationsAllowed(false)
            .archived(false)
            .registeredAt(OffsetDateTime.now())
            .archivedAt(null)
            .loyaltyPoints(new LoyaltyPoints(10))
            .address(Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .complement("Apt. 114")
                .build());
    }

    public static Customer.ExistingCustomerBuilder existingAnonymizedCustomer() {
        return Customer.existing()
            .id(DEFAULT_CUSTOMER_ID)
            .fullName(new FullName("Anonymous", "Anonymous"))
            .birthDate(null)
            .email(new Email(UUID.randomUUID() + "@anonymous.com"))
            .phone(new Phone("000-000-0000"))
            .document(new Document("000-00-0000"))
            .promotionNotificationsAllowed(false)
            .archived(true)
            .registeredAt(OffsetDateTime.now())
            .archivedAt(OffsetDateTime.now())
            .loyaltyPoints(new LoyaltyPoints(10))
            .address(Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .complement("Apt. 114")
                .build());
    }

}
