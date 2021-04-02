package com.ynz.demo.springjpatransaction.services;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.dto.OrderDto;
import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.exceptions.DuplicatedCustomerException;
import com.ynz.demo.springjpatransaction.exceptions.NoSuchCustomerException;
import com.ynz.demo.springjpatransaction.repositories.CustomerRepository;
import com.ynz.demo.springjpatransaction.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public Iterable<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public CustomerDto findCustomerByEmail(String email) {
        log.info("find a customer by email.");
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchCustomerException(new StringBuilder("Customer with email : ")
                        .append(email).append(" is not existed").toString()));
    }

    @Transactional
    public List<OrderDto> findCustomerOrderByEmail(String email) {
        log.info("find a customer's order by its email.");
        findCustomerByEmail(email);

        log.info("find orders by a customer email .......");
        List<OrderDto> orders = orderRepository.findByCustomerEmail(email);
        return orders;
    }

    public Customer createCustomer(Customer customer) {
        log.info("create a customer.");
        Customer persisted;

        try {
            findCustomerByEmail(customer.getEmail());
            throw new DuplicatedCustomerException(new StringBuilder("Customer: ").append(customer.getEmail())
                    .append(" already existed ").toString());

        } catch (NoSuchCustomerException e) {
            persisted = customerRepository.save(customer);
        }

        return persisted;
    }

    public Customer addCustomerOrder(String email, Order order) {
        log.info("add a customer an order.");
        CustomerDto customerDto = findCustomerByEmail(email);

        order.setCreationDateTime(OffsetDateTime.now());

        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setEmail(customerDto.getEmail());
        customer.addOrder(order);

        return customerRepository.save(customer);
    }

}
