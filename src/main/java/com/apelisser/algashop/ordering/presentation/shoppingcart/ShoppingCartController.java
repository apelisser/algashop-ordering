package com.apelisser.algashop.ordering.presentation.shoppingcart;

import com.apelisser.algashop.ordering.application.shoppingcart.management.ShoppingCartItemInput;
import com.apelisser.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
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

    private final ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;
    private final ShoppingCartQueryService shoppingCartQueryService;

    public ShoppingCartController(ShoppingCartManagementApplicationService shoppingCartManagementApplicationService,
            ShoppingCartQueryService shoppingCartQueryService) {
        this.shoppingCartManagementApplicationService = shoppingCartManagementApplicationService;
        this.shoppingCartQueryService = shoppingCartQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@RequestBody @Valid ShoppingCartInput input) {
        UUID shoppingCartId = shoppingCartManagementApplicationService.createNew(input.getCustomerId());
        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable UUID shoppingCartId) {
        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public ShoppingCartItemListModel findItemsByShoppingCartId(@PathVariable UUID shoppingCartId) {
        List<ShoppingCartItemOutput> items = shoppingCartQueryService.findById(shoppingCartId).getItems();
        return new ShoppingCartItemListModel(items);
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID shoppingCartId) {
        shoppingCartManagementApplicationService.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty(@PathVariable UUID shoppingCartId) {
        shoppingCartManagementApplicationService.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable UUID shoppingCartId,
            @RequestBody @Valid ShoppingCartItemInput input) {
        input.setShoppingCartId(shoppingCartId);
        shoppingCartManagementApplicationService.addItem(input);
    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable UUID shoppingCartId, @PathVariable UUID itemId) {
        shoppingCartManagementApplicationService.removeItem(shoppingCartId, itemId);
    }

}
