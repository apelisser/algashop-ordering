package com.apelisser.algashop.ordering.domain.valueobject.id;

import com.apelisser.algashop.ordering.domain.utility.IdGenerator;
import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record ShoppingCartId(TSID value) {

    public ShoppingCartId {
        Objects.requireNonNull(value);
    }

    public ShoppingCartId(Long value) {
        this(TSID.from(value));
    }

    public ShoppingCartId() {
        this(IdGenerator.generateTSID());
    }

    public ShoppingCartId(String value) {
        this(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
