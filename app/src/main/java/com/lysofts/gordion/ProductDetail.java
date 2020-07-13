package com.lysofts.gordion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.gordion.models.ProductModel;
import com.squareup.picasso.Picasso;

public class ProductDetail extends AppCompatActivity {
    private TextView name, price, description;
    private ImageView image;
    ProductModel product;
    String id;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        product = (ProductModel) getIntent().getSerializableExtra("product");



        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Product Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = findViewById(R.id.tv_product_name);
        price = findViewById(R.id.tv_product_price);
        description = findViewById(R.id.tv_product_description);
        image = findViewById(R.id.imv_product_image);

        if (product !=null){
            gepProduct();
        }

    }

    private void gepProduct() {
        name.setText(product.getName());
        price.setText("Ksh. "+product.getPrice());
        description.setText(product.getDescription());
        Picasso.get().load(product.getImage())
                .into(image);
    }
}
