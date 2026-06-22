package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.core.domain.model.order.Order;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderStatus;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.AbstractInfrastructureAPI;
import com.apelisser.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityRepository;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.order.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Import({
    OrdersPersistenceProvider.class,
    OrderPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class,
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration,classpath:db/testdata")
class OrdersPersistenceProviderIT extends AbstractInfrastructureAPI {

    private final OrdersPersistenceProvider persistenceProvider;
    private final OrderPersistenceEntityRepository entityRepository;

    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider persistenceProvider,
            OrderPersistenceEntityRepository entityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.entityRepository = entityRepository;
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