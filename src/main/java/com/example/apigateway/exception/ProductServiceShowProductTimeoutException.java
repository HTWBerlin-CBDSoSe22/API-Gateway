package com.example.apigateway.exception;

public class ProductServiceShowProductTimeoutException extends RuntimeException{
    public ProductServiceShowProductTimeoutException(){
            super("Product Microservice didn't answer");
    }
}
