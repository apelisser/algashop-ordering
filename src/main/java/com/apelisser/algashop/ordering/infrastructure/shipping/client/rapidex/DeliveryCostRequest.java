package com.apelisser.algashop.ordering.infrastructure.shipping.client.rapidex;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DeliveryCostRequest {

    private String originZipCode;
    private String destinationZipCode;

    public DeliveryCostRequest() {
    }

    public DeliveryCostRequest(String originZipCode, String destinationZipCode) {
        this.originZipCode = originZipCode;
        this.destinationZipCode = destinationZipCode;
    }

}
