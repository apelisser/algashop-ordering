package com.apelisser.algashop.ordering.infrastructure.client.rapidex;

import com.apelisser.algashop.ordering.domain.model.service.ShippingCostService;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
public class ShippingCostServiceRapidexImpl implements ShippingCostService {

    private final RapiDexAPIClient rapiDexAPIClient;

    public ShippingCostServiceRapidexImpl(RapiDexAPIClient rapiDexAPIClient) {
        this.rapiDexAPIClient = rapiDexAPIClient;
    }

    @Override
    public CalculationResult calculate(CalculationRequest request) {
        DeliveryCostRequest adaptedRequest = this.adaptRequest(request);
        DeliveryCostResponse response = rapiDexAPIClient.calculate(adaptedRequest);
        return this.adaptResponse(response);
    }

    private DeliveryCostRequest adaptRequest(CalculationRequest request) {
        return new DeliveryCostRequest(
            request.origin().value(),
            request.destination().value());
    }

    private CalculationResult adaptResponse(DeliveryCostResponse response) {
        return new CalculationResult(
            new Money(response.getDeliveryCost()),
            LocalDate.now().plusDays(response.getEstimatedDaysToDeliver())
        );
    }

}
