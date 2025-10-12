package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.repository.Orders;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.apelisser.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository persistenceRepository;
    private final OrderPersistenceEntityAssembler assembler;
    private final OrderPersistenceEntityDisassembler disassembler;

    public OrdersPersistenceProvider(OrderPersistenceEntityRepository persistenceRepository,
            OrderPersistenceEntityAssembler assembler, OrderPersistenceEntityDisassembler disassembler) {
        this.persistenceRepository = persistenceRepository;
        this.assembler = assembler;
        this.disassembler = disassembler;
    }

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        Optional<OrderPersistenceEntity> possibleEntity = persistenceRepository.findById(orderId.value().toLong());
        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(OrderId orderId) {
        return false;
    }

    @Override
    public void add(Order aggregateRoot) {
        long orderId = aggregateRoot.id().value().toLong();

        persistenceRepository.findById(orderId).ifPresentOrElse(
            persistenceEntity -> update(aggregateRoot, persistenceEntity),
            () -> insert(aggregateRoot)
        );
    }

    private void update(Order aggregateRoot, OrderPersistenceEntity persistenceEntity) {
        persistenceEntity = assembler.merge(persistenceEntity, aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
    }

    private void insert(Order aggregateRoot) {
        OrderPersistenceEntity persistenceEntity = assembler.fromDomain(aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
    }

    @Override
    public long count() {
        return 0;
    }

}
