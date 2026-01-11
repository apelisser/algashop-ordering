package com.apelisser.algashop.ordering.infrastructure.beans;

import com.apelisser.algashop.ordering.domain.model.utility.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
    basePackages = "com.apelisser.algashop.ordering.domain.model",
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        value = DomainService.class
    )
)
public class DomainServiceScanConfig {

}
