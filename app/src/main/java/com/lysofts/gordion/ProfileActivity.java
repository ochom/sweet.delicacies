package com.lysofts.gordion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lysofts.gordion.session.Cart;
import com.lysofts.gordion.session.Favorites;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void Logout(View view) {
        firebaseAuth.signOut();
        new Cart(this).deleteAll();
        new Favorites(this).deleteAll();
        startActivity(new Intent(ProfileActivity.this, SplashActivity.class));
        finishAffinity();
        Toast.makeText(ProfileActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
    }

}
