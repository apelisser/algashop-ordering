package com.apelisser.algashop.ordering.presentation.shoppingcart;

import com.apelisser.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.apelisser.algashop.ordering.presentation.AbstractPresentationIT;
import com.apelisser.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

class ShoppingCartControllerIT extends AbstractPresentationIT {

    @Autowired
    ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    static final UUID validShoppingCartId = UUID.fromString("4f31582a-66e6-4601-a9d3-ff608c2d4461");

    @BeforeAll
    static void setUpAll() {
        AbstractPresentationIT.initWireMock();
    }

    @AfterAll
    static void tearDownAll() {
        AbstractPresentationIT.stopWireMock();
    }

    @BeforeEach
    void setUp() {
        super.beforeEach();
    }

    @Test
    void shouldCreateShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-shoppingcart.json");

        String shoppingCartId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts")
            .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    "id", Matchers.not(Matchers.emptyString())
                )
                .extract().jsonPath().getString("id");

        boolean shoppingCartExists = shoppingCartRepository.existsById(new ShoppingCartId(shoppingCartId).value());
        Assertions.assertThat(shoppingCartExists).isTrue();
    }

    @Test
    void shouldNotCreateShoppingCartWhenThereIsInvalidData() {
        String json = AlgaShopResourceUtils.readContent("json/create-shoppingcart-with-invalid-data.json");

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts")
            .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

    @Test
    void shouldAddItemToShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/add-shoppingcart-item.json");
        UUID shoppingCartId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

        RestAssured
            .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", validShoppingCartId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        ShoppingCartPersistenceEntity updatedShoppingCart = shoppingCartRepository.findById(validShoppingCartId).orElseThrow();

        Assertions.assertThat(updatedShoppingCart.getTotalItems()).isEqualTo(4);
        Assertions.assertThat(updatedShoppingCart.getTotalAmount()).isEqualTo(new BigDecimal("4000.00"));
    }

    @Test
    void shouldNotAddItemToANonExistentShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/add-shoppingcart-item.json");
        UUID nonExistentShoppingCartId = UUID.randomUUID();

        RestAssured
            .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", nonExistentShoppingCartId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

}