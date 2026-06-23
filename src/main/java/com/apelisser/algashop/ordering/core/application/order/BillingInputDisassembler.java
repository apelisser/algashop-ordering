package com.apelisser.algashop.ordering.core.application.order;

import com.apelisser.algashop.ordering.core.ports.in.commons.AddressData;
import com.apelisser.algashop.ordering.core.ports.in.order.BillingData;
import com.apelisser.algashop.ordering.core.domain.model.commons.Address;
import com.apelisser.algashop.ordering.core.domain.model.commons.Document;
import com.apelisser.algashop.ordering.core.domain.model.commons.Email;
import com.apelisser.algashop.ordering.core.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.core.domain.model.commons.Phone;
import com.apelisser.algashop.ordering.core.domain.model.commons.ZipCode;
import com.apelisser.algashop.ordering.core.domain.model.order.Billing;
import org.springframework.stereotype.Component;

@Component
public class BillingInputDisassembler {

    public Billing toDomainModel(BillingData billingData) {
        AddressData address = billingData.getAddress();
        return Billing.builder()
            .fullName(new FullName(billingData.getFirstName(), billingData.getLastName()))
            .document(new Document(billingData.getDocument()))
            .phone(new Phone(billingData.getPhone()))
            .email(new Email(billingData.getEmail()))
            .address(Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(new ZipCode(address.getZipCode()))
                .build())
            .build();
    }

}
