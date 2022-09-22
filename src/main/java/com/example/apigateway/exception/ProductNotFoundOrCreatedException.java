package com.example.apigateway.exception;

public class ProductNotFoundOrCreatedException extends Exception{
    public ProductNotFoundOrCreatedException(){super("Product was not found");}
}
