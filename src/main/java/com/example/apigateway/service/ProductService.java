package com.example.apigateway.service;

import com.example.apigateway.exception.ProductAssemblyException;
import com.example.apigateway.exception.ProductNotFoundException;
import com.example.apigateway.model.Component;
import com.example.apigateway.model.CurrencyExchangeDto;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductMicroserviceDto;
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

    public List<ProductMicroserviceDto> showAllProducts(){
        List<ProductMicroserviceDto> listOfAllProducts;
        listOfAllProducts = rabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "getInformation",
                "showProducts",
                new ParameterizedTypeReference<>() {
                });
        return listOfAllProducts;
    }
    public Product createProduct(ProductMicroserviceDto productToCreate, CurrencyExchangeDto currencyExchange) {
        Product productToShow = new Product();
        Future<ProductMicroserviceDto> productMicroserviceDtoFromService = getProductFromService(productToCreate);
        Future<Float> priceOfProduct = getPriceFromService(productToCreate);
        Future<Float> exchangeRate = getExchangeRateFromService(currencyExchange);
        while(true){
            if(servicesAreDone(productMicroserviceDtoFromService, priceOfProduct, exchangeRate)) {
                try {
                    productToShow = assembleProduct(productMicroserviceDtoFromService, priceOfProduct, exchangeRate);
                }catch (ProductAssemblyException e){
                }
                break;
            }
        }
        return productToShow;
    }
    public Product showSingleProductInDetail(ProductMicroserviceDto productToCreateOrShow, CurrencyExchangeDto currencyExchange) throws ProductNotFoundException {
        Product productToShow = new Product();
        ProductMicroserviceDto productMicroserviceDtoFromService;
        Future<ProductMicroserviceDto> futureProductCreationDtoFromService = getProductFromService(productToCreateOrShow);
        Future<Float> exchangeRate = getExchangeRateFromService(currencyExchange);
        productMicroserviceDtoFromService = getProductData(futureProductCreationDtoFromService);
        Future<Float> priceOfProduct = getPriceFromService(productMicroserviceDtoFromService);
        productToShow = waitForPriceOfProduct(productToShow, futureProductCreationDtoFromService, priceOfProduct, exchangeRate);
        return productToShow;
    }
    private Product assembleProduct(Future<ProductMicroserviceDto> productToReturn, Future<Float> priceOfProduct, Future<Float> exchangeRate) throws ProductAssemblyException {
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
    public Future<Float> getPriceFromService(ProductMicroserviceDto productToCreateOrShow) {
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
    public Future<ProductMicroserviceDto> getProductFromService(ProductMicroserviceDto productToCreateOrShow) {
        ListenableFuture<ProductMicroserviceDto> listenableFutureProductCreationDto = produceShowOrCreateProduct(productToCreateOrShow);
        try {
            ProductMicroserviceDto createdProductReceived = listenableFutureProductCreationDto.get();
            return new AsyncResult<>(createdProductReceived);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            productToCreateOrShow.setId(null);
            return new AsyncResult<>(productToCreateOrShow);
        }
    }
    private ListenableFuture<ProductMicroserviceDto> produceShowOrCreateProduct(ProductMicroserviceDto productToShowOrCreate){
                return asyncRabbitTemplate.convertSendAndReceiveAsType(
                        directExchange.getName(),
                        "createProduct",
                        productToShowOrCreate,
                        new ParameterizedTypeReference<>() {
                        });
    }
    private ListenableFuture produceCalculationOfPrice(ProductMicroserviceDto productToShowOrCreate){
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
    private List<Float> getPricesOfComponentsInProduct(ProductMicroserviceDto productToShowOrCreate){
        List pricesOfComponents = new ArrayList();
        Set<Component> componentsOfProduct = productToShowOrCreate.getConsistsOf();
        if(componentsOfProduct != null) {
            for (Component c : componentsOfProduct) {
                pricesOfComponents.add((float) c.getPrice());
            }
        }
        return pricesOfComponents;
    }
    private boolean servicesAreDone(Future<ProductMicroserviceDto> productCreationDtoFromService, Future<Float> priceOfProduct, Future<Float> exchangeRate){
        if(productCreationDtoFromService.isDone() && priceOfProduct.isDone() && exchangeRate.isDone())
            return true;
        return false;
    }
    private ProductMicroserviceDto getProductData(Future<ProductMicroserviceDto> futureProductCreationDtoFromService) throws ProductNotFoundException {
        while(true) {
            if (futureProductCreationDtoFromService.isDone()) {
                break;
            }
        }
        try {
            return futureProductCreationDtoFromService.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ProductNotFoundException();
        }
    }
    private Product waitForPriceOfProduct(Product productToShow, Future<ProductMicroserviceDto> futureProductCreationDtoFromService, Future<Float> priceOfProduct, Future<Float> exchangeRate){
        while(true){
            if(priceOfProduct.isDone() && exchangeRate.isDone()) {
                try {
                    productToShow = assembleProduct(futureProductCreationDtoFromService, priceOfProduct, exchangeRate);
                }catch (ProductAssemblyException e){
                    e.printStackTrace();
                }
                break;
            }
        }
        return productToShow;
    }
}
