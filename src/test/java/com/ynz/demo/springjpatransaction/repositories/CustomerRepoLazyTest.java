package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.dto.OrderDto;
import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.util.AbstractTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
public class CustomerRepoLazyTest extends AbstractTest {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        log.info("persist a customer with an order ");
        Customer customer = createDummyCustomer();
        Order order = createDummyOrder();
        customer.addOrder(order);

        em.persist(customer);
        em.flush();
    }

    /**
     * one-to-many relationship, default fetch type is lazy loading.
     * when loading the customer from db, it generates only one sql.
     */
    @Test
    @Transactional
    void withoutTransactionBoundary_LazyLoadingShouldWorks() {
        log.info("find this customer from db");
        Optional<CustomerDto> found = repository.findByEmail("mb@hotmail.com");
        assertThat(found.isPresent(), is(true));
    }

    /**
     * The derived query generates left outer join sql query.
     */
    @Test
    @Transactional
    void loadOrderCustomerFromOrderManyToOneSide() {
        log.info("find order by customer email from db");
        List<OrderDto> orderList = orderRepository.findByCustomerEmail("mb@hotmail.com");
        assertThat(orderList, hasSize(1));
    }

    @Test
    @Transactional
    void loadOrderByItsId() {
        log.info("find order by its id");
        Order order = super.createDummyOrder();
        em.persist(order);
        em.flush();

        Optional<Order> found = orderRepository.findById(order.getOrderId());
        assertTrue(found.isPresent());
    }

}
