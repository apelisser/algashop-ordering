package com.apelisser.algashop.ordering.core.domain.model;

public interface AggregateRoot<ID> extends DomainEventSource {

    ID id();

}
