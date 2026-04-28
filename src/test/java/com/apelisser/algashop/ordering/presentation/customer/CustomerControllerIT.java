package com.apelisser.algashop.ordering.presentation.customer;

import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.apelisser.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerControllerIT {

    @LocalServerPort
    int port;

    static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @Autowired
    CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
    }

    @Test
    void shouldCreateCustomer() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        String createdCustomerId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    "id", Matchers.not(Matchers.emptyString()))
                .extract().jsonPath().getString("id");

        boolean customerExists = customerPersistenceEntityRepository.existsById(UUID.fromString(createdCustomerId));
        Assertions.assertThat(customerExists).isTrue();
    }

    @Test
    void shouldNotCreateCustomerWithInvalidData() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer-with-invalid-data.json");

        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(
                    "status", Matchers.equalTo(HttpStatus.BAD_REQUEST.value()),
                    "title", Matchers.not(Matchers.emptyString()),
                    "type", Matchers.not(Matchers.emptyString()),
                    "detail", Matchers.not(Matchers.emptyString()),
                    "fields.email", Matchers.not(Matchers.emptyString()),
                    "fields.phone", Matchers.not(Matchers.emptyString()),
                    "fields.document", Matchers.not(Matchers.emptyString()),
                    "fields.birthDate", Matchers.not(Matchers.emptyString()),
                    "fields.promotionNotificationsAllowed", Matchers.not(Matchers.emptyString()),
                    "fields.address", Matchers.not(Matchers.emptyString())
                );
    }

    @Test
    void shouldArchiveCustomer() {
        customerPersistenceEntityRepository.saveAndFlush(CustomerPersistenceEntityTestDataBuilder.existingCustomer()
            .id(validCustomerId)
            .build());

        RestAssured
            .when()
                .delete("/api/v1/customers/{customerId}", validCustomerId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        CustomerPersistenceEntity customer = customerPersistenceEntityRepository.findById(validCustomerId).orElseThrow();

        Assertions.assertThat(customer.getArchived()).isTrue();
        Assertions.assertThat(customer.getArchivedAt()).isNotNull();
    }

    @Test
    void shouldReturnNotFoundWhenArchivingNonExistentCustomer() {
        UUID nonExistentCustomerId = UUID.randomUUID();

        RestAssured
            .when()
                .delete("/api/v1/customers/{customerId}", nonExistentCustomerId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        Assertions.assertThat(customerPersistenceEntityRepository.existsById(nonExistentCustomerId)).isFalse();
    }

}