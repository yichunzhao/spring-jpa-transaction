package com.ynz.demo.springjpatransaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(DuplicatedCustomerException.class)
    public ResponseEntity<ErrorMsgModel> handleDuplicatedCustomerException(DuplicatedCustomerException e) {
        ErrorMsgModel errorMsg = new ErrorMsgModel();
        errorMsg.setMessage(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }

    @ExceptionHandler(NoSuchCustomerException.class)
    public ResponseEntity<ErrorMsgModel> handleNoSuchCustomerException(NoSuchCustomerException e) {
        ErrorMsgModel errorMsg = new ErrorMsgModel();
        errorMsg.setMessage(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
    }

}
