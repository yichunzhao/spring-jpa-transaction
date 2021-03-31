package com.ynz.demo.springjpatransaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CustomerDto {
    @NotBlank(message = "customer must have a first name.")
    private String firstName;

    @NotBlank(message = "customer must have a last name.")
    private String lastName;

    @Email(message = "email address is not valid.")
    private String email;
}
