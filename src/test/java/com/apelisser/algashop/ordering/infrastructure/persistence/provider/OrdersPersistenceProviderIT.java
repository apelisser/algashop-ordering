package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.entity.OrderStatus;
import com.apelisser.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({
    OrdersPersistenceProvider.class,
    OrderPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
class OrdersPersistenceProviderIT {

    private static final Logger log = LoggerFactory.getLogger(OrdersPersistenceProviderIT.class);
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

}