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

    public OrderFilter() {
    }

    public OrderFilter(int page, int size) {
        super(page, size);
    }

    public OrderFilter(String status, String orderId, UUID customerId, OffsetDateTime placedAtFrom,
            OffsetDateTime placedAtTo, BigDecimal totalAmountFrom, BigDecimal totalAmountTo) {
        this.status = status;
        this.orderId = orderId;
        this.customerId = customerId;
        this.placedAtFrom = placedAtFrom;
        this.placedAtTo = placedAtTo;
        this.totalAmountFrom = totalAmountFrom;
        this.totalAmountTo = totalAmountTo;
    }

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
