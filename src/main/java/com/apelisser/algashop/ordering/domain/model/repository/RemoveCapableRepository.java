package com.apelisser.algashop.ordering.domain.model.repository;

import com.apelisser.algashop.ordering.domain.model.entity.AggregateRoot;

public interface RemoveCapableRepository<T extends AggregateRoot<ID>, ID> extends Repository<T, ID> {

    void remove(T aggregateRoot);

    void remove(ID id);

}
