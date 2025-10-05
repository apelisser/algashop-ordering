package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Consumer;

import static com.apelisser.algashop.ordering.domain.entity.OrderStatus.DRAFT;
import static com.apelisser.algashop.ordering.domain.entity.PaymentMethod.CREDIT_CARD;

public class OrderChangingTest {

    @ParameterizedTest
    @MethodSource("changeCases")
    void givenDraftOrder_whenChangeIsPerformed_shouldNotThrowException(Consumer<Order> test) {
        Order order = OrderTestDataBuilder.anOrder()
            .status(DRAFT)
            .build();
        Assertions.assertThatCode(() -> test.accept(order)).doesNotThrowAnyExceptionExcept();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"DRAFT"})
    void givenAnOrderThatIsNotADraft_whenChangeIsPerformed_shouldGenerateException(OrderStatus orderStatus) {
        Order order = OrderTestDataBuilder.anOrder()
            .status(orderStatus)
            .build();

        OrderChangingTest.changeCases().forEach(test ->
            Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> test.accept(order))
        );
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
