package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.domain.model.entity.Customer;
import com.apelisser.algashop.ordering.domain.model.repository.Customers;
import com.apelisser.algashop.ordering.domain.model.valueobject.Email;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class CustomersPersistenceProvider implements Customers {

    private final CustomerPersistenceEntityRepository persistenceRepository;
    private final CustomerPersistenceEntityDisassembler disassembler;
    private final CustomerPersistenceEntityAssembler assembler;
    private final EntityManager entityManager;

    public CustomersPersistenceProvider(CustomerPersistenceEntityRepository persistenceRepository, CustomerPersistenceEntityDisassembler disassembler, CustomerPersistenceEntityAssembler assembler, EntityManager entityManager) {
        this.persistenceRepository = persistenceRepository;
        this.disassembler = disassembler;
        this.assembler = assembler;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Customer> ofId(CustomerId customerId) {
        Optional<CustomerPersistenceEntity> possibleEntity = persistenceRepository.findById(customerId.value());
        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(CustomerId customerId) {
        return persistenceRepository.existsById(customerId.value());
    }

    @Override
    public void add(Customer aggregateRoot) {
        UUID customerId = aggregateRoot.id().value();

        persistenceRepository.findById(customerId).ifPresentOrElse(
            persistenceEntity -> update(aggregateRoot, persistenceEntity),
            () -> insert(aggregateRoot));
    }

    @Override
    public long count() {
        return persistenceRepository.count();
    }

    @Override
    public Optional<Customer> ofEmail(Email email) {
        return persistenceRepository.findByEmail(email.value())
            .map(disassembler::toDomainEntity);
    }

    @Override
    public boolean isEmailUnique(Email email, CustomerId exceptCustomerId) {
        return !persistenceRepository.existsByEmailAndIdNot(email.value(), exceptCustomerId.value());
    }

    private void update(Customer aggregateRoot, CustomerPersistenceEntity persistenceEntity) {
        persistenceEntity = assembler.merge(persistenceEntity, aggregateRoot);
        entityManager.detach(persistenceEntity);
        persistenceEntity = persistenceRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void insert(Customer aggregateRoot) {
        CustomerPersistenceEntity persistenceEntity = assembler.fromDomain(aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void updateVersion(Customer aggregateRoot, CustomerPersistenceEntity persistenceEntity) {
        try {
            Field version = aggregateRoot.getClass().getDeclaredField("version");
            version.setAccessible(true);
            ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());
            version.setAccessible(false);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
