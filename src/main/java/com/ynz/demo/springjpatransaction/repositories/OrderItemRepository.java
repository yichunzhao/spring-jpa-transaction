package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.entities.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
}
