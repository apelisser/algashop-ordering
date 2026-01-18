package com.apelisser.algashop.ordering.application.checkout;

import com.apelisser.algashop.ordering.application.commons.AddressData;
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

    private RecipientData recipient;
    private AddressData address;

    public ShippingInput() {
    }

    public ShippingInput(RecipientData recipient, AddressData address) {
        this.recipient = recipient;
        this.address = address;
    }

}
