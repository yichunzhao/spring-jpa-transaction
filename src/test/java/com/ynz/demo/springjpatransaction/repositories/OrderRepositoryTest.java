package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
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

    @Test
    void testSaveAOrder() {
        Order order = new Order();
        order.setCreationDateTime(OffsetDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("dish");

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setContent("soap");

        order.addOderItem(orderItem);
        order.addOderItem(orderItem1);

        Order persisted = orderRepository.save(order);
        //fx-> orders
        List<Order> fOrders = persisted.getOrderItems().stream().map(i -> i.getOrder()).collect(toList());

        assertAll(
                () -> assertNotNull(persisted),
                () -> assertThat(persisted.getOrderItems(), hasSize(2)),
                () -> assertThat(fOrders, hasSize(2)),
                () -> assertThat(fOrders, everyItem(is(order)))
        );
    }

}