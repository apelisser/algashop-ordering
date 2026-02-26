package com.apelisser.algashop.ordering.application.customer.management;

import com.apelisser.algashop.ordering.application.commons.AddressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CustomerInput {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String document;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotNull
    private Boolean promotionNotificationsAllowed;

    @NotNull
    @Valid
    private AddressData address;

    public CustomerInput() {
    }

    public CustomerInput(String firstName, String lastName, String email, String phone, String document,
            LocalDate birthDate, Boolean promotionNotificationsAllowed, AddressData address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.birthDate = birthDate;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.address = address;
    }

}
