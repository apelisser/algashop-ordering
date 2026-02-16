package com.apelisser.algashop.ordering.application.customer.query;

import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@SpringBootTest
@Transactional
class CustomerQueryServiceIT {

    @Autowired
    CustomerQueryService queryService;

    @Autowired
    Customers customers;

    @BeforeEach
    void setUp() {
        customers.add(CustomerTestDataBuilder.existingCustomer()
            .id(new CustomerId())
            .fullName(new FullName("Mark", "Doe"))
            .email(new Email("mark.doe@example.com"))
            .registeredAt(OffsetDateTime.now().minusDays(4))
            .build());

        customers.add(CustomerTestDataBuilder.existingCustomer()
            .id(new CustomerId())
            .fullName(new FullName("Lily", "Doe"))
            .email(new Email("lily.doe@example.com"))
            .registeredAt(OffsetDateTime.now().minusDays(3))
            .build());

        customers.add(CustomerTestDataBuilder.existingCustomer()
            .id(new CustomerId())
            .fullName(new FullName("John", "Doe"))
            .email(new Email("john.doe@example.com"))
            .registeredAt(OffsetDateTime.now().minusDays(2))
            .build());

        customers.add(CustomerTestDataBuilder.existingCustomer()
            .id(new CustomerId())
            .fullName(new FullName("Alice", "Doe"))
            .email(new Email("alice.doe@example.com"))
            .registeredAt(OffsetDateTime.now().minusDays(1))
            .build());
    }

    @Test
    void shouldFindCustomerByFirstName() {
        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("Li");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(page.getContent())
            .extracting(CustomerSummaryOutput::getFirstName)
            .containsExactlyInAnyOrder("Alice", "Lily");
    }

    @Test
    void shouldFindCustomerByEmail() {
        CustomerFilter filter = new CustomerFilter();
        filter.setEmail("Li");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(page.getContent())
            .extracting(CustomerSummaryOutput::getEmail)
            .containsExactlyInAnyOrder("lily.doe@example.com", "alice.doe@example.com");
    }

    @Test
    void shouldSearchUsingMultipleFilters() {
        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("li");
        filter.setEmail("ly");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst())
            .extracting(
                CustomerSummaryOutput::getFirstName,
                CustomerSummaryOutput::getEmail)
            .containsExactly(
                "Lily",
                "lily.doe@example.com");
    }

    @Test
    void shouldReturnPaginated() {
        CustomerFilter firstPageFilter = new CustomerFilter();
        firstPageFilter.setPage(0);
        firstPageFilter.setSize(3);

        Page<CustomerSummaryOutput> firstPage = queryService.filter(firstPageFilter);

        Assertions.assertThat(firstPage.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(firstPage.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(firstPage.getNumberOfElements()).isEqualTo(3);

        CustomerFilter secondPageFilter = new CustomerFilter();
        secondPageFilter.setPage(1);
        secondPageFilter.setSize(3);

        Page<CustomerSummaryOutput> secondPage  = queryService.filter(secondPageFilter);

        Assertions.assertThat(secondPage.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(secondPage.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(secondPage.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    void shouldReturnSortedByFirstName() {
        CustomerFilter ascSortFilter = new CustomerFilter();
        ascSortFilter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        ascSortFilter.setSortDirection(Sort.Direction.ASC);

        Page<CustomerSummaryOutput> ascSortPage = queryService.filter(ascSortFilter);

        Assertions.assertThat(ascSortPage.getContent())
            .extracting(CustomerSummaryOutput::getFirstName)
            .containsExactlyInAnyOrder("Alice", "John", "Lily", "Mark");

        CustomerFilter descSortFilter = new CustomerFilter();
        descSortFilter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        descSortFilter.setSortDirection(Sort.Direction.DESC);

        Page<CustomerSummaryOutput> descSortPage = queryService.filter(descSortFilter);

        Assertions.assertThat(descSortPage.getContent())
            .extracting(CustomerSummaryOutput::getFirstName)
            .containsExactlyInAnyOrder("Mark", "Lily", "John", "Alice");
    }

    @Test
    void shouldReturnSortedByRegisteredAt() {
        CustomerFilter ascSortFilter = new CustomerFilter();
        ascSortFilter.setSortByProperty(CustomerFilter.SortType.REGISTERED_AT);
        ascSortFilter.setSortDirection(Sort.Direction.ASC);

        Page<CustomerSummaryOutput> ascSortPage = queryService.filter(ascSortFilter);

        Assertions.assertThat(ascSortPage.getContent())
            .extracting(CustomerSummaryOutput::getFirstName)
            .containsExactlyInAnyOrder("Mark", "Lily", "John", "Alice");

        CustomerFilter descSortFilter = new CustomerFilter();
        descSortFilter.setSortByProperty(CustomerFilter.SortType.REGISTERED_AT);
        descSortFilter.setSortDirection(Sort.Direction.DESC);

        Page<CustomerSummaryOutput> descSortPage = queryService.filter(descSortFilter);

        Assertions.assertThat(descSortPage.getContent())
            .extracting(CustomerSummaryOutput::getFirstName)
            .containsExactlyInAnyOrder("Alice", "John", "Lily", "Mark");
    }

    @Test
    void shouldNotReturnAnyRecords() {
        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("Adam");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(0);
        Assertions.assertThat(page.getContent()).isEmpty();
    }

}