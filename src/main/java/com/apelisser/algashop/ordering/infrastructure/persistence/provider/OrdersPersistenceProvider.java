package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.repository.Orders;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository persistenceRepository;

    public OrdersPersistenceProvider(OrderPersistenceEntityRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        return Optional.empty();
    }

    @Override
    public boolean exists(OrderId orderId) {
        return false;
    }

    @Override
    public void add(Order aggregateRoot) {
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntity.builder()
            .id(aggregateRoot.id().value().toLong())
            .customerId(aggregateRoot.customerId().value())
            .build();

        persistenceRepository.saveAndFlush(persistenceEntity);
    }

    @Override
    public long count() {
        return 0;
    }

}
