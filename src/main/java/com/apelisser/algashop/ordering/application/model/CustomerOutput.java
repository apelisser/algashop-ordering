package com.apelisser.algashop.ordering.application.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CustomerOutput {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String document;
    private String phone;
    private LocalDate birthDate;
    private Boolean promotionNotificationsAllowed;
    private Integer loyaltyPoints;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private AddressData address;

    public CustomerOutput() {
    }

    public CustomerOutput(UUID id, String firstName, String lastName, String email, String document, String phone,
            LocalDate birthDate, Boolean promotionNotificationsAllowed, Integer loyaltyPoints,
            OffsetDateTime registeredAt, OffsetDateTime archivedAt, AddressData address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.document = document;
        this.phone = phone;
        this.birthDate = birthDate;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.loyaltyPoints = loyaltyPoints;
        this.registeredAt = registeredAt;
        this.archivedAt = archivedAt;
        this.address = address;
    }

}
