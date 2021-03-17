package com.ynz.demo.springjpatransaction.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.OffsetDateTime;
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

    @NotEmpty(message = "Customer must have a first name.")
    @Column(nullable = false)
    private String firstName;

    @NotEmpty(message = "Customer must have a last name.")
    @Column(nullable = false)
    private String lastName;

    @NotEmpty(message = "Customer must provide an email!")
    @Email
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "customer", targetEntity = Order.class, cascade = {CascadeType.PERSIST})
    private Set<Order> orders = new HashSet<>();

    public void addOrder(@NonNull Order order) {
        orders.add(order);

        order.setCustomer(this);
        order.setCreationDateTime(OffsetDateTime.now());
    }

}
