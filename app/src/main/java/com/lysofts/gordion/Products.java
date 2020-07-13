package com.lysofts.gordion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lysofts.gordion.holders.ProductViewHolder;
import com.lysofts.gordion.models.ProductModel;
import com.lysofts.gordion.session.Cart;
import com.lysofts.gordion.session.Favorites;

import java.util.ArrayList;

public class Products extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ProductModel> products = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> firebaseRecyclerAdapter;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        shimmerFrameLayout = findViewById(R.id.my_shimmmer);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);


        FirebaseRecyclerOptions<ProductModel> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(databaseReference, ProductModel.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ProductModel, ProductViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final ProductModel model) {
                holder.setProduct(model);
                if (new Favorites(Products.this).isInFavorites(model.getId())){
                    holder.imvFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    holder.imvFavorite.setTag(2);
                }
                if (new Cart(Products.this).isInCart(model.getId())){
                    holder.imvAddCart.setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
                    holder.imvAddCart.setTag(2);
                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Products.this, ProductDetail.class);
                        intent.putExtra("product", model);
                        startActivity(intent);
                    }
                });

                holder.imvFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(holder.imvFavorite.getTag().toString())==1){
                            holder.imvFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            holder.imvFavorite.setTag(2);
                            new Favorites(Products.this).add(model);
                        }else{
                            holder.imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            holder.imvFavorite.setTag(1);
                            new Favorites(Products.this).delete(model.getId());
                        }
                    }
                });

                holder.imvAddCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(holder.imvAddCart.getTag().toString())==1){
                            holder.imvAddCart.setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
                            holder.imvAddCart.setTag(2);
                            new Cart(Products.this).add(model);
                        }else{
                            holder.imvAddCart.setImageResource(R.drawable.ic_add_shopping_cart_black_24dp);
                            holder.imvAddCart.setTag(1);
                            new Cart(Products.this).delete(model.getId());
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                shimmerFrameLayout.setVisibility(View.GONE);
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_model, parent, false);
                return new ProductViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
