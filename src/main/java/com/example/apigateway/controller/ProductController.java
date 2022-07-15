package com.example.apigateway.controller;

import com.example.apigateway.exception.ProductNotFoundException;
import com.example.apigateway.model.CurrencyExchangeDto;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductCreationDto;
import com.example.apigateway.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
@ResponseBody
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    List<ProductCreationDto> showAllProducts() {
        List<ProductCreationDto> listOfAllProducts;
        listOfAllProducts = this.productService.showAllProducts();
        return  listOfAllProducts;
    }
    @GetMapping(path = "/{productId}")
    Product showSingleProductById(@RequestParam(defaultValue = "Euro") String newCurrency, @RequestParam(defaultValue = "Euro") String currentCurrency, @PathVariable("productId") long productId){
        CurrencyExchangeDto currencyExchange = convertCurrencies(newCurrency, currentCurrency);
        ProductCreationDto productToGet = new ProductCreationDto();
        productToGet.setId(productId);
        Product detailedProduct;
        try {
            detailedProduct = this.productService.showSingleProductInDetail(productToGet, currencyExchange);
        } catch (ProductNotFoundException e) {
            throw new RuntimeException(e);
        }
        return detailedProduct;
    }
    @PostMapping
    Product createProduct(@RequestParam(defaultValue = "Euro") String newCurrency, @RequestParam(defaultValue = "Euro") String oldCurrency, @RequestBody ProductCreationDto productToCreate){
        if(!productHasComponents(productToCreate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        CurrencyExchangeDto currencyExchange = convertCurrencies(newCurrency, oldCurrency);
        productToCreate.setId(null);
        Product createdProduct = this.productService.showOrCreateProduct(productToCreate, currencyExchange);
        return createdProduct;
    }

    public boolean productHasComponents(ProductCreationDto productToCreate){
        if(productToCreate.getConsistsOf().isEmpty())
            return false;
        return true;
    }

    public CurrencyExchangeDto convertCurrencies(String newCurrency, String oldCurrency){
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setOldCurrency(oldCurrency);
        currencyExchange.setNewCurrency(newCurrency);
        return currencyExchange;
    }


}
