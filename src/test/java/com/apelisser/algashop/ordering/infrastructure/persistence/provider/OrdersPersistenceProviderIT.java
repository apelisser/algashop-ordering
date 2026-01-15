package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderStatus;
import com.apelisser.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrdersPersistenceProvider;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({
    OrdersPersistenceProvider.class,
    OrderPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class,
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
class OrdersPersistenceProviderIT {

    private final OrdersPersistenceProvider persistenceProvider;
    private final CustomersPersistenceProvider customersPersistenceProvider;
    private final OrderPersistenceEntityRepository entityRepository;

    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider persistenceProvider,
            CustomersPersistenceProvider customersPersistenceProvider, OrderPersistenceEntityRepository entityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.customersPersistenceProvider = customersPersistenceProvider;
        this.entityRepository = entityRepository;
    }

    @BeforeEach
    void setUp() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldUpdateAndKeepPersistenceEntityState() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        long orderId = order.id().value().toLong();
        persistenceProvider.add(order);

        OrderPersistenceEntity persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        Assertions.assertThat(persistenceEntity).satisfies(
            p -> Assertions.assertThat(p.getStatus()).isEqualTo(OrderStatus.PLACED.name()),
            p -> Assertions.assertThat(p.getCreatedByUserId()).isNotNull(),
            p -> Assertions.assertThat(p.getCreatedAt()).isNotNull(),
            p -> Assertions.assertThat(p.getLastModifiedByUserId()).isNotNull(),
            p -> Assertions.assertThat(p.getLastModifiedAt()).isNotNull()
        );

        order = persistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        persistenceProvider.add(order);

        persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        Assertions.assertThat(persistenceEntity).satisfies(
            p -> Assertions.assertThat(p.getStatus()).isEqualTo(OrderStatus.PAID.name()),
            p -> Assertions.assertThat(p.getCreatedByUserId()).isNotNull(),
            p -> Assertions.assertThat(p.getCreatedAt()).isNotNull(),
            p -> Assertions.assertThat(p.getLastModifiedByUserId()).isNotNull(),
            p -> Assertions.assertThat(p.getLastModifiedAt()).isNotNull()
        );
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldAddFindAndNotFailWhenNoTransaction() {
        Order order = OrderTestDataBuilder.anOrder().build();
        persistenceProvider.add(order);

        Assertions.assertThatNoException().isThrownBy(
            () -> persistenceProvider.ofId(order.id()).orElseThrow()
        );
    }

}