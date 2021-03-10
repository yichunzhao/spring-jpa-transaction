package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testSaveAOrder() {
        Order order = createDummyOrder();
        Order persisted = orderRepository.save(order);
        //fx-> orders
        List<Order> fOrders = persisted.getOrderItems().stream().map(OrderItem::getOrder).collect(toList());

        assertAll(
                () -> assertNotNull(persisted),
                () -> assertThat(persisted.getOrderItems(), hasSize(2)),
                () -> assertThat(fOrders, hasSize(2)),
                () -> assertThat(fOrders, everyItem(is(order)))
        );
    }

    @Test
    void whenFindCustomerOrdersByCustomerEmail_ItMayReturnListOrder() {
        Customer customer = createDummyCustomer();
        Order order = createDummyOrder();
        customer.addOrder(order);

        //persist a customer and his order in database
        testEntityManager.persistAndFlush(customer);

        List<Order> found = orderRepository.findByCustomerEmail(customer.getEmail());
        assertAll(
                () -> assertNotNull(found),
                () -> assertNotNull(found.get(0)),
                () -> assertNotNull(found.get(0).getOrderItems()),
                () -> assertThat(found, hasSize(1)),
                () -> assertThat(found.get(0).getOrderItems(), hasSize(2))
        );
    }

    @Test
    void whenFindCustomerWithoutOrders_ItReturnNull() {
        Customer customer = createDummyCustomer();
        testEntityManager.persistAndFlush(customer);
        List<Order> found = orderRepository.findByCustomerEmail(customer.getEmail());
        assertThat(found, empty());
    }

    private Order createDummyOrder() {
        Order order = new Order();
        //order.setCreationDateTime(OffsetDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("dish");

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setContent("soap");

        order.addOderItem(orderItem);
        order.addOderItem(orderItem1);

        return order;
    }

    private Customer createDummyCustomer() {
        Customer customer = new Customer();
        customer.setEmail("mb@hotmail.com");
        customer.setFirstName("Mike");
        customer.setLastName("Brown");
        return customer;
    }

}