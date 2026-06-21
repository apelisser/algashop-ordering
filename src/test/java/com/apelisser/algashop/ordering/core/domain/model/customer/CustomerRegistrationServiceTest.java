package com.apelisser.algashop.ordering.core.domain.model.customer;

import com.apelisser.algashop.ordering.core.domain.model.commons.Address;
import com.apelisser.algashop.ordering.core.domain.model.commons.Document;
import com.apelisser.algashop.ordering.core.domain.model.commons.Email;
import com.apelisser.algashop.ordering.core.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.core.domain.model.commons.Phone;
import com.apelisser.algashop.ordering.core.domain.model.commons.ZipCode;
import com.apelisser.algashop.ordering.core.domain.model.customer.BirthDate;
import com.apelisser.algashop.ordering.core.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.core.domain.model.customer.Customers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    @Mock
    Customers customers;

    @InjectMocks
    CustomerAlreadyHaveShoppingCartException.CustomerRegistrationService customerRegistrationService;

    @Test
    void shouldRegister() {
        Mockito.when(customers.isEmailUnique(Mockito.any(Email.class), Mockito.any(CustomerId.class)))
            .thenReturn(true);

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