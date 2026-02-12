package com.apelisser.algashop.ordering.infrastructure.beans;

import com.apelisser.algashop.ordering.domain.model.order.CustomerHaveFreeShippingSpecification;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpecificationBeansConfig {

    @Bean
    public CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification(Orders orders) {
        return new CustomerHaveFreeShippingSpecification(
            orders,
            200,
            2L,
            2000
        );
    }

}
