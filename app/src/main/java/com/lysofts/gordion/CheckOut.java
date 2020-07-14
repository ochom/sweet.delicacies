package com.lysofts.gordion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.gordion.holders.CheckoutListAdapter;
import com.lysofts.gordion.models.ProductModel;
import com.lysofts.gordion.models.Profile;
import com.lysofts.gordion.session.Cart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class CheckOut extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView total_items, total_amount;
    RelativeLayout no_data_view;
    AlertDialog alertDialog;

    DatabaseReference ordersRef;
    DatabaseReference usersRefs;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Checkout");
        setSupportActionBar(toolbar);

        total_items = findViewById(R.id.total_items_view);
        total_amount = findViewById(R.id.total_billing_amount_view);
        no_data_view = findViewById(R.id.no_data);

        recyclerView = findViewById(R.id.checkout_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new CheckoutListAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        updateOtherUI();

        firebaseAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        usersRefs = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void updateOtherUI() {
        total_items.setText(String.valueOf(new Cart(this).countItems()));
        total_amount.setText(String.valueOf(new Cart(this).getBillAmount()));
        if (new Cart(this).countItems()==0){
            no_data_view.setVisibility(View.VISIBLE);
        }else{
            no_data_view.setVisibility(View.GONE);
        }
    }

    public void continueShopping(View view) {
        finish();
    }

    public void checkOut(View v){
        if (new Cart(this).countItems() > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(CheckOut.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);

            final View dialogView = LayoutInflater.from(CheckOut.this).inflate(R.layout.billing_info_layout, viewGroup, false);
            builder.setView(dialogView);
            ((Button)dialogView.findViewById(R.id.btn_submit_payment)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateData(dialogView);
                }
            });
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.product_detail_bg_1);
            alertDialog.show();
        }

    }

    private void validateData(View parentView) {
        final TextView tv_street, tv_city, tv_county, tv_mpesa;
        String street, city, county, mpesa;
        tv_street =  parentView.findViewById(R.id.et_street);
        tv_city = parentView.findViewById(R.id.et_city);
        tv_county = parentView.findViewById(R.id.et_county);
        tv_mpesa = parentView.findViewById(R.id.et_mpesa_number);


        usersRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(">>>>>>>>>>>", String.valueOf(snapshot));
                if (snapshot.child(firebaseAuth.getCurrentUser().getUid()).exists()){
                    Profile profile = snapshot.child(firebaseAuth.getCurrentUser().getUid()).getValue(Profile.class);
                    tv_street.setText(profile.getStreet());
                    tv_city.setText(profile.getCity());
                    tv_county.setText(profile.getCounty());
                    tv_mpesa.setText(profile.getMpesa());
                    System.out.println("HEu>>>>>>>>>>"+profile);
                }
                System.out.println("HEu>>>>>>>>>>"+"FUCKkkkkkkkkkkkk");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        street=tv_street.getText().toString();
        city=tv_city.getText().toString();
        county=tv_county.getText().toString();
        mpesa=tv_mpesa.getText().toString();

        if (TextUtils.isEmpty(street)){
            Toast.makeText(this, "Street address of building is required", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(city)){
            Toast.makeText(this, "Please enter you nearest city or town", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(county)){
            Toast.makeText(this, "Please enter your county or state", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(mpesa)){
            Toast.makeText(this, "M-Pesa Payment Number is required", Toast.LENGTH_SHORT).show();
        }else {
            submitData(street, city, county,mpesa);
        }
    }

    private void submitData(String street, String city, String county, String mpesa) {
        String order_key = firebaseAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, YYYY");
        order_key += dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat  = new SimpleDateFormat("HH:mm:ss");
        order_key += timeFormat.format(calendar.getTime());

        final HashMap<String, Object> billing_address = new HashMap<>();
        billing_address.put("street", street);
        billing_address.put("city", city);
        billing_address.put("county", county);
        billing_address.put("mpesa", mpesa);


        usersRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(firebaseAuth.getUid())){
                    usersRefs.child(firebaseAuth.getUid()).updateChildren(billing_address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String finalOrder_key = order_key;
        ordersRef.child(order_key).child("billing_address").updateChildren(billing_address)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    List<ProductModel> cart_list = new Cart(CheckOut.this).getAll();
                    ordersRef.child(finalOrder_key).child("productList").setValue(cart_list);
//                    for (ProductModel product: cart_list){
//                        HashMap<String, Object>  productData = new HashMap<>();
//                        productData.put("id",product.getId());
//                        productData.put("name",product.getName());
//                        productData.put("description",product.getDescription());
//                        productData.put("type",product.getType());
//                        productData.put("category",product.getCategory());
//                        productData.put("price",product.getPrice());
//                        productData.put("image",product.getImage());
//                        ordersRef.child(finalOrder_key).child("productList").updateChildren(productData);
//                    }
                }
            }
        });
    }
}
