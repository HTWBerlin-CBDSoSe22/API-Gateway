package com.example.apigateway.Model.Product;

import com.example.apigateway.Model.Component;

import java.util.HashSet;
import java.util.Set;

public class Product {

    private Long productId;
    private Set<Component> consistsOf;
    private String name;
    private float price;
    private String currency;

    public float getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public Product(Set<Component> consistsOf, String name, float price, String currency) {
        this.consistsOf = consistsOf;
        this.name = name;
        this.price = price;
        this.currency = currency;
    }

    public Set<Component> getConsistsOf() {
        return consistsOf;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public float getPriceAfterExchange(float exchangeRate){
        return this.price * exchangeRate;
    }
}
