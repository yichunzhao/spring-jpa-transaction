package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
