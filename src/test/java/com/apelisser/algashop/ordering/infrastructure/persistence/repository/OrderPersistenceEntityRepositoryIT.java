package com.apelisser.algashop.ordering.infrastructure.persistence.repository;

import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class OrderPersistenceEntityRepositoryIT {

    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;
    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @Autowired
    OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository orderPersistenceEntityRepository,
            CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.orderPersistenceEntityRepository = orderPersistenceEntityRepository;
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
    }

    @BeforeEach
    void setUp() {
        UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
        if (!customerPersistenceEntityRepository.existsById(customerId)) {
            customerPersistenceEntity = customerPersistenceEntityRepository.saveAndFlush(
                CustomerPersistenceEntityTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldPersist() {
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
            .customer(customerPersistenceEntity)
            .build();

        orderPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(orderPersistenceEntityRepository.existsById(entity.getId())).isTrue();

        OrderPersistenceEntity savedEntity = orderPersistenceEntityRepository.findById(entity.getId()).orElseThrow();
        Assertions.assertThat(savedEntity.getItems()).isNotEmpty();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceEntityRepository.count();
        Assertions.assertThat(ordersCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
            .customer(customerPersistenceEntity)
            .build();
        entity = orderPersistenceEntityRepository.saveAndFlush(entity);

        Assertions.assertThat(entity).satisfies(
            s -> Assertions.assertThat(s.getCreatedAt()).isNotNull(),
            s -> Assertions.assertThat(s.getCreatedByUserId()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedAt()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedByUserId()).isNotNull()
        );
    }

}