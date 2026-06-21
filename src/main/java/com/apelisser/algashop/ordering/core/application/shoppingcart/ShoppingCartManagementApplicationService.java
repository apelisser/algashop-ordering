package com.apelisser.algashop.ordering.core.application.shoppingcart;

import com.apelisser.algashop.ordering.core.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.core.domain.model.product.Product;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductId;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemId;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingService;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ForManagingShoppingCarts;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class ShoppingCartManagementApplicationService implements ForManagingShoppingCarts {

    private final ShoppingCarts shoppingCarts;
    private final ProductCatalogService productCatalogService;
    private final ShoppingService shoppingService;

    public ShoppingCartManagementApplicationService(ShoppingCarts shoppingCarts,
            ProductCatalogService productCatalogService, ShoppingService shoppingService) {
        this.shoppingCarts = shoppingCarts;
        this.productCatalogService = productCatalogService;
        this.shoppingService = shoppingService;
    }

    @Transactional
    @Override
    public void addItem(ShoppingCartItemInput input) {
        Objects.requireNonNull(input);

        ShoppingCartId shoppingCartId = new ShoppingCartId(input.getShoppingCartId());
        ProductId productId = new ProductId(input.getProductId());

        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
            .orElseThrow(ShoppingCartNotFoundException::new);
        Product product = productCatalogService.ofId(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        shoppingCart.addItem(product, new Quantity(input.getQuantity()));
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public UUID createNew(UUID customerId) {
        Objects.requireNonNull(customerId);
        ShoppingCart shoppingCart = shoppingService.startShopping(new CustomerId(customerId));
        shoppingCarts.add(shoppingCart);
        return shoppingCart.id().value();
    }

    @Transactional
    @Override
    public void removeItem(UUID shoppingCartId, UUID shoppingCartItemId) {
        Objects.requireNonNull(shoppingCartId);
        Objects.requireNonNull(shoppingCartItemId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))
            .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCart.removeItem(new ShoppingCartItemId(shoppingCartItemId));
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public void empty(UUID shoppingCartId) {
        Objects.requireNonNull(shoppingCartId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))
            .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCart.empty();
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public void delete(UUID shoppingCartId) {
        Objects.requireNonNull(shoppingCartId);
        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))
            .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCarts.remove(shoppingCart.id());
    }

}
