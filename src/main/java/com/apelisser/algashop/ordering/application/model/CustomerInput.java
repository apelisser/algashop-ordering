package com.apelisser.algashop.ordering.application.model;

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

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String document;
    private LocalDate birthDate;
    private Boolean promotionNotificationsAllowed;
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
