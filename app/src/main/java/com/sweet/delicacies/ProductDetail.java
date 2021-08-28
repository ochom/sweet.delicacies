package com.sweet.delicacies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.sweet.delicacies.models.Product;
import com.sweet.delicacies.session.Cart;
import com.sweet.delicacies.session.Favorites;
import com.squareup.picasso.Picasso;

public class ProductDetail extends AppCompatActivity {
    private TextView name, price, description;
    private ImageView image,imvFavorite, imvAddCart;
    Product product;
    String id;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        product = (Product) getIntent().getSerializableExtra("product");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Product Details");
        setSupportActionBar(toolbar);

        name = findViewById(R.id.tv_product_name);
        price = findViewById(R.id.tv_product_price);
        description = findViewById(R.id.tv_product_description);
        image = findViewById(R.id.imv_product_image);
        imvAddCart = findViewById(R.id.imv_cart_btn);
        imvFavorite = findViewById(R.id.imv_favorite_btn);

        if (product != null){
            gepProduct();
        }

    }

    private void gepProduct() {
        name.setText(product.getName());
        price.setText("$ "+product.getPrice());
        description.setText(product.getDescription());
        Picasso.get().load(product.getImage())
                .into(image);

        if (new Cart(this).isInCart(product.getId())){
            imvAddCart.setTag(2);
            imvAddCart.setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
        }
        if (new Favorites(this).isInFavorites(product.getId())){
            imvFavorite.setTag(2);
            imvFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
    }

    public void addCart(View v){
        if (Integer.parseInt(v.getTag().toString())==1){
            ((ImageView)v).setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
            v.setTag(2);
            new Cart(ProductDetail.this).add(product);
        }else{
            ((ImageView)v).setImageResource(R.drawable.ic_add_shopping_cart_black_24dp);
            v.setTag(1);
            new Cart(ProductDetail.this).delete(product.getId());
        }
    }

    public void addFavorite(View v){
        if (Integer.parseInt(v.getTag().toString())==1){
            ((ImageView)v).setImageResource(R.drawable.ic_favorite_black_24dp);
            v.setTag(2);
            new Favorites(ProductDetail.this).add(product);
        }else{
            ((ImageView)v).setImageResource(R.drawable.ic_favorite_border_black_24dp);
            v.setTag(1);
            new Favorites(ProductDetail.this).delete(product.getId());
        }
    }

    public void rentNow(View v){
        if (!new Cart(this).isInCart(product.getId())){
            imvAddCart.setTag(2);
            imvAddCart.setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
            new Cart(this).add(product);
        }
        startActivity(new Intent(ProductDetail.this, CheckOut.class));
        finish();
    }

}
