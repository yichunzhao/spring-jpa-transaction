package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.entities.OrderItem;
import com.ynz.demo.springjpatransaction.util.AbstractTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        //order.setCreationDateTime(OffsetDateTime.now());
        customer.addOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("iphone8");

        order.addOderItem(orderItem);

        Order order1 = new Order();
        //order1.setCreationDateTime(OffsetDateTime.now());
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
        //order.setCreationDateTime(OffsetDateTime.now());
        customer.addOrder(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setContent("iphone8");

        order.addOderItem(orderItem);

        Order order1 = new Order();
        //order1.setCreationDateTime(OffsetDateTime.now());
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
        //order.setCreationDateTime(OffsetDateTime.now());

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
     * verify one to many, by default is a lazy loading. one to many default fetch type is lazy
     * this is not approved.
     */
    @Test
    @Disabled
    void givenCustomerOrder_OneToMany_CustomerReturnsEmptyOrders() {
        Customer customer = createDummyCustomer();
        Order order = createDummyOrder();
        customer.addOrder(order);

        Customer persisted = entityManager.persistAndFlush(customer);
        assertNotNull(persisted);
        Set<Order> persistedCustomerOrders = persisted.getOrders();
        assertThat(persistedCustomerOrders, hasSize(1));

        //lod customer via repository
        long id = customer.getId();
        //Optional<Customer> found = customerRepository.findById(id);
        Optional<Customer> found = customerRepository.findByEmail(customer.getEmail());
        assertTrue(found.isPresent());
        Set<Order> foundOrders = found.get().getOrders();
        assertThat(foundOrders, empty());
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
    void testDeleteCustomerByEmailAgainstDB() {
        //persist a dummy customer into database
        Customer customer = createDummyCustomer();
        entityManager.persistAndFlush(customer);

        customerRepository.deleteCustomerByEmailPSQL(customer.getEmail());
        entityManager.flush();

        //look up this customer from database again
        //Customer found = entityManager.find(Customer.class, customer.getId());

        Optional<Customer> found = customerRepository.findByEmail(customer.getEmail());

        //assert it being removed from db
        assertFalse(found.isPresent());
    }

    @Test
    @Disabled
    void testDeleteCustomerByEmailAgainstDBAnother_ItGeneratesSingleSQLQuery() {
        Customer customer = createDummyCustomer();
        customerRepository.deleteCustomerByEmailPSQL(customer.getEmail());
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

}
