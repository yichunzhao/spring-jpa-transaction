package com.ynz.demo.springjpatransaction;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class SpringJpaTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaTransactionApplication.class, args);
    }

}
