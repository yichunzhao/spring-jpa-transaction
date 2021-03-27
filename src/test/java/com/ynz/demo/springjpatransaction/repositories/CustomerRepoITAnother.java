package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@Sql(value = "classpath:testdata.sql")
@Sql(value = "classpath:deleteTestdata.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Slf4j
public class CustomerRepoITAnother {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testCountCustomer() {
        log.info("counting number of customers");
        Long countOfCustomer = customerRepository.count();
        assertThat(countOfCustomer, is(2L));
    }

    /**
     * one-many default fetch-type is lazy. it generates One SQL query.
     */
    @Test
    void givenCustomerInDB_FindByItsEmail() {
        log.info("test find customer by its email against db");
        Optional<Customer> found = customerRepository.findByEmail("mb@hotmail.com");
        assertThat(found.isPresent(), is(true));
    }

    /**
     * one-many default fetch-type is lazy. it generates one sql query.
     */
    @Test
    void givenCustomerInDB_FindBYItsEmailByPSQL() {
        log.info("test find customer by PLSQL");
        Optional<Customer> found = customerRepository.findCustomerByEmailJPQL("mb@hotmail.com");
        assertThat(found.isPresent(), is(true));
    }

    @Test
    void countOrderCorrectly() {
        log.info("counting number of orders");
        Long totalOrderNumber = orderRepository.count();
        assertThat(totalOrderNumber, is(3L));
    }

    /**
     * comment: assertThrows fails to catch the LazyInitializationException
     */
    @Test
    void givenCustomerEmail_FindCustomer_ReturnNullOrders() {
        log.info("test find customer lazy loading order");
        Optional<Customer> found = customerRepository.findCustomerByEmailJPQL("mb@hotmail.com");
        Customer c = found.get();

        try {
            c.getOrders();
        } catch (Exception e) {
            assertThat(e, is(LazyInitializationException.class));
        }
    }

}
