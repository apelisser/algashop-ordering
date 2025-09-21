package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.apelisser.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.apelisser.algashop.ordering.domain.valueobject.*;
import com.apelisser.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
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
    void givenDraftOrder_whenChangeChangeBillingInfo_shouldAllowChange() {
        Address address = Address.builder()
            .street("Bourbon Street")
            .number("12345")
            .neighborhood("North Ville")
            .city("Montfort")
            .state("South Carolina")
            .zipCode(new ZipCode("12345"))
            .build();

        BillingInfo billing = BillingInfo.builder()
            .address(address)
            .document(new Document("225-09-1992"))
            .phone(new Phone("123-111-9911"))
            .fullName(new FullName("John", "Doe"))
            .build();

        BillingInfo expectedBilling = BillingInfo.builder()
            .address(address)
            .document(new Document("225-09-1992"))
            .phone(new Phone("123-111-9911"))
            .fullName(new FullName("John", "Doe"))
            .build();

        Order order = Order.draft(new CustomerId());
        order.changeBilling(billing);

        Assertions.assertThat(order.billing()).isEqualTo(expectedBilling);
    }

    @Test
    void givenDraftOrder_whenChangeChangeShippingInfo_shouldAllowChange() {
        Address address = Address.builder()
            .street("Bourbon Street")
            .number("12345")
            .neighborhood("North Ville")
            .city("Montfort")
            .state("South Carolina")
            .zipCode(new ZipCode("12345"))
            .build();

        ShippingInfo shipping = ShippingInfo.builder()
            .address(address)
            .document(new Document("225-09-1992"))
            .phone(new Phone("123-111-9911"))
            .fullName(new FullName("John", "Doe"))
            .build();

        ShippingInfo expectedShipping = ShippingInfo.builder()
            .address(address)
            .document(new Document("225-09-1992"))
            .phone(new Phone("123-111-9911"))
            .fullName(new FullName("John", "Doe"))
            .build();

        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(1);

        order.changeShipping(shipping, shippingCost, expectedDeliveryDate);

        Assertions.assertWith(order,
            o -> Assertions.assertThat(o.shipping()).isEqualTo(expectedShipping),
            o -> Assertions.assertThat(o.shippingCost()).isEqualTo(shippingCost),
            o -> Assertions.assertThat(o.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
        );
    }

    @Test
    void givenDraftOrderAndDeliveryDateInThePast_whenChangeChangeShippingInfo_shouldNotAllowChange() {
        Address address = Address.builder()
            .street("Bourbon Street")
            .number("12345")
            .neighborhood("North Ville")
            .city("Montfort")
            .state("South Carolina")
            .zipCode(new ZipCode("12345"))
            .build();

        ShippingInfo shipping = ShippingInfo.builder()
            .address(address)
            .document(new Document("225-09-1992"))
            .phone(new Phone("123-111-9911"))
            .fullName(new FullName("John", "Doe"))
            .build();

        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
            .isThrownBy(() ->order.changeShipping(shipping, shippingCost, expectedDeliveryDate));
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

}