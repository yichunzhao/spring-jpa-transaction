package com.ynz.demo.springjpatransaction;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerServiceTest {

    @MockBean
    private CustomerRepository customerRepository;

    @Test
    void testPersistCustomerWithOrder() {
        Customer customer = new Customer();
        customer.setEmail("yz@hotmail.com");
        customer.setFirstName("Mike");
        customer.setLastName("Zhao");

        Order order = new Order();
        order.setCreationDateTime(OffsetDateTime.now());

        customer.addOrder(order);
        Customer persisted = customerRepository.save(customer);

        assertAll(
                () -> assertNotNull(persisted),
                () -> assertNotNull(persisted.getId()),
                () -> assertNotNull(persisted.getOrders()),
                () -> assertThat(persisted.getOrders(), hasSize(1))
        );


    }


}