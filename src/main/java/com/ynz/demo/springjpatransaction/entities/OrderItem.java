package com.ynz.demo.springjpatransaction.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

@Entity
@Table(schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long orderItemId;

    @NotEmpty(message = "Content should not be empty")
    private String content;

    @PositiveOrZero(message = " Count of order items should be positive.")
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "FK_ORDER")
    @JsonIgnore
    private Order order;
}
