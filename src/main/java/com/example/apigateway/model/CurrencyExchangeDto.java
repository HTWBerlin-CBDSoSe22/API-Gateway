package com.example.apigateway.model;

public class CurrencyExchangeDto {
    private String oldCurrency;
    private String newCurrency;

    public String getOldCurrency() {
        return oldCurrency;
    }

    public void setOldCurrency(String oldCurrency) {
        this.oldCurrency = oldCurrency;
    }

    public String getNewCurrency() {
        return newCurrency;
    }

    public void setNewCurrency(String newCurrency) {
        this.newCurrency = newCurrency;
    }
}
