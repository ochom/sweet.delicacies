package com.sweet.delicacies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sweet.delicacies.models.Profile;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    EditText et_phone, et_street, et_city, et_county;
    String phone, street, city, county;
    TextView tv_name, tv_email;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tv_name = findViewById(R.id.tv_name);
        tv_email=findViewById(R.id.tv_email);

        et_phone = findViewById(R.id.et_phone);
        et_street = findViewById(R.id.et_street);
        et_city = findViewById(R.id.et_city);
        et_county = findViewById(R.id.et_county);

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getUser();

    }

    private void getUser() {
        tv_email.setText(firebaseAuth.getCurrentUser().getEmail());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Profile profile = snapshot.getValue(Profile.class);
                    tv_name.setText(profile.getName());
                    et_phone.setText(profile.getPhone());
                    et_street.setText(profile.getStreet());
                    et_city.setText(profile.getCity());
                    et_county.setText(profile.getCounty());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update_profile:
                updateProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateProfile() {
        phone = et_phone.getText().toString();
        street = et_street.getText().toString();
        city = et_city.getText().toString();
        county = et_county.getText().toString();
        HashMap<String, Object> user_data = new HashMap<>();
        user_data.put("phone", phone);
        user_data.put("street", street);
        user_data.put("city", city);
        user_data.put("county", county);

        dbRef.updateChildren(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this,"User profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ProfileActivity.this,"Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
