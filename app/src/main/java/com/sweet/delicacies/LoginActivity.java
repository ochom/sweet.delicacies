package com.sweet.delicacies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText et_email, et_password;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        progressDialog = new ProgressDialog(this);
    }

    public void Login(View view) {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write you email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please create a password", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Authenticating. Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, Products.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
            );
        }

    }
}
