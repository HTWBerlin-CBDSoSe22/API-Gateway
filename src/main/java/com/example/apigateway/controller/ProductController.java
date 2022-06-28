package com.example.apigateway.controller;

import com.example.apigateway.model.CurrencyExchangeDto;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductCreationDto;
import com.example.apigateway.model.Product.ProductDto;
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
    Product createProduct(@RequestParam String newCurrency, @RequestParam String currentCurrency, @RequestBody ProductCreationDto productToCreate){
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setOldCurrency(currentCurrency);
        currencyExchange.setNewCurrency(newCurrency);
        productToCreate.setId(null);
        Product createdProduct = this.productService.showProduct(productToCreate, currencyExchange);
        return createdProduct;
    }


}
