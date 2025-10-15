package com.apelisser.algashop.ordering.infrastructure.persistence.embeddable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@Embeddable
public class ShippingEmbeddable {

    private BigDecimal cost;
    private LocalDate expectedDate;

    @Embedded
    private RecipientEmbeddable recipient;

    @Embedded
    private AddressEmbeddable address;

    public ShippingEmbeddable() {
    }

    public ShippingEmbeddable(BigDecimal cost, LocalDate expectedDate, RecipientEmbeddable recipient,
            AddressEmbeddable address) {
        this.cost = cost;
        this.expectedDate = expectedDate;
        this.recipient = recipient;
        this.address = address;
    }

}
