package com.ynz.demo.springjpatransaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerDto {
    private String firstName;
    private String lastName;
    private String email;
}
