package com.apelisser.algashop.ordering.application.customer.management;

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
public class CustomerUpdateInput {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String phone;

    @NotNull
    private Boolean promotionNotificationsAllowed;

    @NotNull
    @Valid
    private AddressData address;

    public CustomerUpdateInput() {
    }

    public CustomerUpdateInput(String firstName, String lastName, String phone, Boolean promotionNotificationsAllowed,
            AddressData address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.address = address;
    }

}
