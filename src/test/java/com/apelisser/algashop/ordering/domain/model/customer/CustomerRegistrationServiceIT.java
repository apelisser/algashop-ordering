package com.apelisser.algashop.ordering.domain.model.customer;

import com.apelisser.algashop.ordering.domain.model.commons.Address;
import com.apelisser.algashop.ordering.domain.model.commons.Document;
import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.commons.Phone;
import com.apelisser.algashop.ordering.domain.model.commons.ZipCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class CustomerRegistrationServiceIT {

    @Autowired
    CustomerAlreadyHaveShoppingCartException.CustomerRegistrationService customerRegistrationService;

    @Test
    void shouldRegister() {
        Customer customer = customerRegistrationService.register(
            new FullName("John", "Doe"),
            new BirthDate(LocalDate.of(1991, 7, 5)),
            new Email("john.doe@example.com"),
            new Phone("478-259-2604"),
            new Document("255-08-0578"),
            true,
            Address.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("North Ville")
                .city("Yostfort")
                .state("South Carolina")
                .zipCode(new ZipCode("70283"))
                .complement("Apt. 901")
                .build()

        );

        Assertions.assertThat(customer.fullName()).isEqualTo(new FullName("John", "Doe"));
        Assertions.assertThat(customer.email()).isEqualTo(new Email("john.doe@example.com"));
    }

}