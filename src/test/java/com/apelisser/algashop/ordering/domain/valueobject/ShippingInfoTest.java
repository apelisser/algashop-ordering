package com.apelisser.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShippingInfoTest {

    @Test
    void shouldGenerate() {
        Runnable runnable = () -> ShippingInfo.builder()
            .fullName(new FullName("John", "Doe"))
            .document(new Document("12345678901"))
            .phone(new Phone("478-256-2504"))
            .address(this.buildAddress())
            .build();

        Assertions.assertThatCode(runnable::run).doesNotThrowAnyException();
    }

    @Test
    void shouldNotGenerate() {
        Runnable runnableMissingFullName = () -> ShippingInfo.builder()
            .fullName(null)
            .document(new Document("12345678901"))
            .phone(new Phone("478-256-2504"))
            .address(this.buildAddress())
            .build();

        Runnable runnableMissingDocument = () -> ShippingInfo.builder()
            .fullName(new FullName("John", "Doe"))
            .document(null)
            .phone(new Phone("478-256-2504"))
            .address(this.buildAddress())
            .build();

        Runnable runnableMissingPhone = () -> ShippingInfo.builder()
            .fullName(new FullName("John", "Doe"))
            .document(new Document("12345678901"))
            .phone(null)
            .address(this.buildAddress())
            .build();

        Runnable runnableMissingAddress = () -> ShippingInfo.builder()
            .fullName(new FullName("John", "Doe"))
            .document(new Document("12345678901"))
            .phone(new Phone("478-256-2504"))
            .address(null)
            .build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingFullName::run);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingDocument::run);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingPhone::run);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingAddress::run);
    }


    private Address buildAddress() {
        return Address.builder()
            .street("Bourbon Street")
            .number("1234")
            .neighborhood("North Ville")
            .city("York")
            .state("South California")
            .zipCode(new ZipCode("12345"))
            .complement("Apt. 114")
            .build();
    }

}