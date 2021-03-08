package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Mike");
        customer.setLastName("Brown");
        customer.setEmail("yz@hotmail.com");

        Order order = new Order();
        order.setCreationDateTime(OffsetDateTime.now());
        customer.addOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("iphone8");

        order.addOderItem(orderItem);

        Order order1 = new Order();
        order1.setCreationDateTime(OffsetDateTime.now());
        customer.addOrder(order1);

        OrderItem orderItem1 = new OrderItem();
        orderItem.setContent("cannon printer");
        order1.addOderItem(orderItem1);

        Customer persisted = customerRepository.save(customer);
        assertAll(
                () -> assertNotNull(persisted),
                () -> assertThat(persisted.getEmail(), is("yz@hotmail.com")),
                () -> assertThat(persisted.getOrders(), hasSize(2))
        );
    }

    @Test
    void testFindCustomerByEmail() {
        Customer customer = new Customer();
        customer.setFirstName("Mike");
        customer.setLastName("Brown");
        customer.setEmail("yz@hotmail.com");

        Order order = new Order();
        order.setCreationDateTime(OffsetDateTime.now());
        customer.addOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("iphone8");

        order.addOderItem(orderItem);

        Order order1 = new Order();
        order1.setCreationDateTime(OffsetDateTime.now());
        customer.addOrder(order1);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setContent("cannon printer");
        order1.addOderItem(orderItem1);

        Customer persisted = entityManager.persistAndFlush(customer);
        String email = persisted.getEmail();

        Optional<Customer> found = customerRepository.findByEmail(email);


        assertAll(
                () -> assertTrue(found.isPresent()),
                () -> assertThat(found.get().getOrders(), hasSize(2)),
                () -> assertThat(found.get().getFirstName(), is("Mike")),
                () -> assertThat(found.get().getLastName(), is("Brown"))
        );

    }

}