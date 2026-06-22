package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.core.domain.model.commons.Money;
import com.apelisser.algashop.ordering.core.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.core.domain.model.product.Product;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductId;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItem;
import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.AbstractInfrastructureAPI;
import com.apelisser.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartUpdateProvider;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartsPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Import({
    ShoppingCartUpdateProvider.class,
    ShoppingCartsPersistenceProvider.class,
    ShoppingCartPersistenceEntityAssembler.class,
    ShoppingCartPersistenceEntityDisassembler.class,
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // to avoid @DirtiesContext
class ShoppingCartUpdateProviderIT extends AbstractInfrastructureAPI {

    private ShoppingCartsPersistenceProvider persistenceProvider;
    private CustomersPersistenceProvider customersPersistenceProvider;
    private ShoppingCartUpdateProvider shoppingCartUpdateProvider;

    @Autowired
    public ShoppingCartUpdateProviderIT(ShoppingCartsPersistenceProvider persistenceProvider,
            CustomersPersistenceProvider customersPersistenceProvider,
            ShoppingCartUpdateProvider shoppingCartUpdateProvider) {
        this.persistenceProvider = persistenceProvider;
        this.customersPersistenceProvider = customersPersistenceProvider;
        this.shoppingCartUpdateProvider = shoppingCartUpdateProvider;
    }

    @BeforeEach
    public void setup() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(
                CustomerTestDataBuilder.existingCustomer().build()
            );
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void shouldUpdateItemPriceAndTotalAmount() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product product1 = ProductTestDataBuilder.aProduct().price(new Money("2000")).build();
        Product product2 = ProductTestDataBuilder.aProductAltRamMemory().price(new Money("200")).build();

        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));

        persistenceProvider.add(shoppingCart);

        ProductId productIdToUpdate = product1.id();
        Money newProduct1Price = new Money("1500");
        Money expectedNewItemTotalPrice = newProduct1Price.multiply(new Quantity(2));
        Money expectedNewCartTotalAmount = expectedNewItemTotalPrice.add(new Money("200"));

        shoppingCartUpdateProvider.adjustPrice(productIdToUpdate, newProduct1Price);

        ShoppingCart updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        Assertions.assertThat(updatedShoppingCart.totalAmount()).isEqualTo(expectedNewCartTotalAmount);
        Assertions.assertThat(updatedShoppingCart.totalItems()).isEqualTo(new Quantity(3));

        ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

        Assertions.assertThat(item.totalAmount()).isEqualTo(expectedNewItemTotalPrice);
        Assertions.assertThat(item.price()).isEqualTo(newProduct1Price);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void shouldUpdateItemAvailability() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        Product product1 = ProductTestDataBuilder.aProduct()
            .price(new Money("2000"))
            .inStock(true)
            .build();
        Product product2 = ProductTestDataBuilder.aProductAltRamMemory()
            .price(new Money("200"))
            .inStock(true)
            .build();

        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));

        persistenceProvider.add(shoppingCart);

        ProductId productIdToUpdate = product1.id();
        ProductId productIdToNotUpdate = product2.id();

        shoppingCartUpdateProvider.changeAvailability(productIdToUpdate, false);

        ShoppingCart updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

        Assertions.assertThat(item.isAvailable()).isFalse();
        Assertions.assertThat(item.isAvailable()).isFalse();

        ShoppingCartItem item2 = updatedShoppingCart.findItem(productIdToNotUpdate);
        Assertions.assertThat(item2.isAvailable()).isTrue();
    }

}