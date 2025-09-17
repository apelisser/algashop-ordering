package com.apelisser.algashop.ordering.domain.valueobject;

import lombok.Builder;

@Builder
public record ShippingInfo(
    FullName fullName,
    Document document,
    Phone phone,
    Address address
) {

    public ShippingInfo {
        if (fullName == null) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (document == null) {
            throw new IllegalArgumentException("Document is required");
        }

        if (phone == null) {
            throw new IllegalArgumentException("Phone is required");
        }

        if (address == null) {
            throw new IllegalArgumentException("Address is required");
        }
    }

}
