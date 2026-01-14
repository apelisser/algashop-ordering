package com.apelisser.algashop.ordering.infrastructure.fake;

import com.apelisser.algashop.ordering.domain.model.service.ShippingCostService;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ShippingCostServiceFakeImpl implements ShippingCostService {

    @Override
    public CalculationResult calculate(CalculationRequest request) {
        return new CalculationResult(
            new Money("20"),
            LocalDate.now().plusDays(5));
    }

}
