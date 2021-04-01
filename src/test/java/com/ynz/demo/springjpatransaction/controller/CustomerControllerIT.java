package com.ynz.demo.springjpatransaction.controller;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.exceptions.ErrorMsgModel;
import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A client-side controller integration test, via a real sever and a real database to test the http request and response.
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
    void givenNonExistedCustomer_RegisterItInSystem() {
        CustomerDto customer = super.createDummyCustomerDto();

        URI uri = builder.build().toUri();

        HttpEntity<CustomerDto> request = new HttpEntity<>(customer);
        ResponseEntity<CustomerDto> response = this.template.postForEntity(uri, request, CustomerDto.class);
        URI location = response.getHeaders().getLocation();
        CustomerDto created = response.getBody();

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(location),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(location.toString()).contains("api/customers/" + customer.getEmail()),
                () -> assertNotNull(created),
                () -> assertEquals(created.getEmail(), "mb@hotmail.com"),
                () -> assertEquals(created.getFirstName(), "Mike"),
                () -> assertEquals(created.getLastName(), "Brown")
        );
    }

    @Test
    @Sql("classpath:testdata.sql")
    @Sql(value = "classpath:deleteTestData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenCustomerAlreadyRegistered_ThrowDuplicatedCustomerException() {
        CustomerDto customerDto = CustomerDto.builder().email("mp@hotmail.com").firstName("Mia")
                .lastName("Peterson").build();

        URI uri = builder.build().toUri();
        HttpEntity<CustomerDto> request = new HttpEntity<>(customerDto);
        ResponseEntity<ErrorMsgModel> response = this.template.postForEntity(uri, request, ErrorMsgModel.class);

        HttpStatus statusCode = response.getStatusCode();
        ErrorMsgModel errorMsg = response.getBody();

        assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(errorMsg.getMessage()).contains("already existed")
        );
    }

    @Test
    void givenCustomerWithoutFirstName_GetBadRequestHttpStatusAndErrorMsg() {
        CustomerDto customerDto = CustomerDto.builder().email("mp@hotmail.com").firstName("")
                .lastName("Peterson").build();

        URI uri = builder.build().toUri();
        HttpEntity<CustomerDto> request = new HttpEntity<>(customerDto);
        ResponseEntity<ErrorMsgModel> response = this.template.postForEntity(uri, request, ErrorMsgModel.class);

        HttpStatus statusCode = response.getStatusCode();
        ErrorMsgModel errorMsg = response.getBody();

        assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(errorMsg.getMessage()).contains("customer must have a first name.")
        );
    }

    @Test
    void whenCustomerGivingInvalidEmail_ThenItGetBadRequestAndErrorMsg() {
        CustomerDto customerDto = CustomerDto.builder().email("hotmail.com").firstName("")
                .lastName("Peterson").build();

        URI uri = builder.build().toUri();
        HttpEntity<CustomerDto> request = new HttpEntity<>(customerDto);
        ResponseEntity<ErrorMsgModel> response = this.template.postForEntity(uri, request, ErrorMsgModel.class);

        HttpStatus statusCode = response.getStatusCode();
        ErrorMsgModel errorMsg = response.getBody();

        assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(errorMsg.getMessage()).contains("email address is not valid.")
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

    @Test
    @Sql("classpath:testdata.sql")
    @Sql(value = "classpath:deleteTestData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindAllCustomers() {
        URI uri = builder.build().toUri();

        //this.template.getForEntity(uri, CustomerDto[].class);
        ResponseEntity<List<CustomerDto>> response = this.template.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CustomerDto>>() {
                });

        List<CustomerDto> customerDtoList = response.getBody();

        List<String> firstNames = Arrays.asList("Mike", "Mia");
        boolean matched = customerDtoList.stream().map(c -> c.getFirstName()).collect(toList()).equals(firstNames);

        assertAll(
                () -> assertThat(customerDtoList).hasSize(2),
                () -> assertThat(matched).isTrue()
        );
    }

    @Test
    @Sql("classpath:testdata.sql")
    @Sql(value = "classpath:deleteTestData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindListCustomerByArray() {
        URI uri = builder.build().toUri();

        ResponseEntity<CustomerDto[]> response = this.template.getForEntity(uri, CustomerDto[].class);

        CustomerDto[] customerDtoList = response.getBody();
        CustomerDto[] emptyArray = {};

        List<String> firstNames = Arrays.asList("Mike", "Mia");
        boolean matched = Arrays.stream(customerDtoList != null ? customerDtoList : emptyArray)
                .map(c -> c.getFirstName()).collect(toList()).equals(firstNames);

        assertAll(
                () -> assertThat(customerDtoList).hasSize(2),
                () -> assertThat(matched).isTrue()
        );
    }

}
