package com.ynz.demo.springjpatransaction.repositories;

import com.ynz.demo.springjpatransaction.dto.CustomerDto;
import com.ynz.demo.springjpatransaction.entities.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    /**
     * class-based dto projection.
     *
     * @param email customer email
     * @return CustomerDto instance
     */
    Optional<CustomerDto> findByEmail(String email);

    /**
     * Dynamic projection
     *
     * @param email String email address as method input argument
     * @param type  Projection target class type
     * @param <T>   Type parameter of projection target type
     * @return T type projection class instance
     */
    <T> T findByEmail(String email, Class<T> type);

    @Query("select c from Customer c where c.email =:email")
    Optional<Customer> findCustomerByEmailJPQL(@Param("email") String email);

    void deleteByEmail(String email);

    @Modifying
    @Query("delete from Customer c where c.email =:email")
    void deleteCustomerByEmailJPQL(@Param("email") String email);

}
