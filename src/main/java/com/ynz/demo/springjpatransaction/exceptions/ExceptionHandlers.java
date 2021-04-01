package com.ynz.demo.springjpatransaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(DuplicatedCustomerException.class)
    public ResponseEntity<ErrorMsgModel> handleDuplicatedCustomerException(RuntimeException e) {
        ErrorMsgModel errorMsg = new ErrorMsgModel();
        errorMsg.setMessage(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMsgModel> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
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

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ErrorMsgModel> handleSpringMessageException(HttpMessageNotWritableException e) {
        ErrorMsgModel errorMsg = new ErrorMsgModel();
        errorMsg.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
    }

}
