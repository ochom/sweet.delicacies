package com.lysofts.gordion.models;

import java.io.Serializable;

public class ProductModel  implements Serializable {
    String id, name, description, image, price, category, type, category_type;

    public ProductModel() {}

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

    public String getCategory_type() {
        return category_type;
    }
}
