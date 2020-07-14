package com.lysofts.gordion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
    EditText et_name, et_email, et_phone, et_password;
    ProgressDialog progressDialog;
    DatabaseReference rootReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        progressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
    }

    public void Register(View view) {
        String name = et_name.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();
        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write you email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write you phone number", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please create a password", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Authenticating. Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            validatePhoneNumber(name,email,phone, password);
        }
    }

    private void validatePhoneNumber(final String name, final String email, final String phone, final String password) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0){
                    // email not existed
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //Toast.makeText(Signup.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                //create profile
                                rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        HashMap<String, Object> userData=new HashMap<>();
                                        userData.put("name",name);
                                        userData.put("phone",phone);
                                        rootReference.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (progressDialog.isShowing()){
                                                            progressDialog.dismiss();
                                                        }
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(Signup.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                            // Toast.makeText(Signup.this, "Your profile has been created", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(Signup.this, Dashboard.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finishAffinity();
                                                        }else{
                                                            Toast.makeText(Signup.this, "Error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                       }
                    });
                }else {
                    // email existed
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(Signup.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
