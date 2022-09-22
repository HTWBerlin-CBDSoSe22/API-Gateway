package com.example.apigateway.model;

public class ProductPrice {

    float price;

    public ProductPrice(float price) {
        this.price = price;
    }
    public ProductPrice(){}

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
