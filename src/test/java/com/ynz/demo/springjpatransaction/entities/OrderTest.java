package com.ynz.demo.springjpatransaction.entities;

import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest extends AbstractTest {

    @Test
    void givenOrderItemNull_AddOrderItemThrowsNPE() {
        Order order = createDummyOrder();
        Throwable exception = assertThrows(NullPointerException.class, () -> order.addOderItem(null));
        assertThat(exception.getMessage(), containsString("item is marked non-null but is null"));
    }
}
