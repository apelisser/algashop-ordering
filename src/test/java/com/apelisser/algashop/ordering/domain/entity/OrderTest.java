package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.apelisser.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.apelisser.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.apelisser.algashop.ordering.domain.valueobject.*;
import com.apelisser.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

class OrderTest {

    @Test
    void shouldGenerate() {
        Assertions.assertThatCode(() -> Order.draft(new CustomerId()))
            .doesNotThrowAnyException();
    }

    @Test
    void shouldAddItem() {
        Order order = Order.draft(new CustomerId());
        Product product = ProductTestDataBuilder.aProductAltMousePad().build();

        order.addItem(product, new Quantity(1));

        Assertions.assertThat(order.items()).hasSize(1);

        OrderItem item = order.items().iterator().next();
        Assertions.assertWith(item,
            i -> Assertions.assertThat(i.productId()).isNotNull(),
            i -> Assertions.assertThat(i.productName()).isEqualTo(new ProductName("Mouse Pad")),
            i -> Assertions.assertThat(i.productId()).isEqualTo(product.id()),
            i -> Assertions.assertThat(i.price()).isEqualTo(new Money("100")),
            i -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(1))
        );
    }

    @Test
    void shouldGenerateExceptionWhenTryToChangeItemSet() {
        Order order = Order.draft(new CustomerId());

        Product product = ProductTestDataBuilder.aProductAltMousePad().build();
        order.addItem(product, new Quantity(1));

        Set<OrderItem> items = order.items();
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(items::clear);
    }

    @Test
    void shouldCalculateTotals() {
        Order order = Order.draft(new CustomerId());

        order.addItem(
            ProductTestDataBuilder.aProductAltMousePad().build(),
            new Quantity(1)
        );

        order.addItem(
            ProductTestDataBuilder.aProductAltRamMemory().build(),
            new Quantity(1)
        );

        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("700.00"));
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(2));
    }

    @Test
    void givenDraftOrder_whenPlace_shouldChangeStatusToPlaced() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(OrderStatus.DRAFT)
            .build();

        order.place();

        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrder_whenMarkAsPaid_shouldChangeStatusToPaid() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(OrderStatus.PLACED)
            .build();

        order.markAsPaid();

        Assertions.assertThat(order.isPaid()).isTrue();
        Assertions.assertThat(order.paidAt()).isNotNull();
    }

    @Test
    void givenPlacedOrder_whenTryToPlace_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(OrderStatus.PLACED)
            .build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
            .isThrownBy(order::place);
    }

    @Test
    void givenDraftOrder_whenChangePaymentMethod_shouldAllowChange() {
        Order order = Order.draft(new CustomerId());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftOrder_whenChangeChangeBilling_shouldAllowChange() {
        Billing billing = OrderTestDataBuilder.aBilling();

        Order order = Order.draft(new CustomerId());
        order.changeBilling(billing);

        Assertions.assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrder_whenChangeShipping_shouldAllowChange() {
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Order order = Order.draft(new CustomerId());

        order.changeShipping(shipping);

        Assertions.assertThat(order.shipping()).isEqualTo(OrderTestDataBuilder.aShipping());
    }

    @Test
    void givenDraftOrderAndDeliveryDateInThePast_whenChangeChangeShipping_shouldNotAllowChange() {
        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);
        Shipping shipping = OrderTestDataBuilder.aShipping().toBuilder()
            .expectedDate(expectedDeliveryDate)
            .build();

        Order order = Order.draft(new CustomerId());

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
            .isThrownBy(() -> order.changeShipping(shipping));
    }

    @Test
    void givenDraftOrder_whenChangeItem_shouldRecalculateTotals() {
        Order order = Order.draft(new CustomerId());
        order.addItem(
            ProductTestDataBuilder.aProductAltMousePad().build(),
            new Quantity(3)
        );

        OrderItem item = order.items().iterator().next();
        order.changeItemQuantity(item.id(), new Quantity(5));

        Assertions.assertWith(order,
            o -> Assertions.assertThat(o.totalAmount()).isEqualTo(new Money("500")),
            o -> Assertions.assertThat(o.totalItems()).isEqualTo(new Quantity(5))
        );
    }

    @Test
    void givenOutOfStockProduct_whenTryToAddToAnOrder_shouldNotAllow() {
        Order order = Order.draft(new CustomerId());

        ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(
            ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(1)
        );

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
            .isThrownBy(addItemTask);
    }

}