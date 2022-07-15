package com.example.apigateway.exception;

public class ProductNotFoundException extends Exception{
    public ProductNotFoundException(){super("Product was not found");}
}
