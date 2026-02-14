package com.apelisser.algashop.ordering.infrastructure.persistence.customer;

import com.apelisser.algashop.ordering.application.customer.query.CustomerOutput;
import com.apelisser.algashop.ordering.application.customer.query.CustomerQueryService;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerPersistenceEntityRepository repository;

    public CustomerQueryServiceImpl(CustomerPersistenceEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public CustomerOutput findById(UUID customerId) {
        return repository.findByIdAsOutput(customerId).orElseThrow(CustomerNotFoundException::new);
    }

}
