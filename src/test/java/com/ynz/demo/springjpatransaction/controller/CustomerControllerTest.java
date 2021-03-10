package com.ynz.demo.springjpatransaction.controller;

import com.ynz.demo.springjpatransaction.CustomerService;
import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;


@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    private static final String rootURI = "/api/customers";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void testCustomerCreation() {
        String email = "yz@hotmail.com";
        String firstName = "Mike";
        String lastName = "Brown";
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);

        Customer persisted = new Customer();
        persisted.setFirstName(firstName);
        persisted.setLastName(lastName);
        persisted.setEmail(email);

        Mockito.when(customerService.createCustomer(any(Customer.class))).thenReturn(persisted);

        RestAssuredMockMvc
                .given()
                .body(customer)
                .contentType(ContentType.JSON)
                .when()
                .post(rootURI)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void whenCreateCustomerWithoutLastName_ThenItReturnBadRequest() {
        String firstName = "Mike";
        String lastName = null;
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        RestAssuredMockMvc
                .when()
                .post(rootURI, customer)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testFindAllCustomers() {
        Customer customer = new Customer();

        Mockito.when(customerService.findAllCustomers()).thenReturn(
                Arrays.asList(customer)
        );

        RestAssuredMockMvc
                .when()
                .get(rootURI)
                .then()
                .statusCode(200)
                .body("$.size()", is(1));
    }

    @Test
    void testFindCustomerByEmail() {
        String email = "yz@hotmail.com";
        String firstName = "Mike";
        String lastName = "Brown";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);

        Mockito.when(customerService.findCustomerByEmail(email)).thenReturn(customer);

        RestAssuredMockMvc
                .when()
                .get(rootURI + "/" + email)
                .then()
                .statusCode(200)
                .body("email", is(email))
                .body("firstName", is(firstName))
                .body("lastName", is(lastName));
    }

    @Test
    void whenCreateCustomerWithoutEmail_ThenItReturnBadRequest() {
        String firstName = "Mike";
        String lastName = "Brown";
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        RestAssuredMockMvc
                .when()
                .post(rootURI, customer)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void whenCreateCustomerWithoutEmptyFirstName_ThenItReturnBadRequest() {
        String firstName = "";
        String lastName = "Brown";
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        RestAssuredMockMvc
                .when()
                .post(rootURI, customer)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createOrderAndAssociateToCustomer() {
        Customer customer = createDummyCustomer();
        Order order = createDummyOrder();
        customer.addOrder(order);

        String email = customer.getEmail();

        Mockito.when(customerService.addCustomerOrder(any(String.class), any(Order.class))).thenReturn(customer);

        RestAssuredMockMvc.given()
                .body(order)
                .contentType(ContentType.JSON)
                .when().put(rootURI + "/{email}", email)
                .then()
                .statusCode(200)
                .body("orders.size()", is(1))
                .body("orders[0].orderItems.size()", is(2));
    }

    private Customer createDummyCustomer() {
        String firstName = "Mike";
        String lastName = "Brown";
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail("mb@hotmail.com");
        return customer;
    }

    private Order createDummyOrder() {
        Order order = new Order();

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("iphone11");
        order.addOderItem(orderItem);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setContent("lenovo e490");
        order.addOderItem(orderItem1);

        return order;
    }

}