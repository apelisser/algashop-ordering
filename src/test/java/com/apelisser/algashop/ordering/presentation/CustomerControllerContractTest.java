package com.apelisser.algashop.ordering.presentation;

import com.apelisser.algashop.ordering.application.commons.AddressData;
import com.apelisser.algashop.ordering.application.customer.management.CustomerInput;
import com.apelisser.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.apelisser.algashop.ordering.application.customer.management.CustomerUpdateInput;
import com.apelisser.algashop.ordering.application.customer.query.CustomerFilter;
import com.apelisser.algashop.ordering.application.customer.query.CustomerOutput;
import com.apelisser.algashop.ordering.application.customer.query.CustomerOutputTestDataBuilder;
import com.apelisser.algashop.ordering.application.customer.query.CustomerQueryService;
import com.apelisser.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.apelisser.algashop.ordering.application.customer.query.CustomerSummaryOutputTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.apelisser.algashop.ordering.domain.model.DomainException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        UUID customerId = UUID.randomUUID();
        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
            .thenReturn(customerId);

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
                .header("Location", Matchers.containsString("/api/v1/customers/" + customerId))
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
    void createCustomerError400Contract() {
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

    @Test
    void findAllCustomersContract() {
        int sizeLimit = 5;
        int pageNumber = 0;

        CustomerSummaryOutput customer1 = CustomerSummaryOutputTestDataBuilder.existing().build();
        CustomerSummaryOutput customer2 = CustomerSummaryOutputTestDataBuilder.existingAlt1().build();

        Mockito.when(customerQueryService.filter(Mockito.any(CustomerFilter.class)))
            .thenReturn(new PageImpl<>(List.of(customer1, customer2)));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("size", sizeLimit)
                .queryParam("page", pageNumber)
            .when()
                .get("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                    "number", Matchers.is(pageNumber),
                    "size", Matchers.is(2),
                    "totalPages", Matchers.is(1),
                    "totalElements", Matchers.is(2),
                    "content[0].id", Matchers.equalTo(customer1.getId().toString()),
                    "content[0].firstName", Matchers.equalTo(customer1.getFirstName()),
                    "content[0].lastName", Matchers.equalTo(customer1.getLastName()),
                    "content[0].email", Matchers.equalTo(customer1.getEmail()),
                    "content[0].document", Matchers.equalTo(customer1.getDocument()),
                    "content[0].phone", Matchers.equalTo(customer1.getPhone()),
                    "content[0].birthDate", Matchers.equalTo(customer1.getBirthDate().toString()),
                    "content[0].loyaltyPoints", Matchers.equalTo(customer1.getLoyaltyPoints()),
                    "content[0].archivedAt", Matchers.nullValue(),
                    "content[0].promotionNotificationsAllowed", Matchers.equalTo(customer1.getPromotionNotificationsAllowed()),
                    "content[0].archived", Matchers.equalTo(customer1.getArchived()),
                    "content[0].registeredAt", Matchers.equalTo(formatter.format(customer1.getRegisteredAt())),


                    "content[1].id", Matchers.equalTo(customer2.getId().toString()),
                    "content[1].firstName", Matchers.equalTo(customer2.getFirstName()),
                    "content[1].lastName", Matchers.equalTo(customer2.getLastName()),
                    "content[1].email", Matchers.equalTo(customer2.getEmail()),
                    "content[1].document", Matchers.equalTo(customer2.getDocument()),
                    "content[1].phone", Matchers.equalTo(customer2.getPhone()),
                    "content[1].birthDate", Matchers.equalTo(customer2.getBirthDate().toString()),
                    "content[1].loyaltyPoints", Matchers.equalTo(customer2.getLoyaltyPoints()),
                    "content[1].archivedAt", Matchers.nullValue(),
                    "content[1].promotionNotificationsAllowed", Matchers.equalTo(customer2.getPromotionNotificationsAllowed()),
                    "content[1].archived", Matchers.equalTo(customer2.getArchived()),
                    "content[1].registeredAt", Matchers.equalTo(formatter.format(customer2.getRegisteredAt()))
                );
    }

    @Test
    void findByIdContract() {
        CustomerOutput customer = CustomerOutputTestDataBuilder.existing().build();
        Mockito.when(customerQueryService.findById(customer.getId())).thenReturn(customer);

        AddressData address = customer.getAddress();

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers/{customerId}", customer.getId())
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                    "id", Matchers.is(customer.getId().toString()),
                    "firstName", Matchers.equalTo(customer.getFirstName()),
                    "lastName", Matchers.equalTo(customer.getLastName()),
                    "email", Matchers.equalTo(customer.getEmail()),
                    "document", Matchers.equalTo(customer.getDocument()),
                    "phone", Matchers.equalTo(customer.getPhone()),
                    "birthDate", Matchers.equalTo(customer.getBirthDate().toString()),
                    "promotionNotificationsAllowed", Matchers.equalTo(customer.getPromotionNotificationsAllowed()),
                    "loyaltyPoints", Matchers.equalTo(customer.getLoyaltyPoints()),
                    "archivedAt", Matchers.nullValue(),
                    "archived", Matchers.equalTo(customer.getArchived()),
                    "registeredAt", Matchers.equalTo(customer.getRegisteredAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
                    "address.street", Matchers.equalTo(address.getStreet()),
                    "address.number", Matchers.equalTo(address.getNumber()),
                    "address.complement", Matchers.equalTo(address.getComplement()),
                    "address.neighborhood", Matchers.equalTo(address.getNeighborhood()),
                    "address.city", Matchers.equalTo(address.getCity()),
                    "address.state", Matchers.equalTo(address.getState()),
                    "address.zipCode", Matchers.equalTo(address.getZipCode())
                );
    }

    @Test
    void findByIdError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();
        Mockito.when(customerQueryService.findById(invalidCustomerId))
            .thenThrow(new DomainEntityNotFoundException());

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers/{customerId}", invalidCustomerId)
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(
                  "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
                    "type", Matchers.is("/errors/not-found"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void createCustomerError409Contract() {
        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
            .thenThrow(new CustomerEmailIsInUseException());

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
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(
                    "status", Matchers.is(HttpStatus.CONFLICT.value()),
                    "type", Matchers.is("/errors/conflict"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void createCustomerError422Contract() {
        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
            .thenThrow(new DomainException());

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
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body(
                    "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                    "type", Matchers.is("/errors/unprocessable-entity"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void createCustomerError500Contract() {
        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
            .thenThrow(new RuntimeException("Internal Server Error"));

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
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(
                    "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "type", Matchers.is("/errors/internal"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void updateCustomerContract() {
        UUID customerId = UUID.randomUUID();
        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
            .thenReturn(customerId);

        CustomerOutput updatedCustomerOutput = CustomerOutputTestDataBuilder.existing()
            .id(customerId)
            .phone("(11) 88888-8888")
            .address(AddressData.builder()
                    .street("Bourbon Street")
                    .number("456")
                    .complement(null)
                    .neighborhood("Center")
                    .city("New York")
                    .state("New York")
                    .zipCode("54321")
                    .build())
            .build();

        Mockito.when(customerQueryService.findById(customerId)).thenReturn(updatedCustomerOutput);

        String jsonInput = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "phone": "(11) 88888-8888",
              "promotionNotificationsAllowed": false,
              "address": {
                "street": "Bourbon Street",
                "number": "456",
                "complement": null,
                "neighborhood": "Center",
                "city": "New York",
                "state": "New York",
                "zipCode": "54321"
              }
            }
            """;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/api/v1/customers/{customerId}", customerId)
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                    "id", Matchers.is(customerId.toString()),
                    "registeredAt", Matchers.notNullValue(),
                    "firstName", Matchers.is("John"),
                    "lastName", Matchers.is("Doe"),
                    "email", Matchers.is("john.doe@example.com"),
                    "document", Matchers.is("123.456.789-00"),
                    "phone", Matchers.is("(11) 88888-8888"),
                    "birthDate", Matchers.is("1991-07-05"),
                    "promotionNotificationsAllowed", Matchers.is(false),
                    "loyaltyPoints", Matchers.is(0),
                    "archived", Matchers.is(false),
                    "address", Matchers.notNullValue(),
                    "address.street", Matchers.is("Bourbon Street"),
                    "address.number", Matchers.is("456"),
                    "address.complement", Matchers.nullValue(),
                    "address.neighborhood", Matchers.is("Center"),
                    "address.city", Matchers.is("New York"),
                    "address.zipCode", Matchers.is("54321")
                );
    }

    @Test
    void updateCustomerError400Contract() {
        String jsonInput = """
            {
              "firstName": "John",
              "lastName": "   ",
              "phone": "(11) 88888-8888",
              "promotionNotificationsAllowed": false,
              "address": null
            }
            """;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/api/v1/customers/{customerId}", UUID.randomUUID())
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

    @Test
    void updateCustomerError409Contract() {
        Mockito.doThrow(new CustomerEmailIsInUseException())
            .when(customerManagementApplicationService).update(Mockito.any(UUID.class), Mockito.any(CustomerUpdateInput.class));

        Mockito.when(customerQueryService.findById(Mockito.any(UUID.class)))
            .thenReturn(CustomerOutputTestDataBuilder.existing().build());

        String jsonInput = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "phone": "(11) 88888-8888",
              "promotionNotificationsAllowed": false,
              "address": {
                "street": "Bourbon Street",
                "number": "456",
                "complement": null,
                "neighborhood": "Center",
                "city": "New York",
                "state": "New York",
                "zipCode": "54321"
              }
            }
            """;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/api/v1/customers/{customerId}", UUID.randomUUID())
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.CONFLICT.value())
                .body(
                    "status", Matchers.is(HttpStatus.CONFLICT.value()),
                    "type", Matchers.is("/errors/conflict"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void updateCustomerError422Contract() {
        Mockito.doThrow(new DomainException())
            .when(customerManagementApplicationService).update(Mockito.any(UUID.class), Mockito.any(CustomerUpdateInput.class));

        Mockito.when(customerQueryService.findById(Mockito.any(UUID.class)))
            .thenReturn(CustomerOutputTestDataBuilder.existing().build());

        String jsonInput = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "phone": "(11) 88888-8888",
              "promotionNotificationsAllowed": false,
              "address": {
                "street": "Bourbon Street",
                "number": "456",
                "complement": null,
                "neighborhood": "Center",
                "city": "New York",
                "state": "New York",
                "zipCode": "54321"
              }
            }
            """;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/api/v1/customers/{customerId}", UUID.randomUUID())
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body(
                    "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                    "type", Matchers.is("/errors/unprocessable-entity"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void deleteCustomerContract() {
        Mockito.doNothing()
            .when(customerManagementApplicationService).archive(Mockito.any(UUID.class));

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .delete("/api/v1/customers/{customerId}", UUID.randomUUID())
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteCustomerError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        Mockito.doThrow(new CustomerNotFoundException())
            .when(customerManagementApplicationService).archive(invalidCustomerId);

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .delete("/api/v1/customers/{customerId}", invalidCustomerId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(
                    "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
                    "type", Matchers.is("/errors/not-found"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void deleteCustomerError422Contract() {
        UUID customerId = UUID.randomUUID();

        Mockito.doThrow(new DomainException())
            .when(customerManagementApplicationService).archive(customerId);

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .delete("/api/v1/customers/{customerId}", customerId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body(
                    "status", Matchers.is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                    "type", Matchers.is("/errors/unprocessable-entity"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

    @Test
    void deleteCustomerError500Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        Mockito.doThrow(new RuntimeException())
            .when(customerManagementApplicationService).archive(invalidCustomerId);

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .delete("/api/v1/customers/{customerId}", invalidCustomerId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(
                    "status", Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "type", Matchers.is("/errors/internal"),
                    "title", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue()
                );
    }

}