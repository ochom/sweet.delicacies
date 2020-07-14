package com.lysofts.gordion.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lysofts.gordion.models.ProductModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Favorites {
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();
    List<ProductModel> productsList = new ArrayList<>();
    Type TYPE = new TypeToken<List<ProductModel>>(){}.getType();
    public Favorites(Context ctx){
        this.sharedPreferences = ctx.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public void add(ProductModel product){
        String sharedData = sharedPreferences.getString("favorites",null);
        if (sharedData != null){
            productsList = gson.fromJson(sharedData, TYPE);
        }
        productsList.add(product);

        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.putString("favorites", gson.toJson(productsList,TYPE));
        editor.commit();
    }

    public List<ProductModel> getAll(){
        String sharedData = sharedPreferences.getString("favorites",null);
        productsList = gson.fromJson(sharedData, TYPE);
        return productsList;
    }

    public void delete(String ID){
        String sharedData = sharedPreferences.getString("favorites",null);
        productsList = gson.fromJson(sharedData, TYPE);
        for (ProductModel product: productsList){
            if (product.getId().equals(ID)){
                Log.d("FAV>>>>>>",product.getName()+" Product found");
                productsList.remove(product);

                SharedPreferences.Editor editor  = sharedPreferences.edit();
                editor.putString("favorites", gson.toJson(productsList,TYPE));
                editor.commit();
                Log.d("FAV>>>>>>","Product deleted");
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
        if (sharedData == null){return false;}
        productsList = gson.fromJson(sharedData, TYPE);
        for (ProductModel product: productsList){
            if (product.getId().equals(ID)){
                return true;
            }
        }
        return false;
    }

    public int countItems() {
        String sharedData = sharedPreferences.getString("favorites",null);
        productsList = gson.fromJson(sharedData, TYPE);
        return productsList.size();
    }
}

