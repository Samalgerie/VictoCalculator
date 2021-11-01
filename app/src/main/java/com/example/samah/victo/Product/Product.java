package com.example.samah.victo.Product;

public class Product {
    private int id;
    private String name;
    private String Comp;
    private float maxPrice;
    private float weight;

    public Product(int id, String name, String Comp, float maxPrice, float weight) {
        this.id = id;
        this.name = name;
        this.Comp = Comp;
        this.maxPrice = maxPrice;
        this.weight = weight;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public float getWeight() {
        return weight;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getComp() {
        return Comp;
    }



}
