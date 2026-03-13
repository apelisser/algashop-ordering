package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.application.commons.AddressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class BillingData {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String document;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    @Valid
    private AddressData address;

    public BillingData() {
    }

    public BillingData(String firstName, String lastName, String document, String email, String phone, AddressData address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

}
