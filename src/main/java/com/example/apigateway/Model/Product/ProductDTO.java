package com.example.apigateway.Model.Product;
import com.example.apigateway.Model.Component;
public class ProductDTO {
    private Long productId;
    private String name;
    private float price;

    public ProductDTO(Long productId, String name, float price) {
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

}
