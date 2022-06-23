package com.example.apigateway.Service;

import com.example.apigateway.Model.Product.ProductCreationDto;
import com.example.apigateway.amqp.Config;
import com.example.apigateway.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

public class ProductServiceTest {
    private ProductService productService;
    private RabbitTemplate rabbitTemplateMock;
    private AsyncRabbitTemplate asyncRabbitTemplateMock;
    private DirectExchange directExchange;
    @Autowired
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private Config config = new Config();

    @Before
    public void setUp() {
        this.rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
        this.asyncRabbitTemplateMock = Mockito.mock(AsyncRabbitTemplate.class);

        this.productService = new ProductService(this.rabbitTemplateMock, config.directExchange(),modelMapper, asyncRabbitTemplateMock);
    }

    @Test
    public void testCreateProduct() {
        ProductCreationDto productToCreate = new ProductCreationDto();
        assertThatCode(() -> this.productService.showFullProduct(productToCreate)).doesNotThrowAnyException();
        Mockito.verify(this.rabbitTemplateMock).convertSendAndReceiveAsType(
                config.directExchange().getName(),
                "createProduct",
                productToCreate.getName(),
                new ParameterizedTypeReference<>() {
                });
    }

}
