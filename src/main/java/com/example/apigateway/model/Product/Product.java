package com.example.apigateway.model.Product;

import com.example.apigateway.model.Component;

import java.util.Set;

public class Product {

    public void setName(String name) {
        this.name = name;
    }

    public void setConsistsOf(Set<Component> consistsOf) {
        this.consistsOf = consistsOf;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private String name;
    private Set<Component> consistsOf;

    private Long productId;
    private float price;
    private String currency;

    public float getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
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

    public Product() {
    }


}
