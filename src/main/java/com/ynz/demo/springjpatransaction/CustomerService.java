package com.ynz.demo.springjpatransaction;

import com.ynz.demo.springjpatransaction.entities.Customer;
import com.ynz.demo.springjpatransaction.entities.Order;
import com.ynz.demo.springjpatransaction.exceptions.DuplicatedCustomerException;
import com.ynz.demo.springjpatransaction.exceptions.NoSuchCustomerException;
import com.ynz.demo.springjpatransaction.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Iterable<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomerByEmail(String email) {
        log.info("find a customer by email.");
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchCustomerException(new StringBuilder("Customer with email : ")
                        .append(email).append(" is not existed").toString()));
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
        Customer customer = findCustomerByEmail(email);
        //order.setCreationDateTime(OffsetDateTime.now());
        customer.addOrder(order);

        return customerRepository.save(customer);
    }

}
