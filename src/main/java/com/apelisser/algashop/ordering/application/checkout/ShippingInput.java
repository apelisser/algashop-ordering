package com.apelisser.algashop.ordering.application.checkout;

import com.apelisser.algashop.ordering.application.commons.AddressData;
import com.apelisser.algashop.ordering.application.order.query.RecipientData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ShippingInput {

    @NotNull
    @Valid
    private RecipientData recipient;

    @NotNull
    @Valid
    private AddressData address;

    public ShippingInput() {
    }

    public ShippingInput(RecipientData recipient, AddressData address) {
        this.recipient = recipient;
        this.address = address;
    }

}
