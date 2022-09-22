package com.example.apigateway.model;

import java.util.List;

public class ComponentPrices {
    private List<Float> priceList;

    public ComponentPrices(List<Float> priceList) {
        this.priceList = priceList;
    }

    public List<Float> getPriceList() {
        return priceList;
    }
    public void setPriceList(List<Float> priceList){
        this.priceList = priceList;
    }
}
