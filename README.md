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


@DataJpaTest and @WebMvcTest 

@DataJpaTest is designed for testing repositories, and @WebMvcTest is designed for testing controllers.

They are both designed to disable full auto-configured application context, and bootstrap a tailored application context good enough for a unit test combined with Mokito.
You need to watch out @DataJpaTest will by default auto-configure an embedded database, so if you test against a real database, then need to disable this default behaviour. 


Creating database schema

database schema can be created from entity definitions via the hibernate, using an external schemal.sql, or by the flyway.

We can control whether the schema.sql file should be executed with the property spring.datasource.initialization-mode. The default value is embedded, meaning it will only execute for an embedded database (i.e. in our tests). If we set it to always, it will always execute. 


