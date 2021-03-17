package com.ynz.demo.springjpatransaction.entities;

import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest extends AbstractTest {

    @Test
    void givenNullOrder_ThenCustomerAddOrderThrowNPE() {
        Customer customer = new Customer();
        Throwable exception = assertThrows(NullPointerException.class, () -> customer.addOrder(null));
        assertThat(exception.getMessage(), Matchers.containsStringIgnoringCase("order is marked non-null but is null"));
    }

}