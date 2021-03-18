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

Using Derived delte query, generating extra SQL queries.

Using a derived-delete query: 'void deleteByEmail(String email)' generates extra SQL queries, for the Hibernate have to load the entity from the database into persistence context and make it become a managed bean. Afterwards, the entity manager may set the managed bean's life-cycle to become a 'removed' state. When the current transaction is committed or the current persistence context is flushed, the entity bean is synchronised with the counterparty of the database.

````
Hibernate: 
    select
        customer0_.id as id1_0_,
        customer0_.email as email2_0_,
        customer0_.first_name as first_na3_0_,
        customer0_.last_name as last_nam4_0_ 
    from
        public.customer customer0_ 
    where
        customer0_.email=?
Hibernate: 
    delete 
    from
        public.customer 
    where
        id=?

````

So, the optimal way to delete is to delete against database directly. Using native or JPQL may directly operate on the database.

````
    @Modifying
    @Query("delete from Customer c where c.email =:email")
    void deleteCustomerByEmailJPQL(@Param("email") String email);

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


The FetchType method defines two strategies for fetching data from the database:

FetchType.EAGER: The persistence provider must load the related annotated field or property. This is the default behavior for @Basic, @ManyToOne, and @OneToOne annotated fields.
FetchType.LAZY: The persistence provider should load data when it's first accessed, but can be loaded eagerly. This is the default behavior for @OneToMany, @ManyToMany and @ElementCollection-annotated fields.

Mapping Java 8 DateTime To Database

Java 8 has introduced the java.time packages, and the JDBC 4.2 API added support for the additional SQL types TIMESTAMP WITH TIME ZONE and TIME WITH TIME ZONE.

We can now map the JDBC Types TIME, DATE, and TIMESTAMP to the java.time types – LocalTime, LocalDate, and LocalDateTime:
````
@Column(name = "local_time", columnDefinition = "TIME")
private LocalTime localTime;

@Column(name = "local_date", columnDefinition = "DATE")
private LocalDate localDate;

@Column(name = "local_date_time", columnDefinition = "TIMESTAMP")
private LocalDateTime localDateTime;
````
Additionally, we have support for the offset local timezone to UTC through the OffsetTime and OffsetDateTime classes:
````
@Column(name = "offset_time", columnDefinition = "TIME WITH TIME ZONE")
private OffsetTime offsetTime;

@Column(name = "offset_date_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
private OffsetDateTime offsetDateTime;
````

Lombok null-check @NonNull

The null-check looks like if (param == null) throw new NullPointerException("param is marked @NonNull but is null"); and will be inserted at the very top of your method. For constructors, the null-check will be inserted immediately following any explicit this() or super() calls.

If a null-check is already present at the top, no additional null-check will be generated. 

When lombok generates a null-check if statement, by default, a java.lang.NullPointerException will be thrown with 'field name is marked non-null but is null' as the exception message.

Open session in View

To better understand the role of Open Session in View (OSIV), let's suppose we have an incoming request:

Spring opens a new Hibernate Session at the beginning of the request. These Sessions are not necessarily connected to the database.
Every time the application needs a Session, it will reuse the already existing one.
At the end of the request, the same interceptor closes that Session.

By default, OSIV is active in Spring Boot applications. Despite that, as of Spring Boot 2.0, it warns us of the fact that it's enabled at application startup if we haven't configured it explicitly:
````
2021-03-17 16:28:34.971  WARN 16756 --- [  restartedMain] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning

````
Explicitly configure 
spring.jpa.open-in-view to disable this warning
Anyway, we can disable the OSIV by using the spring.jpa.open-in-view configuration property:

spring.jpa.open-in-view=false

Unfortunately, exhausting the connection pool is not the only OSIV-related performance issue.

Since the Session is open for the entire request lifecycle, some property navigations may trigger a few more unwanted queries outside of the transactional context. It's even possible to end up with n+1 select problem, and the worst news is that we may not notice this until production.

If we're developing a simple CRUD service, it might make sense to use the OSIV, as we may never encounter those performance issues.

On the other hand, if we find ourselves calling a lot of remote services or there is so much going on outside of our transactional contexts, it's highly recommended to disable the OSIV altogether. 


JPA default fetch type

to-many default fetch type = lazy; one-one default fetch type = eager; 

