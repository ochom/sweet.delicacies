package com.sweet.delicacies.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sweet.delicacies.models.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Favorites {
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();
    List<Product> productsList;
    Type TYPE = new TypeToken<List<Product>>(){}.getType();
    public Favorites(Context ctx){
        this.sharedPreferences = ctx.getSharedPreferences("session", Context.MODE_PRIVATE);
        productsList = new ArrayList<>();
    }

    public void add(Product product){
        String sharedData = sharedPreferences.getString("favorites",null);
        if (sharedData != null){
            productsList = gson.fromJson(sharedData, TYPE);
        }
        productsList.add(product);

        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.putString("favorites", gson.toJson(productsList,TYPE));
        editor.commit();
    }

    public List<Product> getAll(){
        String sharedData = sharedPreferences.getString("favorites",null);
        if (sharedData != null){
            productsList = gson.fromJson(sharedData, TYPE);
        }
        return productsList;
    }

    public void delete(String ID){
        String sharedData = sharedPreferences.getString("favorites",null);
        if (sharedData != null){
            productsList = gson.fromJson(sharedData, TYPE);
        }
        for (Product product: productsList){
            if (product.getId().equals(ID)){
                productsList.remove(product);

                SharedPreferences.Editor editor  = sharedPreferences.edit();
                editor.putString("favorites", gson.toJson(productsList,TYPE));
                editor.commit();
                return;
            }
        }
    }

    public void deleteAll(){
        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.remove("favorites");
        editor.commit();
    }

    public boolean isInFavorites(String ID){
        String sharedData = sharedPreferences.getString("favorites",null);
        if (sharedData != null){
            productsList = gson.fromJson(sharedData, TYPE);
        }
        for (Product product: productsList){
            if (product.getId().equals(ID)){
                return true;
            }
        }
        return false;
    }

    public int countItems() {
        String sharedData = sharedPreferences.getString("favorites",null);
        if (sharedData != null){
            productsList = gson.fromJson(sharedData, TYPE);
        }
        return productsList.size();
    }
}

