package com.lysofts.gordion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lysofts.gordion.holders.FavoritesAdapter;
import com.lysofts.gordion.session.Favorites;

public class FavoritesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RelativeLayout no_data_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Saved Items");
        setSupportActionBar(toolbar);

        no_data_view = findViewById(R.id.no_data);

        recyclerView = findViewById(R.id.checkout_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new FavoritesAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        updateOtherUI();
    }

    public void updateOtherUI() {
        if (new Favorites(this).countItems()==0){
            no_data_view.setVisibility(View.VISIBLE);
        }else{
            no_data_view.setVisibility(View.GONE);
        }
    }

    public void continueShopping(View view) {
        finish();
    }

    public void openCart(View view) {
        startActivity(new Intent(FavoritesActivity.this, CheckOut.class));
        finish();
    }
}
