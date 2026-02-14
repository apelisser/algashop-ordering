package com.apelisser.algashop.ordering.application.order.query;

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
public class RecipientData {

    private String firstName;
    private String lastName;
    private String document;
    private String phone;

    public RecipientData() {
    }

    public RecipientData(String firstName, String lastName, String document, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.phone = phone;
    }

}
