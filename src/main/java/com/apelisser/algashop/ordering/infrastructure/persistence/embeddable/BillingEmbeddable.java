package com.apelisser.algashop.ordering.infrastructure.persistence.embeddable;

import com.apelisser.algashop.ordering.domain.model.valueobject.Address;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class BillingEmbeddable {

    private String firstName;
    private String lastName;
    private String document;
    private String phone;
    private String email;
    @Embedded
    private Address address;

    public BillingEmbeddable() {
    }

    public BillingEmbeddable(String firstName, String lastName, String document, String phone, String email,
            Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

}
