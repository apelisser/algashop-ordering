package com.apelisser.algashop.ordering.domain.model.service;

import com.apelisser.algashop.ordering.domain.model.entity.Customer;
import com.apelisser.algashop.ordering.domain.model.repository.Customers;
import com.apelisser.algashop.ordering.domain.model.valueobject.Address;
import com.apelisser.algashop.ordering.domain.model.valueobject.BirthDate;
import com.apelisser.algashop.ordering.domain.model.valueobject.Document;
import com.apelisser.algashop.ordering.domain.model.valueobject.Email;
import com.apelisser.algashop.ordering.domain.model.valueobject.FullName;
import com.apelisser.algashop.ordering.domain.model.valueobject.Phone;
import com.apelisser.algashop.ordering.domain.model.valueobject.ZipCode;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class CustomerRegistrationServiceIT {

    Customers customers;

    @Autowired
    CustomerRegistrationService customerRegistrationService;

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