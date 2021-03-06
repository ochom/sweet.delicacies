package com.sweet.delicacies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sweet.delicacies.holders.ProductViewHolder;
import com.sweet.delicacies.models.Product;
import com.sweet.delicacies.session.Cart;
import com.sweet.delicacies.session.Favorites;

public class Products extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout no_data_view;
    BottomNavigationView bottomNavigationView;
    TextView no_data_text_view;
    ImageView no_data_image_view;
    String type, category;

    FirebaseAuth firebaseAuth;
    DatabaseReference rootReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);


        firebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        shimmerFrameLayout = findViewById(R.id.my_shimmmer);
        no_data_view = findViewById(R.id.no_data);
        no_data_text_view = findViewById(R.id.no_data_text);
        no_data_image_view = findViewById(R.id.no_data_image);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        type = getIntent().getStringExtra("type");
        category = getIntent().getStringExtra("category");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    no_data_image_view.setImageResource(R.drawable.ic_remove_shopping_cart_black_24dp);
                    no_data_text_view.setText("Products Not Found");
                    no_data_view.setVisibility(View.VISIBLE);
                }
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                no_data_image_view.setImageResource(R.drawable.ic_cloud_off_black_24dp);
                no_data_text_view.setText("You are offline");
                no_data_view.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });
        

        FirebaseRecyclerOptions<Product> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(databaseReference,
                        Product.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final Product model) {
                holder.setProduct(model);
                if (new Favorites(Products.this).isInFavorites(model.getId())){
                    holder.imvFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    holder.imvFavorite.setTag(2);
                }
                if (new Cart(Products.this).isInCart(model.getId())){
                    holder.imvAddCart.setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
                    holder.imvAddCart.setTag(2);
                }
                holder.mView.setOnClickListener(v -> {
                    Intent intent = new Intent(Products.this, ProductDetail.class);
                    intent.putExtra("product", model);
                    startActivity(intent);
                });

                holder.imvFavorite.setOnClickListener(v -> {
                    if (Integer.parseInt(holder.imvFavorite.getTag().toString())==1){
                        holder.imvFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                        holder.imvFavorite.setTag(2);
                        new Favorites(Products.this).add(model);
                    }else{
                        holder.imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        holder.imvFavorite.setTag(1);
                        new Favorites(Products.this).delete(model.getId());
                    }
                });

                holder.imvAddCart.setOnClickListener(v -> {
                    if (Integer.parseInt(holder.imvAddCart.getTag().toString())==1){
                        holder.imvAddCart.setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
                        holder.imvAddCart.setTag(2);
                        new Cart(Products.this).add(model);
                    }else{
                        holder.imvAddCart.setImageResource(R.drawable.ic_add_shopping_cart_black_24dp);
                        holder.imvAddCart.setTag(1);
                        new Cart(Products.this).delete(model.getId());
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

        handleBottomNavigation();
    }

    private void handleBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.nav_favorites:
                    startActivity(new Intent(Products.this, FavoritesActivity.class));
                    return true;
                case R.id.nav_cart:
                    startActivity(new Intent(Products.this, CheckOut.class));
                    return true;
                default:
                    return false;
            }
        });
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_account:
                if(firebaseAuth.getCurrentUser()!= null){
                    startActivity(new Intent(Products.this, ProfileActivity.class));
                }else{
                    startActivity(new Intent(Products.this, SplashActivity.class));
                }
                return true;
            case R.id.nav_i_am_admin:
                final String userID = firebaseAuth.getCurrentUser().getUid();
                rootReference.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(userID)){
                            startActivity(new Intent(Products.this, AdminPanel.class));
                        }else{
                            Toast.makeText(Products.this, "You are not an admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                return true;
            case R.id.nav_cart:
                startActivity(new Intent(Products.this, CheckOut.class));
                return true;
            case R.id.nav_favorites:
                startActivity(new Intent(Products.this, FavoritesActivity.class));
                return true;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                new Cart(this).deleteAll();
                new Favorites(this).deleteAll();
                startActivity(new Intent(Products.this, SplashActivity.class));
                finishAffinity();
                Toast.makeText(Products.this, "You have been logged out", Toast.LENGTH_SHORT).show();
                return true;
            default:
                Toast.makeText(this, "I don't know clicked button", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }
}
