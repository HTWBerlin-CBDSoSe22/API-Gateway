package com.example.apigateway.Model;

public class CurrencyExchangeDto {
    private String currentExchange;
    private String newExchange;

    public String getCurrentExchange() {
        return currentExchange;
    }

    public void setCurrentExchange(String currentExchange) {
        this.currentExchange = currentExchange;
    }

    public String getNewExchange() {
        return newExchange;
    }

    public void setNewExchange(String newExchange) {
        this.newExchange = newExchange;
    }
}
