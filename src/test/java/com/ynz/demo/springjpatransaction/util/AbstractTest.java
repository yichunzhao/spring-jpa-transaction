package com.ynz.demo.springjpatransaction.util;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;

public abstract class AbstractTest {

    protected Order createDummyOrder() {
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

    protected Customer createDummyCustomer() {
        Customer customer = new Customer();
        customer.setEmail("mb@hotmail.com");
        customer.setFirstName("Mike");
        customer.setLastName("Brown");
        return customer;
    }

}
