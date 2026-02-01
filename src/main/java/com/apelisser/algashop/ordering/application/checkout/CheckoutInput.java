package com.apelisser.algashop.ordering.application.checkout;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CheckoutInput {

    private UUID shoppingCartId;
    private String paymentMethod;
    private ShippingInput shipping;
    private BillingData billing;

    public CheckoutInput() {
    }

    public CheckoutInput(UUID shoppingCartId, String paymentMethod, ShippingInput shipping, BillingData billing) {
        this.shoppingCartId = shoppingCartId;
        this.paymentMethod = paymentMethod;
        this.shipping = shipping;
        this.billing = billing;
    }

}
