package com.apelisser.algashop.ordering.domain.model.valueobject;

import lombok.Builder;

@Builder
public record Billing(
    FullName fullName,
    Document document,
    Phone phone,
    Email email,
    Address address
) {

    public Billing {
        if (fullName == null) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (document == null) {
            throw new IllegalArgumentException("Document is required");
        }

        if (phone == null) {
            throw new IllegalArgumentException("Phone is required");
        }

        if (email == null) {
            throw new IllegalArgumentException("E-mail is required");
        }

        if (address == null) {
            throw new IllegalArgumentException("Address is required");
        }
    }

}
