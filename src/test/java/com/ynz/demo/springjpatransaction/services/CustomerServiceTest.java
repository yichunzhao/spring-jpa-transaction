package com.ynz.demo.springjpatransaction.services;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.exceptions.NoSuchCustomerException;
import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class ser extends AbstractTest {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void whenAddCustomerOrder_CustomerNotExisted_ItThrowsNoSuchCustomerException() {
        Order order = createDummyOrder();
        assertThrows(NoSuchCustomerException.class, () -> customerService.addCustomerOrder("fake@hotmail.com", order));
    }

    @Test
    @Transactional
    void whenAddCustomerOrder_ThenCustomerContainsOrders() {
        Customer customer = createDummyCustomer();

        entityManager.persist(customer);
        entityManager.flush();
        Order order = createDummyOrder();
        assertThat(customer.getOrders(), empty());

        Customer afterOrderAdded = customerService.addCustomerOrder(customer.getEmail(), order);
        assertThat(afterOrderAdded.getOrders(), hasSize(1));
    }

}