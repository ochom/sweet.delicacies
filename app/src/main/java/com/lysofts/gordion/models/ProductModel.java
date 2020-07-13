package com.lysofts.gordion.models;

import java.io.Serializable;

public class ProductModel  implements Serializable {
    String id, name, description, image, price, category, type;

    public ProductModel() {
    }

    public ProductModel(String id, String name, String description, String image, String price, String category, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.category = category;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }
}
