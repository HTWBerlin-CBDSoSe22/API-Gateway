package com.example.apigateway.exception;

public class ProductServiceQueueException extends RuntimeException{
    public ProductServiceQueueException(String message){
        super(message);
    }
}
