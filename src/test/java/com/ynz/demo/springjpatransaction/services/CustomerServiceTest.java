package com.ynz.demo.springjpatransaction.services;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.dto.OrderDto;
import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.exceptions.NoSuchCustomerException;
import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class CustomerServiceTest extends AbstractTest {
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

    @Test
    @Sql("classpath:testdata.sql")
    @Sql(value = "classpath:deleteTestData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenGivenDataInDB_FindCustomerByEmail() {
        String targetEmail = "mb@hotmail.com";
        CustomerDto found = customerService.findCustomerByEmail(targetEmail);
        String firstName = found.getFirstName();
        String lastName = found.getLastName();

        assertAll(
                () -> assertThat(firstName, is("Mike")),
                () -> assertThat(lastName, is("Brown"))
        );
    }

    @Test
    @Sql("classpath:testdata.sql")
    @Sql(value = "classpath:deleteTestData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenGivenCustomerEmail_FindItsOrders() {
        String targetEmail = "mp@hotmail.com";
        List<OrderDto> found = customerService.findCustomerOrderByEmail(targetEmail);

        assertAll(
                () -> assertThat(found, hasSize(1)),
                () -> assertNotNull(found.get(0)),
                () -> assertThat(found.get(0).getGivenOrderId(), is(Matchers.equalTo(UUID.fromString("f2062372-93d2-11eb-a8b3-0242ac130003"))))
        );
    }

}