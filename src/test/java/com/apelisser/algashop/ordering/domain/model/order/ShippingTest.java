package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.commons.Address;
import com.apelisser.algashop.ordering.domain.model.commons.Document;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.commons.Phone;
import com.apelisser.algashop.ordering.domain.model.commons.ZipCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ShippingTest {

    @Test
    void shouldGenerate() {
        Assertions.assertThatCode(this::aShipping).doesNotThrowAnyException();
    }

    @Test
    void shouldNotGenerate() {
        Runnable runnableMissingCost = () -> aShipping().toBuilder()
            .cost(null)
            .build();

        Runnable runnableMissingExpectedDate = () -> aShipping().toBuilder()
            .expectedDate(null)
            .build();

        Runnable runnableMissingRecipient = () -> aShipping().toBuilder()
            .recipient(null)
            .build();

        Runnable runnableMissingAddress = () -> aShipping().toBuilder()
            .address(null)
            .build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingCost::run);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingExpectedDate::run);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingRecipient::run);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(runnableMissingAddress::run);
    }

    private Shipping aShipping() {
        return Shipping.builder()
            .cost(new Money("10"))
            .expectedDate(LocalDate.now().plusWeeks(1))
            .address(anAddress())
            .recipient(Recipient.builder()
                .fullName(new FullName("John", "Doe"))
                .document(new Document("112-33-2321"))
                .phone(new Phone("111-441-1244"))
                .build())
            .build();
    }

    private Address anAddress() {
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