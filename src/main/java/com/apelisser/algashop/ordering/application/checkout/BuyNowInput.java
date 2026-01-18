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
public class BuyNowInput {

    private ShippingInput shipping;
    private BillingData billing;
    private UUID productId;
    private UUID customerId;
    private Integer quantity;
    private String paymentMethod;

    public BuyNowInput() {
    }

    public BuyNowInput(ShippingInput shipping, BillingData billing, UUID productId, UUID customerId, Integer quantity, String paymentMethod) {
        this.shipping = shipping;
        this.billing = billing;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
    }

}
