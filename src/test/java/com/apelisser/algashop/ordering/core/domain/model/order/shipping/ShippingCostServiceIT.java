package com.apelisser.algashop.ordering.core.domain.model.order.shipping;

import com.apelisser.algashop.ordering.core.domain.model.AbstractDomainIT;
import com.apelisser.algashop.ordering.core.domain.model.commons.Money;
import com.apelisser.algashop.ordering.core.domain.model.commons.ZipCode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

class ShippingCostServiceIT extends AbstractDomainIT {

    @Autowired
    ShippingCostService shippingCostService;

    @Autowired
    OriginAddressService originAddressService;

    WireMockServer wireMockRapidex;

    @BeforeEach
    void setUp() {
        wireMockRapidex = new WireMockServer(WireMockConfiguration.options()
            .port(8780)
            .usingFilesUnderClasspath("src/test/resources/wiremock/rapidex")
            .extensions(new ResponseTemplateTransformer(true)));

        wireMockRapidex.start();
    }

    @AfterEach
    void tearDown() {
        wireMockRapidex.stop();
    }

    @Test
    void shouldCalculate() {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode("12345");

        var request = new ShippingCostService.CalculationRequest(origin, destination);
        var shippingCalculation = shippingCostService.calculate(request);

        Assertions.assertThat(shippingCalculation.cost()).isEqualTo(new Money("35.00"));
        Assertions.assertThat(shippingCalculation.expectedDate()).isEqualTo(LocalDate.now().plusDays(7));
    }

}