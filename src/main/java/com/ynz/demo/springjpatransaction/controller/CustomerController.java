package com.ynz.demo.springjpatransaction.controller;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Void> createCustomer(@Validated @RequestBody Customer customer, UriComponentsBuilder builder) {
        log.info("create a customer.... ");
        Customer created = customerService.createCustomer(customer);
        URI resultUri = builder.path("/api/customers/{email}").buildAndExpand(created.getEmail()).toUri();

        return ResponseEntity.created(resultUri).build();
    }

    @GetMapping("{email}")
    public ResponseEntity<CustomerDto> findCustomerByEmail(@PathVariable("email") String email) {
        log.info("find a customer whose Email is " + email);
        CustomerDto customerDto = customerService.findCustomerByEmail(email);
        return ResponseEntity.status(HttpStatus.FOUND).body(customerDto);
    }

    @GetMapping(value = "{email}/orders")
    public ResponseEntity<List<Order>> findCustomerOrderByEmail(@PathVariable("email") String email) {
        log.info("find a customer's orders by its Email " + email);
        List<Order> orderList = customerService.findCustomerOrderByEmail(email);

        return ResponseEntity.status(HttpStatus.FOUND).body(orderList);
    }

    @GetMapping
    public ResponseEntity<Iterable<Customer>> findAllCustomer() {
        log.info("find all customers ...");
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @PutMapping("{email}")
    public ResponseEntity<Customer> addCustomerOrder(@PathVariable("email") String email, @Valid @RequestBody Order order) {
        log.info("add customer an oder ...");
        return ResponseEntity.ok(customerService.addCustomerOrder(email, order));
    }

}
