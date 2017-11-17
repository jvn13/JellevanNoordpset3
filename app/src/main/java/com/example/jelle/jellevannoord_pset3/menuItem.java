package com.example.jelle.jellevannoord_pset3;

import java.io.Serializable;

public class menuItem implements Serializable {

    private String category;
    private String description;
    private int price;
    private String image;
    private int id;
    private String name;
    private boolean isOrder = false;

    public menuItem(String name) {
        this.name = name;
    }

    public menuItem(int price, String name, boolean isOrder) {
        this.price = price;
        this.name = name;
        this.isOrder = isOrder;
    }

    public menuItem(String category, String description, int price, String image, int id, String name) {
        this.category = category;
        this. description = description;
        this.price = price;
        this.image = image;
        this.id = id;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isOrder() {
        return isOrder;
    }


}