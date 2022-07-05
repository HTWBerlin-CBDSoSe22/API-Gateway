package com.example.apigateway.service;

import com.example.apigateway.model.Component;
import com.example.apigateway.model.CurrencyExchangeDto;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductCreationDto;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

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

    public List<ProductCreationDto> showAllProducts(){
        List<ProductCreationDto> listOfAllProducts;
        listOfAllProducts = rabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "getInformation",
                "showProducts",
                new ParameterizedTypeReference<>() {
                });
        return listOfAllProducts;
    }


    /**
     * create or get details of a product in database with associated price in specified currency
     * @param productToCreateOrShow has id = null when it has to be created
     */
    public Product showProduct(ProductCreationDto productToCreateOrShow, CurrencyExchangeDto currencyExchange) {
        Product productToReturn = new Product();
        productToReturn.setCurrency(currencyExchange.getOldCurrency());

        ListenableFuture<ProductCreationDto> listenableFutureFromProductMS = createOrShowProduct(productToCreateOrShow);
        ListenableFuture listenableFutureFromPriceMS = produceCalculationOfPriceRequest(productToCreateOrShow);
        ListenableFuture listenableFutureCurrency = produceExchangeRateRequest(currencyExchange);

        // try to read separately from queues and adept the returning product with information from service
        try {
            ProductCreationDto createdProductReceived = listenableFutureFromProductMS.get();
            productToReturn = modelMapper.map(createdProductReceived, Product.class);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        try {
            if(listenableFutureFromPriceMS != null) {
                String price = String.valueOf(listenableFutureFromPriceMS.get());
                productToReturn.setPrice(Float.parseFloat(price));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        try {
            String exchangeRate = String.valueOf(listenableFutureCurrency.get());
            productToReturn.setPrice(productToReturn.getPrice() * Float.parseFloat(exchangeRate));
            productToReturn.setCurrency(currencyExchange.getNewCurrency());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return productToReturn;
    }

    private ListenableFuture<ProductCreationDto> createOrShowProduct(ProductCreationDto productToCreate){
                return asyncRabbitTemplate.convertSendAndReceiveAsType(
                        directExchange.getName(),
                        "createProduct",
                        productToCreate,
                        new ParameterizedTypeReference<>() {
                        });
    }
    private ListenableFuture produceCalculationOfPriceRequest(ProductCreationDto productToCreateOrShow){
        if(productToCreateOrShow.getConsistsOf()==null){
            return null;
        }
        List<Float> pricesOfComponents = new ArrayList<>();
        for(Component c : productToCreateOrShow.getConsistsOf()){
            pricesOfComponents.add((float)c.getPrice());
        }
        return asyncRabbitTemplate.convertSendAndReceive(
                directExchange.getName(),
                "calculatePrice",
                pricesOfComponents);
    }
    private ListenableFuture produceExchangeRateRequest(CurrencyExchangeDto currencyExchange){
        return asyncRabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "getExchangeRate",
                currencyExchange,
                new ParameterizedTypeReference<>() {
                });
    }
}
