package com.apelisser.algashop.ordering.presentation.order;

import com.apelisser.algashop.ordering.application.checkout.BuyNowInput;
import com.apelisser.algashop.ordering.application.checkout.BuyNowInputTestDataBuilder;
import com.apelisser.algashop.ordering.application.order.query.OrderDetailOutput;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
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

import java.util.UUID;

public class OrderControllerIT extends AbstractPresentationIT {

    @Autowired
    OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");
    static final UUID validProductId = UUID.fromString("fffe4676-367b-4015-941a-41c31c3b3d3e");

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
    void shouldCreateOrderUsingProduct() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

        String createdOrderId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    "id", Matchers.not(Matchers.emptyString()),
                    "customer.id", Matchers.is(validCustomerId.toString()))
                .extract().jsonPath().getString("id");

        boolean orderExists = orderPersistenceEntityRepository.existsById(new OrderId(createdOrderId).value().toLong());
        Assertions.assertThat(orderExists).isTrue();
    }

    @Test
    void shouldCreateOrderUsingProduct_DTO() {
        UUID creditCardId = UUID.randomUUID();
        BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
            .productId(validProductId)
            .customerId(validCustomerId)
            .creditCardId(creditCardId)
            .build();

        OrderDetailOutput orderDetailOutput = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(input)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    "id", Matchers.not(Matchers.emptyString()),
                    "customer.id", Matchers.is(validCustomerId.toString()))
                .extract().body().as(OrderDetailOutput.class);

        Assertions.assertThat(orderDetailOutput.getCustomer().getId()).isEqualTo(validCustomerId);
        Assertions.assertThat(orderDetailOutput.getCreditCardId()).isEqualTo(creditCardId);

        boolean orderExists = orderPersistenceEntityRepository.existsById(new OrderId(orderDetailOutput.getId()).value().toLong());
        Assertions.assertThat(orderExists).isTrue();
    }

    @Test
    void shouldNotCreateOrderUsingProductWhenProductAPIIsUnavailable() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

        wireMockProductCatalog.stop();

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.GATEWAY_TIMEOUT.value());
    }

    @Test
    void shouldNotCreateOrderUsingProductWhenProductNotExists() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-invalid-product.json");

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void shouldNotCreateOrderUsingProductWhenCustomerWasNotFound() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product-and-invalid-customer.json");

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

    @Test
    void shouldCreateOrderUsingShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-shoppingcart.json");

        String createdOrderId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    "id", Matchers.not(Matchers.emptyString()),
                    "customer.id", Matchers.is(validCustomerId.toString()))
                .extract().jsonPath().getString("id");

        boolean orderExists = orderPersistenceEntityRepository.existsById(new OrderId(createdOrderId).value().toLong());
        Assertions.assertThat(orderExists).isTrue();
    }

    @Test
    void shouldReturnUnprocessableEntityWhenCheckingOutEmptyShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-empty-shoppingcart.json");

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

    @Test
    void shouldReturnUnprocessableEntityWhenCheckingOutNonExistentShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-non-existent-shoppingcart.json");

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

}
