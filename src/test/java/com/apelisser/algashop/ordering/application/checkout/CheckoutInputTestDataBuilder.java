package com.apelisser.algashop.ordering.application.checkout;

import com.apelisser.algashop.ordering.application.commons.AddressData;
import com.apelisser.algashop.ordering.application.order.query.BillingData;
import com.apelisser.algashop.ordering.application.order.query.RecipientData;
import com.apelisser.algashop.ordering.domain.model.order.PaymentMethod;

import java.util.UUID;

public class CheckoutInputTestDataBuilder {

    private static final String PAYMENT_METHOD = PaymentMethod.CREDIT_CARD.toString();

    public static CheckoutInput.CheckoutInputBuilder aCheckoutInput() {
        return CheckoutInput.builder()
            .shoppingCartId(UUID.randomUUID())
            .paymentMethod(PAYMENT_METHOD)
            .shipping(aShippingInput())
            .billing(aBillingData());
    }

    private static ShippingInput aShippingInput() {
        return ShippingInput.builder()
            .recipient(RecipientData.builder()
                .firstName("John")
                .lastName("Doe")
                .document("255-08-0578")
                .phone("478-256-2604")
                .build())
            .address(AddressData.builder()
                .street("Elm Street")
                .number("456")
                .complement("House A")
                .neighborhood("Central Park")
                .city("Springfield")
                .state("Illinois")
                .zipCode("62704")
                .build())
            .build();
    }

    private static BillingData aBillingData() {
        return BillingData.builder()
            .firstName("Matt")
            .lastName("Damon")
            .phone("123-321-1112")
            .document("123-45-6789")
            .email("matt.damon@email.com")
            .address(AddressData.builder()
                .street("Amphitheatre Parkway")
                .number("1600")
                .complement("")
                .neighborhood("Mountain View")
                .city("Mountain View")
                .state("California")
                .zipCode("94043")
                .build())
            .build();
    }

}
