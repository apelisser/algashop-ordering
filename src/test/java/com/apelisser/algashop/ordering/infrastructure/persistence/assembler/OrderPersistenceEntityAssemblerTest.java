package com.apelisser.algashop.ordering.infrastructure.persistence.assembler;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.entity.OrderItem;
import com.apelisser.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @InjectMocks
    private OrderPersistenceEntityAssembler assembler;

    @BeforeEach
    void setUp() {
        Mockito.when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
            .then(a -> {
                UUID customerId = a.getArgument(0, UUID.class);
                return CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(customerId).build();
            });
    }

    @Test
    void shouldConvertToDomain() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderPersistenceEntity orderPersistenceEntity = assembler.fromDomain(order);

        Assertions.assertThat(orderPersistenceEntity).satisfies(
            p -> Assertions.assertThat(p.getId()).isEqualTo(order.id().value().toLong()),
            p -> Assertions.assertThat(p.getCustomerId()).isEqualTo(order.customerId().value()),
            p -> Assertions.assertThat(p.getTotalAmount()).isEqualTo(order.totalAmount().value()),
            p -> Assertions.assertThat(p.getTotalItems()).isEqualTo(order.totalItems().value()),
            p -> Assertions.assertThat(p.getStatus()).isEqualTo(order.status().name()),
            p -> Assertions.assertThat(p.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
            p -> Assertions.assertThat(p.getPlacedAt()).isEqualTo(order.placedAt()),
            p -> Assertions.assertThat(p.getPaidAt()).isEqualTo(order.paidAt()),
            p -> Assertions.assertThat(p.getCanceledAt()).isEqualTo(order.canceledAt()),
            p -> Assertions.assertThat(p.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

    @Test
    void givenOrderWithNoItems_shouldRemovePersistenceEntityItems() {
        Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
        OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        Assertions.assertThat(order.items()).isEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();

        assembler.merge(orderPersistenceEntity, order);

        Assertions.assertThat(orderPersistenceEntity.getItems()).isEmpty();
    }

    @Test
    void givenOrderWithItems_shouldAddToPersistenceEntity() {
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
        OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
            .items(new HashSet<>())
            .build();

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).isEmpty();

        assembler.merge(orderPersistenceEntity, order);
        Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).hasSize(order.items().size());
    }

    @Test
    void givenOrderWithItems_whenMerge_shouldMergeCorrectly() {
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();

        Assertions.assertThat(order.items()).hasSize(2);

        Set<OrderItemPersistenceEntity> orderItemPersistenceEntities = order.items().stream()
            .map(assembler::fromDomain)
            .collect(Collectors.toSet());

        OrderPersistenceEntity orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder()
            .items(orderItemPersistenceEntities)
            .build();

        OrderItem orderItem = order.items().iterator().next();
        order.removeItem(orderItem.id());

        assembler.merge(orderPersistenceEntity, order);
        Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).hasSize(order.items().size());
    }

}