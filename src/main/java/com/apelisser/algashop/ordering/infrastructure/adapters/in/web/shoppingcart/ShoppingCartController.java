package com.apelisser.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemInput;
import com.apelisser.algashop.ordering.core.application.shoppingcart.ForManagingShoppingCarts;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemOutput;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.apelisser.algashop.ordering.presentation.UnprocessableEntityException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-carts")
public class ShoppingCartController {

    private final ForManagingShoppingCarts forManagingShoppingCarts;
    private final ForQueryingShoppingCarts forQueryingShoppingCarts;

    public ShoppingCartController(ForManagingShoppingCarts forManagingShoppingCarts,
            ForQueryingShoppingCarts forQueryingShoppingCarts) {
        this.forManagingShoppingCarts = forManagingShoppingCarts;
        this.forQueryingShoppingCarts = forQueryingShoppingCarts;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@RequestBody @Valid ShoppingCartInput input) {
        UUID shoppingCartId;
        try {
            shoppingCartId = forManagingShoppingCarts.createNew(input.getCustomerId());
        } catch (CustomerNotFoundException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
        return forQueryingShoppingCarts.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable UUID shoppingCartId) {
        return forQueryingShoppingCarts.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public ShoppingCartItemListModel findItemsByShoppingCartId(@PathVariable UUID shoppingCartId) {
        List<ShoppingCartItemOutput> items = forQueryingShoppingCarts.findById(shoppingCartId).getItems();
        return new ShoppingCartItemListModel(items);
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID shoppingCartId) {
        forManagingShoppingCarts.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty(@PathVariable UUID shoppingCartId) {
        forManagingShoppingCarts.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable UUID shoppingCartId,
            @RequestBody @Valid ShoppingCartItemInput input) {
        input.setShoppingCartId(shoppingCartId);
        try {
            forManagingShoppingCarts.addItem(input);
        } catch (ProductNotFoundException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable UUID shoppingCartId, @PathVariable UUID itemId) {
        forManagingShoppingCarts.removeItem(shoppingCartId, itemId);
    }

}
