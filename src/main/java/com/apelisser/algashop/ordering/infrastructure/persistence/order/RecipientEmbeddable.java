package com.apelisser.algashop.ordering.infrastructure.persistence.order;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Embeddable
public class RecipientEmbeddable {

    private String firstName;
    private String lastName;
    private String document;
    private String phone;

    public RecipientEmbeddable() {
    }

    public RecipientEmbeddable(String firstName, String lastName, String document, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.phone = phone;
    }

}
