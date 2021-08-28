package com.sweet.delicacies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sweet.delicacies.models.Product;
import com.squareup.picasso.Picasso;
import com.sweet.delicacies.utils.ObjectToMap;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAddProduct extends AppCompatActivity {
    private String name, description, price,
            currentDate,currentTime, downloadedImageUri, productRandomKey;
    private EditText product_name,product_description, product_price;
    private Button btn_add_product;
    private ImageView product_image;
    private static  final  int GALLERY_PICK = 1;
    private Product product;
    private Uri imageURI;
    private StorageReference imageRef;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        imageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        product = (Product) getIntent().getSerializableExtra("product");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Product Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        product_image = findViewById(R.id.imv_product_image);
        product_name = findViewById(R.id.et_product_name);
        product_description = findViewById(R.id.et_product_description);
        product_price = findViewById(R.id.et_product_price);
        btn_add_product = findViewById(R.id.btn_add_product);

        progressDialog = new ProgressDialog(this);
        initValues();
    }

    private void initValues(){
        if (product!=null){
            Picasso.get().load(product.getImage()).into(product_image);
            product_name.setText(product.getName());
            product_description.setText(product.getDescription());
            product_price.setText(String.valueOf(product.getPrice()));
            downloadedImageUri = product.getImage();
            btn_add_product.setText("Update product");
        }
    }

    public void openGallery(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK && data !=null){
         imageURI = data.getData();
         product_image.setImageURI(imageURI);
        }

    }

    public void addProduct(View view) {
        name = product_name.getText().toString();
        description = product_description.getText().toString();
        price = product_price.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write product name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(description)){
            Toast.makeText(this,"Please write product description", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(price)){
            Toast.makeText(this,"Please write product price", Toast.LENGTH_SHORT).show();
        }else if (imageURI==null && product==null){
            Toast.makeText(this,"Product image is required", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setMessage("Uploading product, please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            if (product!=null){
                updateProductInfo();
            }else{
                storeProductInfo();
            }
        }
    }

    private void updateProductInfo() {
        Product product = new Product();
        product.setId(product.getId());
        product.setName(name);
        product.setDescription(description);
        product.setImage(downloadedImageUri);
        product.setPrice(Double.valueOf(price));


        databaseReference.child(product.getId()).updateChildren(ObjectToMap.MapObject(product)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (task.isSuccessful()){
                    Toast.makeText(AdminAddProduct.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(AdminAddProduct.this, "Error: "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void storeProductInfo() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        currentDate = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ssa");
        currentTime = timeFormat.format(calendar.getTime());
        productRandomKey = currentDate+currentTime;

        final StorageReference filePath = imageRef.child(imageURI.getLastPathSegment()+productRandomKey+".jpg");
        final UploadTask uploadTask = filePath.putFile(imageURI);
        uploadTask.addOnFailureListener(e -> {
            Toast.makeText(AdminAddProduct.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    progressDialog.dismiss();
                    throw task.getException();
                }
                downloadedImageUri = filePath.getDownloadUrl().toString();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    downloadedImageUri = task.getResult().toString();
                    // Toast.makeText(AdminAddProduct.this, "Got Image URI already", Toast.LENGTH_SHORT).show();
                    saveProductInfoToDatabase();
                }else{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(AdminAddProduct.this, "Error: "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveProductInfoToDatabase() {
        Product product = new Product();
        product.setId(productRandomKey);
        product.setName(name);
        product.setDescription(description);
        product.setImage(downloadedImageUri);
        product.setPrice(Double.valueOf(price));

        databaseReference.child(productRandomKey).updateChildren(ObjectToMap.MapObject(product)).addOnCompleteListener(task -> {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if (task.isSuccessful()){
                Toast.makeText(AdminAddProduct.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(AdminAddProduct.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
