package com.example.apigateway.model;

public class Component {
    private Long componentId;
    private String name;

    private double price;

    private double height;

    private double weight;

    private String color;

    private String countryOfOrigin;

    private String grade;

    private String category;

    private String classification;

    private String harvestSeason;

    public Component() {
    }

    public Component(String name, double price, double height, double weight, String color, String countryOfOrigin, String grade, String category, String classification, String harvestSeason) {
        this.name = name;
        this.price = price;
        this.height = height;
        this.weight = weight;
        this.color = color;
        this.countryOfOrigin = countryOfOrigin;
        this.grade = grade;
        this.category = category;
        this.classification = classification;
        this.harvestSeason = harvestSeason;
    }

    public Long getComponentId() {
        return componentId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }


    public double getHeight() {
        return height;
    }


    public double getWeight() {
        return weight;
    }


    public String getColor() {
        return color;
    }


    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public String getGrade() {
        return grade;
    }

    public String getCategory() {
        return category;
    }

    public String getClassification() {
        return classification;
    }

    public String getHarvestSeason() {
        return harvestSeason;
    }

}
