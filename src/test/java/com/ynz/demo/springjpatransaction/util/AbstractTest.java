package com.ynz.demo.springjpatransaction.util;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.dto.OrderDto;
import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;

import java.time.OffsetDateTime;
import java.util.UUID;

public abstract class AbstractTest {
    protected static final String UUID_1 = "a782f100-93d1-11eb-a8b3-0242ac130003";

    protected Order createDummyOrder() {
        Order order = new Order();

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

    protected CustomerDto createDummyCustomerDto() {
        return new CustomerDto("John", "Smith", "js@hotmail.com");
    }

    protected OrderDto createDummyOrderDto(){
        return OrderDto.builder().givenOrderId(UUID.fromString(UUID_1))
                .creationDateTime(OffsetDateTime.now()).build();
    }


}
