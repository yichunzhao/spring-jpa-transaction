package com.ynz.demo.springjpatransaction.entities;

import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest extends AbstractTest {

    @Test
    void givenNullOrder_ThenCustomerAddOrderThrowNPE() {
        Customer customer = new Customer();
        Throwable exception = assertThrows(NullPointerException.class, () -> customer.addOrder(null));
        assertThat(exception.getMessage(), Matchers.containsStringIgnoringCase("order is marked non-null but is null"));
    }

    @Test
    void addCustomerOrder_ItGeneratesGivenOrderIdAutomatically() {
        Customer customer = new Customer();
        Order order = new Order();

        assertNull(order.getGivenOrderId());

        customer.addOrder(order);
        assertNotNull(order.getGivenOrderId());
        assertThat(order.getGivenOrderId(), Matchers.instanceOf(UUID.class));
    }

}