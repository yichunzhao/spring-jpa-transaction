package com.ynz.demo.springjpatransaction.controller;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A client-side controller integration test, via a real sever to test the http request and response.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIT extends AbstractTest {

    @Autowired
    private CustomerController controller;

    @Autowired
    private TestRestTemplate template;

    @LocalServerPort
    private int port;

    private UriComponentsBuilder builder;

    @BeforeEach
    void setup() {
        builder = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port)
                .pathSegment("api/customers");
    }

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

    @Test
    void testCreatingCustomerWithoutOrders() {
        Customer customer = super.createDummyCustomer();

        URI uri = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port)
                .pathSegment("api/customers").build().toUri();

        HttpEntity<Customer> request = new HttpEntity<>(customer);
        ResponseEntity<Void> response = this.template.postForEntity(uri, request, Void.class);
        URI location = response.getHeaders().getLocation();

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(location),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(location.toString()).contains("api/customers/" + customer.getEmail())
        );
    }

    @Test
    @Sql("classpath:testdata.sql")
    @Sql(value = "classpath:deleteTestData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindCustomerByEmail() {
        String email = "mp@hotmail.com";
        URI uri = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port)
                .pathSegment("api/customers", "{email}").buildAndExpand(email).toUri();

        ResponseEntity<CustomerDto> response = this.template.getForEntity(uri, CustomerDto.class);
        CustomerDto customerDto = response.getBody();

        assertAll(
                () -> assertNotNull(response),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND),
                () -> {
                    assertNotNull(customerDto);
                    assertNotNull(customerDto.getFirstName());
                    assertNotNull(customerDto.getLastName());
                },
                () -> assertEquals(customerDto.getFirstName(), "Mia"),
                () -> assertEquals(customerDto.getLastName(), "Peterson")
        );
    }

}
