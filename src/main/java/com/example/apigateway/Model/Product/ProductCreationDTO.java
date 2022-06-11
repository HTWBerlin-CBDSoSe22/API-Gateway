package com.example.apigateway.Model.Product;

import com.example.apigateway.Model.Component;

import java.util.Set;

public class ProductCreationDTO {
    private String name;
    private Set<Component> consistsOf;

    public String getName() {
        return name;
    }

    public Set<Component> getConsistsOf() {
        return consistsOf;
    }

    public ProductCreationDTO(String name, Set<Component> consistsOf) {
        this.name = name;
        this.consistsOf = consistsOf;
    }
}
