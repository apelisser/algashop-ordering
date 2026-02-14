package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.application.checkout.RecipientData;
import com.apelisser.algashop.ordering.application.commons.AddressData;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ShippingData {

    private BigDecimal cost;
    private LocalDate expectedDate;
    private RecipientData recipient;
    private AddressData address;

    public ShippingData() {
    }

    public ShippingData(BigDecimal cost, LocalDate expectedDate, RecipientData recipient, AddressData address) {
        this.cost = cost;
        this.expectedDate = expectedDate;
        this.recipient = recipient;
        this.address = address;
    }

}
