package com.example.apigateway.model.Product;

public class ProductDto {
    private Long productId;
    private String name;
    private float price;

    public ProductDto(Long productId, String name, float price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public float getPrice() {
        return price;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
