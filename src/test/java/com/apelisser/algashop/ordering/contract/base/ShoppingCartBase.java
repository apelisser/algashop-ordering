package com.apelisser.algashop.ordering.contract.base;

import com.apelisser.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartOutputTestDataBuilder;
import com.apelisser.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.apelisser.algashop.ordering.presentation.shoppingcart.ShoppingCartController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebMvcTest(controllers = ShoppingCartController.class)
public class ShoppingCartBase {

    @Autowired
    WebApplicationContext context;

    @MockitoBean
    ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @MockitoBean
    ShoppingCartQueryService shoppingCartQueryService;

    public static final UUID VALID_CUSTOMER_ID = UUID.fromString("1e7f075c-ede5-49a0-a389-ba81052f45fb");
    public static final UUID VALID_SHOPPING_CART_ID = UUID.fromString("d71cb0ee-0632-4c8a-9dc4-7cb593b83f67");
    public static final UUID INVALID_SHOPPING_CART_ID = UUID.fromString("a90fd4bd-037a-43b7-9fba-011f3f5df99d");

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
            MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockValidShoppingCartFindById();
        mockInvalidShoppingCartFindById();
        mockCreateShoppingCart();
    }

    private void mockInvalidShoppingCartFindById() {
        Mockito.when(shoppingCartQueryService.findById(Mockito.eq(INVALID_SHOPPING_CART_ID)))
            .thenThrow(new ShoppingCartNotFoundException());
    }

    private void mockValidShoppingCartFindById() {
        Mockito.when(shoppingCartQueryService.findById(Mockito.eq(VALID_SHOPPING_CART_ID)))
            .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCart()
                .id(VALID_SHOPPING_CART_ID)
                .customerId(VALID_CUSTOMER_ID)
                .build());
    }

    private void mockCreateShoppingCart() {
        Mockito.when(shoppingCartManagementApplicationService.createNew(Mockito.eq(VALID_CUSTOMER_ID)))
            .thenReturn(VALID_SHOPPING_CART_ID);
    }

}
