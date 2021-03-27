package com.ynz.demo.springjpatransaction.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long orderId;

    @Column(name = "OffSet_Date_Time", columnDefinition = "TIME WITH TIME ZONE")
    private OffsetDateTime creationDateTime;

    @ManyToOne
    @JoinColumn(name = "FK_CUSTOMER")
    @JsonIgnore
    private Customer customer;

    @OneToMany(mappedBy = "order", targetEntity = OrderItem.class, cascade = {CascadeType.PERSIST, CascadeType.DETACH}, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    private UUID givenOrderId;

    public void addOderItem(@NonNull OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

}
