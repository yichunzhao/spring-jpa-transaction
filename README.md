# spring-jpa-transaction

What are the benefits to create bilateral relationships? 

You may easily create join on both sides, fx: a customer has many orders, then you may retrieve the corresponding customer directly on the order entity by a derived query.
`List<Order> findByCustomerEmail(String email);`  
It generates the following SQL query clause. 


````

Hibernate: 
    select
        order0_.order_id as order_id1_1_,
        order0_.fk_customer as fk_custo2_1_ 
    from
        public.order order0_ 
    left outer join
        public.customer customer1_ 
            on order0_.fk_customer=customer1_.id 
    where
        customer1_.email=?
        
````        

What are the benefits to use Set instead of List in the Entity?

Using a Set may improve database performance.  when you insert a new element into a list, Hibernate will remove all elements from the database first, and then re-populate all elements including the newly added element. However, as you using a Set, it only causes an insertion operation for the new element. 
