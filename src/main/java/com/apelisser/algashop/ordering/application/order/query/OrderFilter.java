package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.application.utility.SortablePageFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class OrderFilter extends SortablePageFilter<OrderFilter.SortType> {

    private String status;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime placedAtFrom;
    private OffsetDateTime placedAtTo;
    private BigDecimal totalAmountFrom;
    private BigDecimal totalAmountTo;

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.PLACED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    public enum SortType {
        PLACED_AT("placedAt"),
        PAID_AT("paidAt"),
        CANCELED_AT("canceledAt"),
        READY_AT("readyAt"),
        STATUS("status");

        private final String propertyName;

        SortType(String propertyName) {
            this.propertyName = propertyName;
        }
    }

}
