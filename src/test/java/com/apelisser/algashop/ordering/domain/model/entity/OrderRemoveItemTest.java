package com.apelisser.algashop.ordering.domain.model.entity;

import com.apelisser.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.apelisser.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.Shipping;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static com.apelisser.algashop.ordering.domain.model.entity.OrderStatus.DRAFT;
import static com.apelisser.algashop.ordering.domain.model.entity.OrderStatus.PLACED;

public class OrderRemoveItemTest {

    @Test
    void givenDraftOrderWithItems_whenRemoveItemIsPerformed_shouldRemoveItem() {
        Shipping freeShipping = OrderTestDataBuilder.aShipping().toBuilder()
            .cost(Money.ZERO)
            .build();

        Order order = OrderTestDataBuilder.anOrder()
            .withItems(false)
            .status(DRAFT)
            .shipping(freeShipping)
            .build();

        ThrowableAssert.ThrowingCallable addItemTask = () ->
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
        ThrowableAssert.ThrowingCallable removeItemTask = () -> {
            OrderItem orderItem = order.items().iterator().next();
            order.removeItem(orderItem.id());
        };

        Assertions.assertThatCode(addItemTask).doesNotThrowAnyException();
        Assertions.assertThatCode(removeItemTask).doesNotThrowAnyException();
        Assertions.assertWith(order,
            o -> Assertions.assertThat(o.items()).hasSize(0),
            o -> Assertions.assertThat(o.totalItems()).isEqualTo(Quantity.ZERO),
            o -> Assertions.assertThat(o.totalAmount()).isEqualTo(Money.ZERO)
        );
    }

    @Test
    void givenDraftOrder_whenTryingToRemoveNonExistentItem_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(DRAFT)
            .build();

        Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
            .isThrownBy(() -> order.removeItem(new OrderItemId()));
    }

    @Test
    void givenDraftOrder_whenRemovingAnItemWithNullId_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(DRAFT)
            .build();

        Assertions.assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> order.removeItem(null));
    }

    @Test
    void givenPlacedOrder_whenRemoveItemIsPerformed_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(PLACED)
            .build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
            .isThrownBy(() -> order.removeItem(new OrderItemId()));
    }

}
