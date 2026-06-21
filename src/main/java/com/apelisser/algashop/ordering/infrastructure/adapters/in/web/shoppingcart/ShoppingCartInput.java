package com.apelisser.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ShoppingCartInput {

    @NotNull
    private UUID customerId;

}
