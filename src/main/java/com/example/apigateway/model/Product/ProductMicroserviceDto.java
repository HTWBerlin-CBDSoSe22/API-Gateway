package com.example.apigateway.model.Product;

import com.example.apigateway.model.Component;

import java.util.Set;

public class ProductMicroserviceDto {
    private Long productId = null;
    private String name;
    private Set<Component> consistsOf;

    public String getName() {
        return name;
    }

    public Set<Component> getConsistsOf() {
        return consistsOf;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConsistsOf(Set<Component> consistsOf) {
        this.consistsOf = consistsOf;
    }

    public ProductMicroserviceDto() {
    }
}
