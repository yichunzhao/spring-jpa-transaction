package com.ynz.demo.springjpatransaction.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * A controller integration test, starting a sever to test the http request and response.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:testdata.sql")
@Sql(value = "classpath:deleteTestData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CustomerControllerIT {

    @Autowired
    private CustomerController controller;

    @Autowired
    private TestRestTemplate template;

    @LocalServerPort
    private int port;

    @Test
    void testContextLoads() {
        assertAll(
                () -> assertThat(template).isNotNull(),
                () -> assertThat(controller).isNotNull(),
                () -> assertThat(port).isNotZero()
        );
    }

    @Test
    void testUriBuilding() {
        String email = "mb@hotmail.com";
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port)
                .pathSegment("api/customers", "{email}").buildAndExpand(email);
        String urlBuilt = uriComponents.toUri().toString();
        String expectedUri = "http://localhost:" + port + "/api/customers" + "/" + email;

        assertThat(urlBuilt).isEqualTo(expectedUri);
    }

}
