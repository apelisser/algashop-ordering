package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class OrderMarkAsReadyTest {

    @Test
    void givenPaidOrder_whenMarkingAsReady_shouldAllowChange() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(OrderStatus.PAID)
            .build();

        Assertions.assertThatCode(order::markAsReady).doesNotThrowAnyException();
        Assertions.assertThat(order.readyAt())
            .isCloseTo(OffsetDateTime.now(), Assertions.within(3, ChronoUnit.SECONDS));
    }

    @Test
    void givenAnOrderThatIsNotADraft_whenMarkingAsReady_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(OrderStatus.DRAFT)
            .build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
            .isThrownBy(order::markAsReady);
        Assertions.assertWith(order,
            o -> Assertions.assertThat(o.status()).isNotEqualTo(OrderStatus.READY),
            o -> Assertions.assertThat(o.readyAt()).isNull()
        );
    }

}
