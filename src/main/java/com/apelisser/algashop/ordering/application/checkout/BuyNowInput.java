package com.apelisser.algashop.ordering.application.checkout;

import com.apelisser.algashop.ordering.application.order.query.BillingData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull
    @Valid
    private ShippingInput shipping;

    @NotNull
    @Valid
    private BillingData billing;

    @NotNull
    private UUID productId;

    @NotNull
    private UUID customerId;

    @NotNull
    @Positive
    private Integer quantity;

    @NotBlank
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
