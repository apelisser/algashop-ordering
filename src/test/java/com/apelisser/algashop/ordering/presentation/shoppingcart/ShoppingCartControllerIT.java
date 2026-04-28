package com.apelisser.algashop.ordering.presentation.shoppingcart;

import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.apelisser.algashop.ordering.utils.AlgaShopResourceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShoppingCartControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    private WireMockServer wireMockProductCatalog;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        JsonConfig jsonConfig = JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        RestAssured.config().jsonConfig(jsonConfig);

        customerRepository.saveAndFlush(
            CustomerPersistenceEntityTestDataBuilder.existingCustomer()
                .id(validCustomerId)
                .build());

        wireMockProductCatalog = new WireMockServer(WireMockConfiguration.options()
            .port(8781)
            .usingFilesUnderClasspath("src/test/resources/wiremock/product-catalog")
            .extensions(new ResponseTemplateTransformer(true)));

        wireMockProductCatalog.start();
    }

    @AfterEach
    void tearDown() {
        wireMockProductCatalog.stop();
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
                    "id", Matchers.not(Matchers.emptyString()),
                    "customerId", Matchers.is(validCustomerId.toString())
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

        ShoppingCartPersistenceEntity newShoppingCart = ShoppingCartPersistenceEntityTestDataBuilder
            .existingShoppingCart()
            .id(shoppingCartId)
            .customer(customerRepository.getReferenceById(validCustomerId))
            .totalItems(0)
            .totalAmount(BigDecimal.ZERO)
            .items(null)
            .build();

        shoppingCartRepository.saveAndFlush(newShoppingCart);

        RestAssured
            .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", shoppingCartId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        ShoppingCartPersistenceEntity updatedShoppingCart = shoppingCartRepository.findByCustomer_Id(validCustomerId).orElseThrow();

        Assertions.assertThat(updatedShoppingCart.getId()).isEqualTo(shoppingCartId);
        Assertions.assertThat(updatedShoppingCart.getTotalItems()).isEqualTo(2);
        Assertions.assertThat(updatedShoppingCart.getTotalAmount()).isEqualTo(new BigDecimal("2000.00"));
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