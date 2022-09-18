package com.example.apigateway.service;

import com.example.apigateway.exception.ComponentNotDeserializeException;
import com.example.apigateway.exception.ComponentsNotFoundException;
import com.example.apigateway.model.Component;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<Component> listOfAllComponents = rabbitTemplate.convertSendAndReceiveAsType(
                directExchange.getName(),
                "getInformation",
                "showComponents",
                new ParameterizedTypeReference<>() {
                });
        if(isComponentsListEmpty(listOfAllComponents)) {
            throw new ComponentsNotFoundException();
        }
        return listOfAllComponents;
    }

    public Component showSingleComponent(long componentId) throws ComponentsNotFoundException, ComponentNotDeserializeException {
        List<Component> singleComponent;
        try {
            singleComponent = rabbitTemplate.convertSendAndReceiveAsType(
                    directExchange.getName(),
                    "getInformation",
                    componentId,
                    new ParameterizedTypeReference<>() {
                    });
        } catch(RuntimeException e){
            throw new ComponentNotDeserializeException();
        }
        if(isComponentsListEmpty(singleComponent)) {
            throw new ComponentsNotFoundException();
        }
        return singleComponent.get(0);
    }

    private boolean isComponentsListEmpty(List<Component> returnedComponentsList) {
        if(returnedComponentsList == null)
            return false;
        return returnedComponentsList.isEmpty();
    }
}
