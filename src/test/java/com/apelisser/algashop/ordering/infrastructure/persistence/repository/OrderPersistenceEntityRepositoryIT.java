package com.apelisser.algashop.ordering.infrastructure.persistence.repository;

import com.apelisser.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class OrderPersistenceEntityRepositoryIT {

    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    @Autowired
    OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository orderPersistenceEntityRepository) {
        this.orderPersistenceEntityRepository = orderPersistenceEntityRepository;
    }

    @Test
    void shouldPersist() {
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        orderPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(orderPersistenceEntityRepository.existsById(entity.getId())).isTrue();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceEntityRepository.count();
        Assertions.assertThat(ordersCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        entity = orderPersistenceEntityRepository.saveAndFlush(entity);

        Assertions.assertThat(entity).satisfies(
            s -> Assertions.assertThat(s.getCreatedAt()).isNotNull(),
            s -> Assertions.assertThat(s.getCreatedByUserId()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedAt()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedByUserId()).isNotNull()
        );
    }

}