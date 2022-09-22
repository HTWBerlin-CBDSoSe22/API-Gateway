package com.example.apigateway.exception;

public class ProductAssemblyException extends Exception{
    public ProductAssemblyException(){
        super("Product couldn't be assembled");
    }
}
