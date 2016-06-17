package com.example.vanjavidenov.etf2.resources;

/**
 * Created by vanjavidenov on 6/15/16.
 */
public class Item {
    private String name;
    private String price;
    private String description;

    public Item(String un, String pass, String em){
        name = un;
        price = pass;
        description = em;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
