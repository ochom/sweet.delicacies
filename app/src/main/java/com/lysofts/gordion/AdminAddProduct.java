package com.lysofts.gordion;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lysofts.gordion.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AdminAddProduct extends AppCompatActivity {
    private String name, description, price, type, category,
            currentDate,currentTime, downloadedImageUri, productRandomKey;
    private EditText product_name,product_description, product_price;
    private Button btn_add_product;
    private Spinner fashion_type, fashion_category;
    private ImageView product_image;
    private static  final  int GALLERY_PICK = 1;
    private ProductModel product;
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
        product = (ProductModel) getIntent().getSerializableExtra("product");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Product Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        product_image = findViewById(R.id.imv_product_image);
        product_name = findViewById(R.id.et_product_name);
        product_description = findViewById(R.id.et_product_description);
        product_price = findViewById(R.id.et_product_price);
        fashion_type = findViewById(R.id.sp_fashion_type);
        fashion_category = findViewById(R.id.sp_fashion_category);
        btn_add_product = findViewById(R.id.btn_add_product);

        progressDialog = new ProgressDialog(this);
        initValues();
    }

    private void initValues(){
        String[] types = getResources().getStringArray(R.array.fashion_types);
        String[] categories = getResources().getStringArray(R.array.fashion_categories);
        if (product!=null){
            Picasso.get().load(product.getImage()).into(product_image);
            product_name.setText(product.getName());
            product_description.setText(product.getDescription());
            product_price.setText(product.getPrice());
            fashion_type.setSelection(Arrays.asList(types).indexOf(product.getType()));
            fashion_category.setSelection(Arrays.asList(categories).indexOf(product.getCategory()));
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
        type = fashion_type.getSelectedItem().toString();
        category = fashion_category.getSelectedItem().toString();

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
        HashMap<String,Object> productData=new HashMap<>();
        productData.put("id",product.getId());
        productData.put("name",name);
        productData.put("description",description);
        productData.put("type",type);
        productData.put("category",category);
        productData.put("price",price);
        productData.put("image",downloadedImageUri);
        productData.put("type_category",type.substring(0, type.indexOf("'"))+"_"+category);

        databaseReference.child(product.getId()).updateChildren(productData).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddYYYY");
        currentDate = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ssa");
        currentTime = timeFormat.format(calendar.getTime());
        productRandomKey = currentDate+currentTime;

        final StorageReference filePath = imageRef.child(imageURI.getLastPathSegment()+productRandomKey+".jpg");
        final UploadTask uploadTask = filePath.putFile(imageURI);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminAddProduct.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AdminAddProduct.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            progressDialog.dismiss();
                            throw task.getException();
                        }
                        downloadedImageUri = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
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
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String,Object> productData=new HashMap<>();
        productData.put("id",productRandomKey);
        productData.put("name",name);
        productData.put("description",description);
        productData.put("type",type);
        productData.put("category",category);
        productData.put("price",price);
        productData.put("image",downloadedImageUri);
        productData.put("type_category",type.substring(0, type.indexOf("'"))+"_"+category);

        databaseReference.child(productRandomKey).updateChildren(productData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (task.isSuccessful()){
                    Toast.makeText(AdminAddProduct.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(AdminAddProduct.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
