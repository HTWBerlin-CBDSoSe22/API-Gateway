package com.example.apigateway.controller;

import com.example.apigateway.exception.ProductNotFoundOrCreatedException;
import com.example.apigateway.model.CurrencyExchangeDto;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductMicroserviceDto;
import com.example.apigateway.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/products")
@ResponseBody
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    List<ProductMicroserviceDto> showAllProducts() {
        List<ProductMicroserviceDto> listOfAllProducts;
        listOfAllProducts = this.productService.showAllProducts();

        return  listOfAllProducts;
    }

    @GetMapping(path = "/{productId}")
    Product showSingleProductById(@RequestParam(defaultValue = "EUR") String newCurrency, @RequestParam(defaultValue = "EUR") String currentCurrency, @PathVariable("productId") long productId){
        CurrencyExchangeDto currencyExchange = convertCurrenciesToEchangeDto(newCurrency, currentCurrency);
        ProductMicroserviceDto productToGet = new ProductMicroserviceDto();
        productToGet.setProductId(productId);
        Product detailedProduct;
        try {
            detailedProduct = this.productService.showSingleProductInDetail(productToGet, currencyExchange);
        } catch (ProductNotFoundOrCreatedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return detailedProduct;
    }

    @PostMapping
    Product createProduct(@RequestParam(defaultValue = "EUR") String newCurrency, @RequestParam(defaultValue = "EUR") String oldCurrency, @RequestBody ProductMicroserviceDto productToCreate){
        if(!productHasComponents(productToCreate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        CurrencyExchangeDto currencyExchange = convertCurrenciesToEchangeDto(newCurrency, oldCurrency);
        productToCreate.setProductId(null);

        try {
            Product createdProduct = this.productService.createProduct(productToCreate, currencyExchange);
            return createdProduct;
        }catch(ProductNotFoundOrCreatedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public boolean productHasComponents(ProductMicroserviceDto productToCreate){
        return !productToCreate.getConsistsOf().isEmpty();
    }

    public CurrencyExchangeDto convertCurrenciesToEchangeDto(String newCurrency, String oldCurrency){
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setOldCurrency(oldCurrency);
        currencyExchange.setNewCurrency(newCurrency);

        return currencyExchange;
    }
}
