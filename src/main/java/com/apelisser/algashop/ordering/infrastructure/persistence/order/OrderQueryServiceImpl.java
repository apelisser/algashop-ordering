package com.apelisser.algashop.ordering.infrastructure.persistence.order;

import com.apelisser.algashop.ordering.application.order.query.OrderDetailOutput;
import com.apelisser.algashop.ordering.application.order.query.OrderQueryService;
import com.apelisser.algashop.ordering.application.utility.Mapper;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.OrderNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository repository;
    private final Mapper mapper;

    public OrderQueryServiceImpl(OrderPersistenceEntityRepository repository, Mapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public OrderDetailOutput findById(String id) {
        OrderPersistenceEntity entity = repository.findById(new OrderId(id).value().toLong())
            .orElseThrow(OrderNotFoundException::new);

        return mapper.convert(entity, OrderDetailOutput.class);
    }

}
