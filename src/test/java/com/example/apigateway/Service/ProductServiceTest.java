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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void testAssembleProductProductGood01() throws ExecutionException, InterruptedException, ProductAssemblyException {
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setNewCurrency("Euro");
        currencyExchange.setOldCurrency("Dollar");
        Product productToAssemble = new Product();
        productToAssemble.setName("prod1");
        productToAssemble.setProductId((long)1);
        productToAssemble.setCurrency("Euro");
        productToAssemble.setPrice(2f);
        when(mockedFutureProductMicroserviceDto.get()).thenReturn(modelMapper.map(productToAssemble, ProductMicroserviceDto.class));
        when(mockedFuturePriceOfProduct.get()).thenReturn(1f);
        when(mockedFutureExchangeRate.get()).thenReturn(2f);
        Product assembledProduct = productService.assembleProduct(mockedFutureProductMicroserviceDto, mockedFuturePriceOfProduct, mockedFutureExchangeRate, currencyExchange);
        Assert.assertEquals(productToAssemble.getProductId(),assembledProduct.getProductId());  // 1 long
        Assert.assertEquals("prod1",assembledProduct.getName());
        Assert.assertEquals(2f,assembledProduct.getPrice(), 0.0001);
        Assert.assertEquals("Euro",assembledProduct.getCurrency());
    }
    @Test
    public void testAssembleProductProductWithoutValidPrice() throws ExecutionException, InterruptedException, ProductAssemblyException {
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setNewCurrency("Euro");
        currencyExchange.setOldCurrency("Dollar");
        Product expectedProduct = new Product();
        expectedProduct.setName("prod1");
        expectedProduct.setProductId((long)5);
        expectedProduct.setCurrency("Euro");
        expectedProduct.setPrice(2f);
        when(mockedFutureProductMicroserviceDto.get()).thenReturn(modelMapper.map(expectedProduct, ProductMicroserviceDto.class));
        when(mockedFuturePriceOfProduct.get()).thenReturn(-1f);
        when(mockedFutureExchangeRate.get()).thenReturn(2f);
        Product assembledProduct = productService.assembleProduct(mockedFutureProductMicroserviceDto, mockedFuturePriceOfProduct, mockedFutureExchangeRate, currencyExchange);
        Assert.assertEquals(expectedProduct.getProductId(),assembledProduct.getProductId());    // 5 long
        Assert.assertEquals("prod1",assembledProduct.getName());
        Assert.assertEquals(0f,assembledProduct.getPrice(), 0.0001);
        Assert.assertEquals(null,assembledProduct.getCurrency());
    }
    @Test
    public void testAssembleProductProductWithoutValidCurrency() throws ExecutionException, InterruptedException, ProductAssemblyException {
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setNewCurrency("Euro");
        currencyExchange.setOldCurrency("Dollar");
        Product expectedProduct = new Product();
        expectedProduct.setName("prod3");
        expectedProduct.setProductId((long)5);
        expectedProduct.setCurrency("Euro");
        expectedProduct.setPrice(2f);
        when(mockedFutureProductMicroserviceDto.get()).thenReturn(modelMapper.map(expectedProduct, ProductMicroserviceDto.class));
        when(mockedFuturePriceOfProduct.get()).thenReturn(10f);
        when(mockedFutureExchangeRate.get()).thenReturn(-99.5f);
        Product assembledProduct = productService.assembleProduct(mockedFutureProductMicroserviceDto, mockedFuturePriceOfProduct, mockedFutureExchangeRate, currencyExchange);
        Assert.assertEquals(expectedProduct.getProductId(),assembledProduct.getProductId());    // 5 long
        Assert.assertEquals("prod3",assembledProduct.getName());
        Assert.assertEquals(0f,assembledProduct.getPrice(), 0.0001);
        Assert.assertEquals(null,assembledProduct.getCurrency());
    }
    @Test
    public void testAssembleProductProductShouldThrowProductAssemblyException() throws ExecutionException, InterruptedException, ProductAssemblyException {
        CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto();
        currencyExchange.setNewCurrency("Euro");
        currencyExchange.setOldCurrency("Dollar");
        Product expectedProduct = new Product();
        expectedProduct.setName("prod3");
        expectedProduct.setProductId((long)5);
        expectedProduct.setCurrency("Euro");
        when(mockedFutureProductMicroserviceDto.get()).thenReturn(modelMapper.map(expectedProduct, ProductMicroserviceDto.class));
        when(mockedFuturePriceOfProduct.get()).thenReturn(10f);
        when(mockedFutureExchangeRate.get()).thenThrow(new InterruptedException());
        assertThrows(ProductAssemblyException.class, () ->
            productService.assembleProduct(mockedFutureProductMicroserviceDto, mockedFuturePriceOfProduct, mockedFutureExchangeRate, currencyExchange));

    }

}
