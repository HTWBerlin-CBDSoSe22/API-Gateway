package com.example.apigateway.service;

import com.example.apigateway.exception.ComponentsNotFoundException;
import com.example.apigateway.model.Component;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ComponentService {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;

    @Autowired
    public ComponentService(RabbitTemplate rabbitTemplate, DirectExchange directExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.directExchange = directExchange;
    }

    public List<Component> showAllComponents() throws ComponentsNotFoundException {
        List<Component> listOfAllComponents;
        listOfAllComponents = rabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "getInformation",
                "showComponents",
                new ParameterizedTypeReference<>() {
                });
        if(listOfAllComponents == null) {
            throw new ComponentsNotFoundException("no components found");
        }
        return listOfAllComponents;
    }

    public Component showSingleComponent(long componentId) throws ComponentsNotFoundException {
        Component singleComponent;
        singleComponent = rabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "getInformation",
                componentId,
                new ParameterizedTypeReference<>() {
                });
        if(singleComponent == null) {
            throw new ComponentsNotFoundException("single component not found");
        }
        return singleComponent;
    }
}
