package com.apelisser.algashop.ordering.presentation.order;

import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import com.apelisser.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIT {

    @LocalServerPort
    private int port;

    private static boolean databaseInitialized;
    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @Autowired
    CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();;
        RestAssured.port = port;

        JsonConfig jsonConfig = JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        RestAssured.config().jsonConfig(jsonConfig);

        initDatabase();
    }

    void initDatabase() {
        if (databaseInitialized) {
            return;
        }

        customerRepository.saveAndFlush(
            CustomerPersistenceEntityTestDataBuilder.existingCustomer()
                .id(validCustomerId)
                .build());
        databaseInitialized = true;

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

}
