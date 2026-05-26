package com.apelisser.algashop.ordering.presentation.order;

import com.apelisser.algashop.ordering.application.checkout.BuyNowInput;
import com.apelisser.algashop.ordering.application.checkout.BuyNowInputTestDataBuilder;
import com.apelisser.algashop.ordering.application.order.query.OrderDetailOutput;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import com.apelisser.algashop.ordering.utils.AlgaShopResourceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureStubRunner(
//    stubsMode = StubRunnerProperties.StubsMode.LOCAL,
//    ids = "com.apelisser.algashop:product-catalog:0.0.1-SNAPSHOT:8781"
//)
//@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // to avoid @DirtiesContext
@Sql(scripts = "classpath:db/testdata/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class OrderControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");
    static final UUID validProductId = UUID.fromString("fffe4676-367b-4015-941a-41c31c3b3d3e");

    static WireMockServer wireMockProductCatalog;
    static WireMockServer wireMockRapidex;

    @BeforeAll
    static void setUpAll() {
        wireMockRapidex = new WireMockServer(WireMockConfiguration.options()
            .port(8780)
            .usingFilesUnderClasspath("src/test/resources/wiremock/rapidex")
            .extensions(new ResponseTemplateTransformer(true)));

        wireMockProductCatalog = new WireMockServer(WireMockConfiguration.options()
            .port(8781)
            .usingFilesUnderClasspath("src/test/resources/wiremock/product-catalog")
            .extensions(new ResponseTemplateTransformer(true)));

        wireMockRapidex.start();
        wireMockProductCatalog.start();
    }

    @AfterAll
    static void tearDownAll() {
        wireMockRapidex.stop();
        wireMockProductCatalog.stop();
    }

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        JsonConfig jsonConfig = JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        RestAssured.config().jsonConfig(jsonConfig);

        if (!wireMockRapidex.isRunning()) {
            wireMockRapidex.start();
        }

        if (!wireMockProductCatalog.isRunning()) {
            wireMockProductCatalog.start();
        }
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
        BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
            .productId(validProductId)
            .customerId(validCustomerId)
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
