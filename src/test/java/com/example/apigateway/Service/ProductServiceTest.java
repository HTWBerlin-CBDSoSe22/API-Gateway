package com.example.apigateway.Service;

import com.example.apigateway.exception.ProductAssemblyException;
import com.example.apigateway.model.CurrencyExchangeDto;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductMicroserviceDto;
import com.example.apigateway.amqp.Config;
import com.example.apigateway.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Mockito.when;

public class ProductServiceTest {
    private ProductService productService;
    private RabbitTemplate rabbitTemplateMock;
    private AsyncRabbitTemplate asyncRabbitTemplateMock;
    @Autowired
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private Config config = new Config();
    private final Future<Float> mockedFuturePriceOfProduct = Mockito.mock(Future.class);
    private final Future<ProductMicroserviceDto> mockedFutureProductMicroserviceDto = Mockito.mock(Future.class);
    private final Future<Float> mockedFutureExchangeRate = Mockito.mock(Future.class);

    @Before
    public void setUp() {
        this.rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
        this.asyncRabbitTemplateMock = Mockito.mock(AsyncRabbitTemplate.class);
        this.productService = new ProductService(this.rabbitTemplateMock, config.directExchange(),modelMapper, asyncRabbitTemplateMock);
        this.modelMapper = new ModelMapper();
    }

    @Test
    public void testAssembleProductProduct() throws ExecutionException, InterruptedException, ProductAssemblyException {
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setNewCurrency("Euro");
        currencyExchange.setOldCurrency("Dollar");
        Product expectedProduct = new Product();
        expectedProduct.setName("prod1");
        expectedProduct.setProductId(1l);
        expectedProduct.setCurrency("Euro");
        expectedProduct.setPrice(2l);
        when(mockedFutureProductMicroserviceDto.get()).thenReturn(modelMapper.map(expectedProduct, ProductMicroserviceDto.class));
        when(mockedFuturePriceOfProduct.get()).thenReturn(1f);
        when(mockedFutureExchangeRate.get()).thenReturn(2f);
        Product assembledProduct = productService.assembleProduct(mockedFutureProductMicroserviceDto, mockedFuturePriceOfProduct, mockedFutureExchangeRate, currencyExchange);
        Assert.assertEquals(expectedProduct.getProductId(),assembledProduct.getProductId());
    }

}
