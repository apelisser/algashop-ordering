package com.apelisser.algashop.ordering.infrastructure.persistence.entity;

import com.apelisser.algashop.ordering.domain.model.utility.IdGenerator;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@SpringBootTest
@Transactional
class OrderPersistenceEntityIT {

    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    @Autowired
    OrderPersistenceEntityIT(OrderPersistenceEntityRepository orderPersistenceEntityRepository) {
        this.orderPersistenceEntityRepository = orderPersistenceEntityRepository;
    }

    @Test
    void shouldPersist() {
        Long orderId = IdGenerator.generateTSID().toLong();
        OrderPersistenceEntity entity = OrderPersistenceEntity.builder()
            .id(orderId)
            .customerId(IdGenerator.generateTimeBasedUUID())
            .totalItems(2)
            .totalAmount(new BigDecimal(1000))
            .status("DRAFT")
            .paymentMethod("CREDIT_CARD")
            .placetAt(OffsetDateTime.now())
            .build();

        orderPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(orderPersistenceEntityRepository.existsById(orderId)).isTrue();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceEntityRepository.count();
        Assertions.assertThat(ordersCount).isZero();
    }

}