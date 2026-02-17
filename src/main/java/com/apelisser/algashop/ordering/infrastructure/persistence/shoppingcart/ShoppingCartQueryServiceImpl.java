package com.apelisser.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.apelisser.algashop.ordering.application.utility.Mapper;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {

    private final ShoppingCartPersistenceEntityRepository shoppingCartRepository;
    private final Mapper mapper;

    public ShoppingCartQueryServiceImpl(ShoppingCartPersistenceEntityRepository shoppingCartRepository, Mapper mapper) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.mapper = mapper;
    }

    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        return shoppingCartRepository.findById(shoppingCartId)
            .map(entity -> mapper.convert(entity, ShoppingCartOutput.class))
            .orElseThrow(ShoppingCartNotFoundException::new);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        return shoppingCartRepository.findByCustomer_Id(customerId)
            .map(entity -> mapper.convert(entity, ShoppingCartOutput.class))
            .orElseThrow(ShoppingCartNotFoundException::new);
    }

}
