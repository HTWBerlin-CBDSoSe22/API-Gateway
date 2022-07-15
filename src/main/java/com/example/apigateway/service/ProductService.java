package com.example.apigateway.service;

import com.example.apigateway.exception.ProductAssemblyException;
import com.example.apigateway.exception.ProductNotFoundException;
import com.example.apigateway.model.Component;
import com.example.apigateway.model.CurrencyExchangeDto;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductCreationDto;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class ProductService {
    private final RabbitTemplate rabbitTemplate;
    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final DirectExchange directExchange;
    private final ModelMapper productMapper;

    @Autowired
    public ProductService(RabbitTemplate rabbitTemplate, DirectExchange directExchange, ModelMapper productMapper, AsyncRabbitTemplate asyncRabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.directExchange = directExchange;
        this.productMapper = productMapper;
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
     * create or get detailed product from database with associated price in specified currency
     * @param productToCreateOrShow has id = null when it has to be created
     */
    public Product showOrCreateProduct(ProductCreationDto productToCreateOrShow, CurrencyExchangeDto currencyExchange) {
        Product productToShow = new Product();
        Future<ProductCreationDto> productCreationDtoFromService = getProductFromService(productToCreateOrShow);
        Future<Float> priceOfProduct = getPriceFromService(productToCreateOrShow);
        Future<Float> exchangeRate = getExchangeRateFromService(currencyExchange);
        while(true){
            if(productCreationDtoFromService.isDone() && priceOfProduct.isDone() && exchangeRate.isDone()) {
                try {
                    productToShow = assembleProduct(productCreationDtoFromService, priceOfProduct, exchangeRate);
                }catch (ProductAssemblyException e){
                }
                break;
            }
        }
        return productToShow;
    }
    public Product showSingleProductInDetail(ProductCreationDto productToCreateOrShow, CurrencyExchangeDto currencyExchange) throws ProductNotFoundException {
        Product productToShow = new Product();
        Future<ProductCreationDto> futureProductCreationDtoFromService = getProductFromService(productToCreateOrShow);
        Future<Float> exchangeRate = getExchangeRateFromService(currencyExchange);
        // in neue methode ?
        while(true) {
            if (futureProductCreationDtoFromService.isDone()) {
                break;
            }
        }
        ProductCreationDto productCreationDtoFromService;
        try {
            productCreationDtoFromService = futureProductCreationDtoFromService.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ProductNotFoundException();
        }
        Future<Float> priceOfProduct = getPriceFromService(productCreationDtoFromService);
        while(true){
            if(priceOfProduct.isDone() && exchangeRate.isDone()) {
                try {
                    productToShow = assembleProduct(futureProductCreationDtoFromService, priceOfProduct, exchangeRate);
                }catch (ProductAssemblyException e){
                }
                break;
            }
        }
        return productToShow;
    }
    private Product assembleProduct(Future<ProductCreationDto> productToReturn, Future<Float> priceOfProduct, Future<Float> exchangeRate) throws ProductAssemblyException {
        Product assembledProduct;
        try {
            assembledProduct = productMapper.map(productToReturn.get(), Product.class);
            assembledProduct.setPrice(priceOfProduct.get() * exchangeRate.get());
        } catch (InterruptedException | ExecutionException e ) {
            throw new ProductAssemblyException();
        }
        return assembledProduct;
    }
    @Async
    public Future<Float> getPriceFromService(ProductCreationDto productToCreateOrShow) {
        ListenableFuture listenableFuturePrice = produceCalculationOfPrice(productToCreateOrShow);
        try {
            if(listenableFuturePrice != null) {
                String price = String.valueOf(listenableFuturePrice.get());
                return new AsyncResult<>(Float.parseFloat(price));
            }
            return new AsyncResult<>(-1f);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new AsyncResult<>(-1f);
        }
    }
    @Async
    public Future<Float> getExchangeRateFromService(CurrencyExchangeDto currencyExchange){
        ListenableFuture listenableFutureCurrency = produceExchangeRate(currencyExchange);
        try {
            String exchangeRate = String.valueOf(listenableFutureCurrency.get());
            return new AsyncResult<>(Float.parseFloat(exchangeRate));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new AsyncResult<>(-1f);
        }
    }
    @Async
    public Future<ProductCreationDto> getProductFromService(ProductCreationDto productToCreateOrShow) {
        ListenableFuture<ProductCreationDto> listenableFutureProductCreationDto = produceShowOrCreateProduct(productToCreateOrShow);
        try {
            ProductCreationDto createdProductReceived = listenableFutureProductCreationDto.get();
            return new AsyncResult<>(createdProductReceived);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            productToCreateOrShow.setId(null);
            return new AsyncResult<>(productToCreateOrShow);
        }
    }
    private ListenableFuture<ProductCreationDto> produceShowOrCreateProduct(ProductCreationDto productToShowOrCreate){
                return asyncRabbitTemplate.convertSendAndReceiveAsType(
                        directExchange.getName(),
                        "createProduct",
                        productToShowOrCreate,
                        new ParameterizedTypeReference<>() {
                        });
    }
    private ListenableFuture produceCalculationOfPrice(ProductCreationDto productToShowOrCreate){
        List<Float> pricesOfComponents = getPricesOfComponentsInProduct(productToShowOrCreate);
        return asyncRabbitTemplate.convertSendAndReceive(
                directExchange.getName(),
                "calculatePrice",
                pricesOfComponents);
    }
    private ListenableFuture produceExchangeRate(CurrencyExchangeDto currencyExchange){
        return asyncRabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "getExchangeRate",
                currencyExchange,
                new ParameterizedTypeReference<>() {
                });
    }
    private List<Float> getPricesOfComponentsInProduct(ProductCreationDto productToShowOrCreate){
        List pricesOfComponents = new ArrayList();
        Set<Component> componentsOfProduct = productToShowOrCreate.getConsistsOf();
        for(Component c : componentsOfProduct){
            pricesOfComponents.add((float)c.getPrice());
        }
        return pricesOfComponents;
    }

}
