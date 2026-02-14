package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.application.commons.AddressData;
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
public class BillingData {

    private String firstName;
    private String lastName;
    private String document;
    private String email;
    private String phone;
    private AddressData address;

    public BillingData() {
    }

    public BillingData(String firstName, String lastName, String document, String email, String phone, AddressData address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

}
