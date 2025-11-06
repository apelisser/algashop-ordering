package com.apelisser.algashop.ordering.infrastructure.persistence.embeddable;

public class AddressEmbeddableTestDataBuilder {

    private AddressEmbeddableTestDataBuilder() {
    }

    public static AddressEmbeddable.AddressEmbeddableBuilder existingAddress() {
        return AddressEmbeddable.builder()
            .street("Bourbon Street")
            .complement("Apt. 114")
            .neighborhood("North Ville")
            .number("1234")
            .city("York")
            .state("South California")
            .zipCode("12345");
    }

}
