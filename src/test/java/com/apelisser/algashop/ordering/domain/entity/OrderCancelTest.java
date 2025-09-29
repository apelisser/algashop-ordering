package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class OrderCancelTest {

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELED"})
    void givenAnUncancelledOrder_whenCancelIsPerformed_shouldCancel(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder()
            .status(status)
            .build();

        Assertions.assertWith(order,
            o -> Assertions.assertThat(o.isCanceled()).isFalse(),
            o -> Assertions.assertThat(o.canceledAt()).isNull(),
            o -> Assertions.assertThatCode(o::cancel).doesNotThrowAnyException(),
            o -> Assertions.assertThat(o.isCanceled()).isTrue(),
            o -> Assertions.assertThat(o.canceledAt())
                .isCloseTo(OffsetDateTime.now(), Assertions.within(3, ChronoUnit.SECONDS))
        );
    }


    @Test
    void givenACanceledOrder_whenCancelIsPerformed_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder()
            .status(OrderStatus.CANCELED)
            .build();

        Assertions.assertWith(order,
            o -> Assertions.assertThat(o.isCanceled()).isTrue(),
            o -> Assertions.assertThat(o.canceledAt()).isNotNull(),
            o -> Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(o::cancel)
        );
    }

}
