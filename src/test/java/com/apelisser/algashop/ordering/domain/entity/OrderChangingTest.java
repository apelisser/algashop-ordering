package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Consumer;

import static com.apelisser.algashop.ordering.domain.entity.OrderStatus.DRAFT;
import static com.apelisser.algashop.ordering.domain.entity.PaymentMethod.CREDIT_CARD;

public class OrderChangingTest {

    Order order;

    @BeforeEach
    void setUp() {
        this.order = OrderTestDataBuilder.anOrder()
            .status(DRAFT)
            .build();
    }

    @ParameterizedTest
    @MethodSource("changeCases")
    void givenDraftOrder_whenChangeIsPerformed_shouldNotThrowException(Consumer<Order> test) {
        Assertions.assertThatCode(() -> test.accept(order)).doesNotThrowAnyExceptionExcept();
    }

    @ParameterizedTest
    @MethodSource("changeCases")
    void givenAnOrderThatIsNotADraft_whenChangeIsPerformed_shouldGenerateException(Consumer<Order> test) {
        order.place();
        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
            .isThrownBy(() -> test.accept(order));
    }

    private static List<Consumer<Order>> changeCases() {
        return List.of(
            order -> order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1)),
            order -> order.changeBilling(OrderTestDataBuilder.aBilling()),
            order -> order.changeShipping(OrderTestDataBuilder.aShipping()),
            order -> order.changePaymentMethod(CREDIT_CARD),
            order -> {
                order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));
                OrderItem orderItem = order.items().iterator().next();
                order.changeItemQuantity(orderItem.id(), new Quantity(3));
            }
        );
    }

}
