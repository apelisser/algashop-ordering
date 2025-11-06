package com.apelisser.algashop.ordering.infrastructure.persistence.repository;

import com.apelisser.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class CustomerPersistenceEntityRepositoryIT {

    final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @Autowired
    CustomerPersistenceEntityRepositoryIT(CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
    }

    @Test
    void shouldPersist() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();

        customerPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(customerPersistenceEntityRepository.existsById(entity.getId())).isTrue();

        CustomerPersistenceEntity savedEntity = customerPersistenceEntityRepository.findById(entity.getId()).orElseThrow();
        Assertions.assertThat(savedEntity.getAddress()).isNotNull();
    }

    @Test
    void shouldCount() {
        long ordersCount = customerPersistenceEntityRepository.count();
        Assertions.assertThat(ordersCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        entity = customerPersistenceEntityRepository.saveAndFlush(entity);

        Assertions.assertThat(entity).satisfies(
            s -> Assertions.assertThat(s.getCreatedAt()).isNotNull(),
            s -> Assertions.assertThat(s.getCreatedByUserId()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedAt()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedByUserId()).isNotNull()
        );
    }

}