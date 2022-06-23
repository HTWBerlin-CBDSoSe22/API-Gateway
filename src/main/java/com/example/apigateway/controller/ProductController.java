package com.example.apigateway.controller;

import com.example.apigateway.Model.Product.Product;
import com.example.apigateway.Model.Product.ProductCreationDto;
import com.example.apigateway.Model.Product.ProductDto;
import com.example.apigateway.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    List<ProductDto> getAllProducts() {
        List<ProductDto> listOfAllProducts = new ArrayList<>();
        return  listOfAllProducts;
    }
    @GetMapping(path = "/{productId}")
    void findSingleProductById(@PathVariable("productId") long productId){
        //ProductDTO singleProduct = ...;
    }
    @PostMapping
    Product createProduct(@RequestParam String currency, @RequestBody ProductCreationDto productToCreate){
        Product createdProduct = this.productService.createProduct(productToCreate);
        System.out.println("created product");
        return createdProduct;
    }


}
