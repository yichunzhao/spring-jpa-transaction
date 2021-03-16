package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;
import com.ynz.demo.springjpatransaction.util.AbstractTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class CustomerRepositoryTest extends AbstractTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Mike");
        customer.setLastName("Brown");
        customer.setEmail("yz@hotmail.com");

        Order order = new Order();
        customer.addOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("iphone8");

        order.addOderItem(orderItem);

        Order order1 = new Order();
        customer.addOrder(order1);

        OrderItem orderItem1 = new OrderItem();
        orderItem.setContent("cannon printer");
        order1.addOderItem(orderItem1);

        Customer persisted = customerRepository.save(customer);
        assertAll(
                () -> assertNotNull(persisted),
                () -> assertThat(persisted.getEmail(), is("yz@hotmail.com")),
                () -> assertThat(persisted.getOrders(), hasSize(2))
        );
    }

    @Test
    void testFindCustomerByEmail() {
        Customer customer = new Customer();
        customer.setFirstName("Mike");
        customer.setLastName("Brown");
        customer.setEmail("yz@hotmail.com");

        Order order = new Order();
        customer.addOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("iphone8");

        order.addOderItem(orderItem);

        Order order1 = new Order();
        customer.addOrder(order1);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setContent("cannon printer");
        order1.addOderItem(orderItem1);

        Customer persisted = entityManager.persistAndFlush(customer);
        String email = persisted.getEmail();

        Optional<Customer> found = customerRepository.findByEmail(email);

        assertAll(
                () -> assertTrue(found.isPresent()),
                () -> assertThat(found.get().getOrders(), hasSize(2)),
                () -> assertThat(found.get().getFirstName(), is("Mike")),
                () -> assertThat(found.get().getLastName(), is("Brown"))
        );
    }

    @Test
    void testPersistCustomerWithOrder() {
        Customer customer = createDummyCustomer();
        Order order = new Order();

        customer.addOrder(order);
        Customer persisted = customerRepository.save(customer);

        assertAll(
                () -> assertNotNull(persisted),
                () -> assertNotNull(persisted.getId()),
                () -> assertNotNull(persisted.getOrders()),
                () -> assertThat(persisted.getOrders(), hasSize(1))
        );
    }

    /**
     * OneToMany default fetch type = lazy; and ManyToOne default fetch type = lazy
     * <p>
     * when accessing orders of customer; it generates a query clause to select the orders of this customer.
     */
    @Test
    @Disabled
    void givenCustomerOrder_OneToMany_WhenAccessingCustomerOrderReturnsOrders() {
        log.info("approving lazy loading orders: ");
        Customer customer = createDummyCustomer();
        Order order = createDummyOrder();
        customer.addOrder(order);

        Customer persisted = entityManager.persistAndFlush(customer);
        assertNotNull(persisted);

        Set<Order> persistedCustomerOrders = persisted.getOrders();
        assertThat(persistedCustomerOrders, hasSize(1));

        entityManager.detach(customer);

        //lod customer via repository
        log.info("via repository find customer by its Email: it generates a select query ");
        Optional<Customer> found = customerRepository.findByEmail(customer.getEmail());

        assertTrue(found.isPresent());

        log.info("accessing order via found Customer: as accessing orders inside the customer");
        log.info("accessing order via found Customer: it generates another query to select orders");
        Set<Order> foundOrders = found.get().getOrders();
        assertThat(foundOrders, is(not(empty())));
        log.info("accessing order via found Customer: due to within the same transaction scope");
    }

    @Test
    @Disabled
    void givenCustomer_OneToMany_CustomerReturnsEmptyOrders() {
        log.info("approving lazy loading orders: ");
        Customer customer = createDummyCustomer();

        Customer persisted = entityManager.persistAndFlush(customer);
        assertNotNull(persisted);

        entityManager.detach(customer);

        //load customer via repository
        log.info("via repository find customer by its Email: ");
        Optional<Customer> found = customerRepository.findByEmail(customer.getEmail());

        assertTrue(found.isPresent());
    }

    @Test
    void usingDerivedDeleteQueryToDeleteCustomer_ItCausesExtraSQLQueries() {
        //persist a dummy customer into database
        Customer customer = createDummyCustomer();
        entityManager.persistAndFlush(customer);

        //using derived delete query to delete it from database.
        customerRepository.deleteByEmail(customer.getEmail());
        //sync. with underlying database.
        entityManager.flush();

        //the current session is still alive; detach the Customer entity;
        entityManager.detach(customer);
        //and then look up this customer from database again
        Customer found = entityManager.find(Customer.class, customer.getId());
        //assert it being removed from db
        assertNull(found);
    }

    @Test
    @DisplayName("delete directly against the database")
    void testDeleteCustomerByEmailAgainstDB() {
        log.info("test delete a customer against database directly");
        //persist a dummy customer into database
        Customer customer = createDummyCustomer();
        entityManager.persistAndFlush(customer);

        //delete directly against the database.
        customerRepository.deleteCustomerByEmailJPQL(customer.getEmail());

        //detach entity customer bean from the current persistent context
        entityManager.detach(customer);

        //look up this customer from database again
        Customer found = entityManager.find(Customer.class, customer.getId());

        //assert it being removed from db
        assertNull(found);
    }

    @Test
    @Disabled
    void testDeleteCustomerByEmailAgainstDBAnother_ItGeneratesSingleSQLQuery() {
        Customer customer = createDummyCustomer();
        customerRepository.deleteCustomerByEmailJPQL(customer.getEmail());
    }

    @Test
    @Disabled
    void testDerivedQuery_DeleteCustomerByEmail_ItGeneratesTwoSQLQueries() {
        Customer customer = createDummyCustomer();
        //here it gen. an insert query
        entityManager.persistAndFlush(customer);

        //entity manager change the managed bean, customer, to be removed state.
        //it generate a sql select and then delete two queries.
        customerRepository.deleteByEmail(customer.getEmail());
        //sync. with the underlying db, it generates a sql delete query.
        entityManager.flush();
    }

    @Test
    @DisplayName("persisting Date_Time with Zone")
    void whenCreatingOrderForCustomer_OrderContainsTimeStampWithZone() {
        log.info("test order creation timestamp");

        Customer customer = createDummyCustomer();
        Order order = createDummyOrder();

        customer.addOrder(order);
        log.info("entity manager persisting customer entity: ");
        entityManager.persistAndFlush(customer);

        log.info("entity manager detach customer entity: ");
        entityManager.detach(customer);

        log.info("entity manager find customer entity by its P-Key: ");
        Customer found = entityManager.find(Customer.class, customer.getId());

        List<Order> persistedOrders = new ArrayList<>(found.getOrders());
        Order persistedOrder = persistedOrders.get(0);
        OffsetDateTime creationTimeStamp = persistedOrder.getCreationDateTime();

        ZoneOffset localZoneOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

        assertAll(
                () -> assertThat(creationTimeStamp, is(notNullValue())),
                () -> assertThat(creationTimeStamp.getOffset(), is(localZoneOffset))
        );
    }

}
