package com.ynz.demo.springjpatransaction.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long orderId;

    private ZonedDateTime creationDateTime;

    @ManyToOne
    @JoinColumn(name = "FK_CUSTOMER")
    private Customer customer;

    @OneToMany(mappedBy = "order", targetEntity = OrderItem.class, cascade = CascadeType.PERSIST)
    private Set<OrderItem> orderItems = new HashSet<>();
}
