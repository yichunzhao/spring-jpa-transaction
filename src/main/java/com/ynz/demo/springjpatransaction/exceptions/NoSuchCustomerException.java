package com.ynz.demo.springjpatransaction.exceptions;

public class NoSuchCustomerException extends RuntimeException {

    public NoSuchCustomerException(String message) {
        super(message);
    }
}
