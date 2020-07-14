package com.lysofts.gordion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lysofts.gordion.holders.CheckoutListAdapter;
import com.lysofts.gordion.holders.FavoritesAdapter;
import com.lysofts.gordion.session.Favorites;

public class FavoritesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView total_items, total_amount;
    RelativeLayout no_data_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Checkout");
        setSupportActionBar(toolbar);

        total_items = findViewById(R.id.total_items_view);
        total_amount = findViewById(R.id.total_billing_amount_view);
        no_data_view = findViewById(R.id.no_data);

        recyclerView = findViewById(R.id.checkout_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new FavoritesAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        updateOtherUI();
    }

    public void updateOtherUI() {
        total_items.setText(String.valueOf(new Favorites(this).countItems()));
        if (new Favorites(this).countItems()==0){
            no_data_view.setVisibility(View.VISIBLE);
        }else{
            no_data_view.setVisibility(View.GONE);
        }
    }

    public void continueShopping(View view) {
        finish();
    }

    public void openFavorites(View view) {
        startActivity(new Intent(FavoritesActivity.this, CheckOut.class));
        finish();
    }
}
