package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.repository.Orders;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.apelisser.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;

@Component
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository persistenceRepository;
    private final OrderPersistenceEntityAssembler assembler;
    private final OrderPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    public OrdersPersistenceProvider(OrderPersistenceEntityRepository persistenceRepository,
            OrderPersistenceEntityAssembler assembler, OrderPersistenceEntityDisassembler disassembler,
            EntityManager entityManager) {
        this.persistenceRepository = persistenceRepository;
        this.assembler = assembler;
        this.disassembler = disassembler;
        this.entityManager = entityManager;
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
        entityManager.detach(persistenceEntity);
        persistenceEntity = persistenceRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void insert(Order aggregateRoot) {
        OrderPersistenceEntity persistenceEntity = assembler.fromDomain(aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void updateVersion(Order aggregateRoot, OrderPersistenceEntity persistenceEntity) {
        try {
            Field version = aggregateRoot.getClass().getDeclaredField("version");
            version.setAccessible(true);
            ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());
            version.setAccessible(false);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        return 0;
    }

}
