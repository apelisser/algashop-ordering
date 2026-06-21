package com.apelisser.algashop.ordering.core.application.checkout;

import com.apelisser.algashop.ordering.core.application.order.query.BillingData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private UUID shoppingCartId;

    @NotBlank
    private String paymentMethod;

    @NotNull
    @Valid
    private ShippingInput shipping;

    @NotNull
    @Valid
    private BillingData billing;

    private UUID creditCardId;

    public CheckoutInput() {
    }

    public CheckoutInput(UUID shoppingCartId, String paymentMethod, ShippingInput shipping, BillingData billing, UUID creditCardId) {
        this.shoppingCartId = shoppingCartId;
        this.paymentMethod = paymentMethod;
        this.shipping = shipping;
        this.billing = billing;
        this.creditCardId = creditCardId;
    }

}
