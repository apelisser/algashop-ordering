package com.apelisser.algashop.ordering.infrastructure.persistence.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Embeddable
public class AddressEmbeddable {

    private String street;
    private String complement;
    private String neighborhood;
    private String number;
    private String city;
    private String state;
    private String zipCode;

    public AddressEmbeddable() {
    }

    public AddressEmbeddable(String street, String complement, String neighborhood, String number,
            String city, String state, String zipCode) {
        this.street = street;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

}
