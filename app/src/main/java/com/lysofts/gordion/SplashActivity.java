package com.lysofts.gordion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(SplashActivity.this, Dashboard.class));
            finish();
        }
    }

    public void joinNow(View view) {
        startActivity(new Intent(SplashActivity.this, Signup.class));
    }

    public void loginNow(View view) {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    }
}
