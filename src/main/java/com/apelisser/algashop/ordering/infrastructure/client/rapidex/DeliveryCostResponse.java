package com.apelisser.algashop.ordering.infrastructure.client.rapidex;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DeliveryCostResponse {

    private String deliveryCost;
    private Long estimatedDaysToDeliver;

    public DeliveryCostResponse() {
    }

    public DeliveryCostResponse(String deliveryCost, Long estimatedDaysToDeliver) {
        this.deliveryCost = deliveryCost;
        this.estimatedDaysToDeliver = estimatedDaysToDeliver;
    }

}
