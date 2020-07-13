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

public class Cart {
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();
    List<ProductModel> productsList;
    Type TYPE = new TypeToken<List<ProductModel>>(){}.getType();

    public Cart(Context ctx){
        this.sharedPreferences = ctx.getSharedPreferences("session", Context.MODE_PRIVATE);
        productsList = new ArrayList<>();
    }

    public void add(ProductModel product){
        String sharedData = sharedPreferences.getString("cart",null);
        if (sharedData != null){
            productsList = gson.fromJson(sharedData, TYPE);
        }
        productsList.add(product);

        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.putString("cart", gson.toJson(productsList,TYPE));
        editor.commit();
    }

    public List<ProductModel> getAll(){
        String sharedData = sharedPreferences.getString("cart",null);
        productsList = gson.fromJson(sharedData, TYPE);
        return productsList;
    }

    public void delete(String ID){
        String sharedData = sharedPreferences.getString("cart",null);
        productsList = gson.fromJson(sharedData, TYPE);
        for (ProductModel product: productsList){
            if (product.getId().equals(ID)){
                Log.d("FAV>>>>>>",product.getName()+" Product found");
                productsList.remove(product);

                SharedPreferences.Editor editor  = sharedPreferences.edit();
                editor.putString("cart", gson.toJson(productsList,TYPE));
                editor.commit();
                Log.d("FAV>>>>>>","Product deleted");
                return;
            }
        }
    }

    public void deleteAll(){
        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.remove("cart");
        editor.commit();
    }

    public boolean isInCart(String ID){
        String sharedData = sharedPreferences.getString("cart",null);
        if (sharedData == null){return false;}
        productsList = gson.fromJson(sharedData, TYPE);
        for (ProductModel product: productsList){
            if (product.getId().equals(ID)){
               return true;
            }
        }
        return false;
    }
}

