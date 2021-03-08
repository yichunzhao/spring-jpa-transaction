package com.ynz.demo.springjpatransaction.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull(message = "Customer must have a first name.")
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "Customer must have a last name.")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Customer must provide an email!")
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "customer", targetEntity = Order.class, cascade = {CascadeType.PERSIST})
    private Set<Order> orders = new HashSet<>();

    public void addOrder(@NotNull Order order) {
        orders.add(order);
        order.setCustomer(this);
    }

}
