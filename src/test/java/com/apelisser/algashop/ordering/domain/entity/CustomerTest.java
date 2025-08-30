package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.utility.IdGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testingCustomer() {
        Customer customer = new Customer(
            IdGenerator.generateTimeBasedUUID(),
            "John Doe",
            LocalDate.of(1991, 7, 5),
            "john@example.com",
            "123456789",
            "12345",
            true,
            OffsetDateTime.now()
        );

        customer.addLoyaltyPoints(10);
    }

}