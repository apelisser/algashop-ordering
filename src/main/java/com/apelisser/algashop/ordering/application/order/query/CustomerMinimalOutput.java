package com.apelisser.algashop.ordering.application.order.query;

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
public class CustomerMinimalOutput {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String document;
    private String phone;

    public CustomerMinimalOutput() {
    }

    public CustomerMinimalOutput(UUID id, String firstName, String lastName, String email, String document, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.document = document;
        this.phone = phone;
    }

}
