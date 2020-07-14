package com.lysofts.gordion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    FirebaseAuth firebaseAuth;
    DatabaseReference rootReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Men");
        adapter.addFragment(new Tab2Fragment(), "Women");
        adapter.addFragment(new Tab3Fragment(), "Children");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }



    public void loadMenProducts(View view){
        Intent intent = new Intent(Dashboard.this, Products.class);
        intent.putExtra("type","Men");
        intent.putExtra("category",view.getTag().toString());
        startActivity(intent);
    }

    public void loadWomenProducts(View view){
        Intent intent = new Intent(Dashboard.this, Products.class);
        intent.putExtra("type","Women");
        intent.putExtra("category",view.getTag().toString());
        startActivity(intent);
    }

    public void loadChildrenProducts(View view){
        Intent intent = new Intent(Dashboard.this, Products.class);
        intent.putExtra("type","Children");
        intent.putExtra("category",view.getTag().toString());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.account:
                if(firebaseAuth.getCurrentUser()!= null){
                    startActivity(new Intent(Dashboard.this, ProfileActivity.class));
                }else{
                    startActivity(new Intent(Dashboard.this, SplashActivity.class));
                }
                return true;
            case R.id.i_am_admin:
                final String userID = firebaseAuth.getCurrentUser().getUid();
                rootReference.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(userID)){
                            startActivity(new Intent(Dashboard.this, AdminPanel.class));
                        }else{
                            Toast.makeText(Dashboard.this, "You are not an admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return true;
            case R.id.cart:
                startActivity(new Intent(Dashboard.this, CheckOut.class));
                return true;
            default:
                Toast.makeText(this, "I dont know clicked button", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }
}