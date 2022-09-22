package com.example.apigateway.Service;

import com.example.apigateway.model.Component;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductMicroserviceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperTest {
    private ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    public void setup() {
        this.modelMapper = new ModelMapper();
    }

    @Test
    public void whenConvertProductMicroserviceDtoToProduct_thenCorrect() {
        Component testComponent = new Component("Banana",0.75,
                13,120,"yellow","Ecuador",
                "H. extra","dry","Tropical fruit",
                "winter");
        Set<Component> componentsOfProduct = new HashSet<>();
        componentsOfProduct.add(testComponent);
        ProductMicroserviceDto productMicroserviceDto = new ProductMicroserviceDto();
        productMicroserviceDto.setName("defaultProduct1");
        productMicroserviceDto.setConsistsOf(componentsOfProduct);

        Product product = modelMapper.map(productMicroserviceDto, Product.class);
        assertEquals(productMicroserviceDto.getName(),product.getName());
    }@Test
    public void whenConvertProductToProductMicroserviceDto_thenCorrect() {
        Component testComponent = new Component("Banana",0.75,
                13,120,"yellow","Ecuador",
                "H. extra","dry","Tropical fruit",
                "winter");
        Set<Component> componentsOfProduct = new HashSet<>();
        componentsOfProduct.add(testComponent);
        Product product = new Product();
        product.setName("fruitSalad1");
        product.setConsistsOf(componentsOfProduct);
        product.setProductId((long)123456789);
        product.setCurrency("Euro");

        ProductMicroserviceDto productMicroserviceDto = modelMapper.map(product, ProductMicroserviceDto.class);
        assertEquals(product.getName(),productMicroserviceDto.getName());
    }


}
