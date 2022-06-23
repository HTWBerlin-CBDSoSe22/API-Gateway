package com.example.apigateway.service;

import com.example.apigateway.Model.Component;
import com.example.apigateway.Model.CurrencyExchangeDto;
import com.example.apigateway.Model.Product.Product;
import com.example.apigateway.Model.Product.ProductCreationDto;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {
    private final RabbitTemplate rabbitTemplate;
    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final DirectExchange directExchange;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(RabbitTemplate rabbitTemplate, DirectExchange directExchange, ModelMapper modelMapper, AsyncRabbitTemplate asyncRabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.directExchange = directExchange;
        this.modelMapper = modelMapper;
        this.asyncRabbitTemplate = asyncRabbitTemplate;
    }

    public Product showFullProduct(ProductCreationDto productToCreate){
        Product createdProduct = new Product();
        ProductCreationDto createdProductFromProductMS = rabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "createProduct",
                productToCreate,
                new ParameterizedTypeReference<>() {
                });

        createdProduct = modelMapper.map(createdProductFromProductMS, Product.class);
        return createdProduct;
    }

    public Product createProduct(ProductCreationDto productToCreate, CurrencyExchangeDto currencyExchange) {
        Product createdProduct = new Product();

        ListenableFuture<ProductCreationDto> listenableFuture =
                asyncRabbitTemplate.convertSendAndReceiveAsType(
                        directExchange.getName(),
                        "createProduct",
                        productToCreate,
                        new ParameterizedTypeReference<>() {
                        });

        List componentPrices = new ArrayList();
        for(Component c : productToCreate.getConsistsOf()){
            componentPrices.add(c.getPrice());
        }
        ListenableFuture listenableFuturePrice =
                asyncRabbitTemplate.convertSendAndReceive(
                        directExchange.getName(),
                        "calculatePrice",
                        componentPrices);

        // do some other work...

        try {
            ProductCreationDto createdProductReceived = listenableFuture.get();
            String price = String.valueOf(listenableFuturePrice.get());
            createdProduct = modelMapper.map(createdProductReceived, Product.class);
            createdProduct.setPrice(Float.parseFloat(price));
            return createdProduct;
        } catch (InterruptedException | ExecutionException e) {
            return createdProduct;
        }

    }
}
