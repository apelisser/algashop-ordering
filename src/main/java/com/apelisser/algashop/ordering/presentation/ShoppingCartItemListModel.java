package com.apelisser.algashop.ordering.presentation;

import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ShoppingCartItemListModel {

    private List<ShoppingCartItemOutput> items = new ArrayList<>();

    public ShoppingCartItemListModel(List<ShoppingCartItemOutput> items) {
        this.items = items;
    }

    public ShoppingCartItemListModel() {
    }

}
