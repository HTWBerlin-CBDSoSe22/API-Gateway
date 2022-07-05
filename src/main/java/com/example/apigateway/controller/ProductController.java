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
    List<ProductCreationDto> getAllProducts() {
        List<ProductCreationDto> listOfAllProducts = new ArrayList<>();
        listOfAllProducts = this.productService.showAllProducts();
        return  listOfAllProducts;
    }
    @GetMapping(path = "/{productId}")
    Product findSingleProductById(@RequestParam(defaultValue = "Euro") String newCurrency, @RequestParam(defaultValue = "Euro") String currentCurrency,@PathVariable("productId") long productId){
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setOldCurrency(currentCurrency);
        currencyExchange.setNewCurrency(newCurrency);
        ProductCreationDto productToGet = new ProductCreationDto();
        productToGet.setId(productId);
        Product createdProduct = this.productService.showProduct(productToGet, currencyExchange);
        return createdProduct;
    }
    @PostMapping
    Product createProduct(@RequestParam(defaultValue = "Euro") String newCurrency, @RequestParam(defaultValue = "Euro") String currentCurrency, @RequestBody ProductCreationDto productToCreate){
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setOldCurrency(currentCurrency);
        currencyExchange.setNewCurrency(newCurrency);
        // if id is null -> ProductMS creates Product
        productToCreate.setId(null);
        Product createdProduct = this.productService.showProduct(productToCreate, currencyExchange);
        return createdProduct;
    }


}
