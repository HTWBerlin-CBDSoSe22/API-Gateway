package com.example.apigateway.controller;

import com.example.apigateway.exception.ComponentNotDeserializeException;
import com.example.apigateway.exception.ComponentsNotFoundException;
import com.example.apigateway.model.Component;
import com.example.apigateway.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/components")
@ResponseBody
public class ComponentController {
    @Autowired
    private ComponentService componentService;
    @GetMapping
    List<Component> getAllComponents() {
        List<Component> listOfAllComponents;

        try {
            listOfAllComponents = this.componentService.showAllComponents();
        } catch (ComponentsNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return  listOfAllComponents;
    }
    @GetMapping(path = "/{componentId}")
    Component findSingleComponentById(@PathVariable("componentId") long componentId) {
        Component singleComponent;

        try {
            singleComponent = this.componentService.showSingleComponent(componentId);
        } catch (ComponentsNotFoundException | ComponentNotDeserializeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return  singleComponent;
    }
}
