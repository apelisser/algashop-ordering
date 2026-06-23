package com.apelisser.algashop.ordering.infrastructure.adapters.in.web;

import com.apelisser.algashop.ordering.utils.TestcontainerPostgreSqlConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // to avoid @DirtiesContext
@Sql(scripts = "classpath:db/testdata/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Import(TestcontainerPostgreSqlConfig.class)
public abstract class AbstractPresentationIT {

    @LocalServerPort
    protected int port;

    protected static WireMockServer wireMockProductCatalog;
    protected static WireMockServer wireMockRapidex;

    protected static void initWireMock() {
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

    protected static void stopWireMock() {
        wireMockRapidex.stop();
        wireMockProductCatalog.stop();
    }

    protected void beforeEach() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        JsonConfig jsonConfig = JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        RestAssured.config().jsonConfig(jsonConfig);

        if (!wireMockProductCatalog.isRunning()) {
            wireMockProductCatalog.start();
        }

        if (!wireMockRapidex.isRunning()) {
            wireMockRapidex.start();
        }
    }

}
