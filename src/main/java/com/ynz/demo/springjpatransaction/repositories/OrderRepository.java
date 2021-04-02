package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.dto.OrderDto;
import com.ynz.demo.springjpatransaction.entities.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<OrderDto> findByCustomerEmail(String email);
}
