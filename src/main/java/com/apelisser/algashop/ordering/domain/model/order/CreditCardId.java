package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record CreditCardId(UUID id) {

    public CreditCardId {
        Objects.requireNonNull(id);
    }

    public CreditCardId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

}
