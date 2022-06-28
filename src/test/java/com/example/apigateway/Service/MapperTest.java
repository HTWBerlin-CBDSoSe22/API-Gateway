package com.example.apigateway.Service;

import com.example.apigateway.model.Component;
import com.example.apigateway.model.Product.Product;
import com.example.apigateway.model.Product.ProductCreationDto;
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
    public void whenConvertProductCreationDtoToProduct_thenCorrect() {
        Component testComponent = new Component("Banana",0.75,
                13,120,"yellow","Ecuador",
                "H. extra","dry","Tropical fruit",
                "winter");
        Set<Component> componentsOfProduct = new HashSet<>();
        componentsOfProduct.add(testComponent);
        ProductCreationDto productCreationDto = new ProductCreationDto();
        productCreationDto.setName("defaultProduct1");
        productCreationDto.setConsistsOf(componentsOfProduct);

        Product product = modelMapper.map(productCreationDto, Product.class);
        assertEquals(productCreationDto.getName(),product.getName());
    }


}
