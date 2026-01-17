package com.apelisser.algashop.ordering.application.customer.management;

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
public class CustomerUpdateInput {

    private String firstName;
    private String lastName;
    private String phone;
    private Boolean promotionNotificationsAllowed;
    private AddressData address;

    public CustomerUpdateInput() {
    }

    public CustomerUpdateInput(String firstName, String lastName, String phone, Boolean promotionNotificationsAllowed,
            AddressData address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.address = address;
    }

}
