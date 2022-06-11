package com.example.apigateway.controller;

import com.example.apigateway.Model.Product.Product;
import com.example.apigateway.Model.Product.ProductDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    List<ProductDTO> getAllProducts() {
        List<ProductDTO> listOfAllProducts = new ArrayList<>();
        return  listOfAllProducts;
    }

    @GetMapping(path = "/{productId}")
    void findSingleProductById(@PathVariable("productId") long productId){
        //ProductDTO singleProduct = ...;
    }

    @PostMapping
    void createProduct(){
    }
}
