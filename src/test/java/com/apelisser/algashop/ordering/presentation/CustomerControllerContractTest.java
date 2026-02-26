package com.apelisser.algashop.ordering.presentation;

import com.apelisser.algashop.ordering.application.customer.management.CustomerInput;
import com.apelisser.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.apelisser.algashop.ordering.application.customer.query.CustomerOutputTestDataBuilder;
import com.apelisser.algashop.ordering.application.customer.query.CustomerQueryService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoBean
    CustomerQueryService customerQueryService;

    @BeforeEach
    void setUpAll() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createCustomerContract() {
        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
            .thenReturn(UUID.randomUUID());

        Mockito.when(customerQueryService.findById(Mockito.any(UUID.class)))
            .thenReturn(CustomerOutputTestDataBuilder.existing().build());

        String jsonInput = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "john.doe@example.com",
              "document": "123.456.789-00",
              "phone": "(11) 99999-9999",
              "birthDate": "1991-07-05",
              "promotionNotificationsAllowed": false,
              "address": {
                "street": "Main Street",
                "number": "123",
                "complement": "Apartment 1",
                "neighborhood": "Center",
                "city": "New York",
                "state": "New York",
                "zipCode": "12345"
              }
            }
            """;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body(
                    "id", Matchers.notNullValue(),
                    "registeredAt", Matchers.notNullValue(),
                    "firstName", Matchers.is("John"),
                    "lastName", Matchers.is("Doe"),
                    "email", Matchers.is("john.doe@example.com"),
                    "document", Matchers.is("123.456.789-00"),
                    "phone", Matchers.is("(11) 99999-9999"),
                    "birthDate", Matchers.is("1991-07-05"),
                    "promotionNotificationsAllowed", Matchers.is(false),
                    "loyaltyPoints", Matchers.is(0),
                    "archived", Matchers.is(false),
                    "address", Matchers.notNullValue(),
                    "address.street", Matchers.is("Main Street"),
                    "address.number", Matchers.is("123"),
                    "address.complement", Matchers.is("Apartment 1"),
                    "address.neighborhood", Matchers.is("Center"),
                    "address.city", Matchers.is("New York"),
                    "address.zipCode", Matchers.is("12345")
                );
    }

    @Test
    void createCustomerErrorContract() {
        String jsonInput = """
            {
              "email": "john.doe@example.com",
              "document": "123.456.789-00",
              "phone": "(11) 99999-9999",
              "birthDate": "1991-07-05",
              "promotionNotificationsAllowed": false,
              "address": {
                "street": "Main Street",
                "number": "123",
                "complement": "Apartment 1",
                "neighborhood": "Center",
                "city": "New York",
                "state": "New York",
                "zipCode": "12345"
              }
            }
            """;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                    "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
                    "type", Matchers.is("/errors/invalid-field"),
                    "title", Matchers.notNullValue(),
                    "detail", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue(),
                    "fieldErrors", Matchers.notNullValue()
                );
    }

}