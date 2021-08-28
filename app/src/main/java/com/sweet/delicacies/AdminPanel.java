package com.sweet.delicacies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sweet.delicacies.models.Product;
import com.squareup.picasso.Picasso;

public class AdminPanel extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Product, ViewHolder> firebaseRecyclerAdapter;
    private LinearLayout no_data_view;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        no_data_view = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.admin_products_list);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

        Query queryDB = databaseReference;
        queryDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    no_data_view.setVisibility(View.GONE);
                }else{
                    Log.d("<>>>>>>>>>>", "NO Data found");
                    no_data_view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("<>>>>>>>>>>", error.getMessage());
            }
        });

        FirebaseRecyclerOptions<Product> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(queryDB, Product.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ViewHolder>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_model, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Product model) {
                Log.d("<>>>>>>>>>","product bound");
                holder.setProduct(model);
                holder.card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminPanel.this, AdminAddProduct.class);
                        intent.putExtra("product", model);
                        startActivity(intent);
                    }
                });
                holder.imvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProduct(model);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void deleteProduct(final Product model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Product will be deleted permanently");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(model.getId())){
                            databaseReference.child(model.getId()).removeValue();
                            hideAlert();
                            Toast.makeText(AdminPanel.this,"Product deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AdminPanel.this,"Product not deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideAlert();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void hideAlert() {
        if (alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_add_product:
                startActivity(new Intent(AdminPanel.this, AdminAddProduct.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View mView;
        public TextView tvName,tvPrice, tvDescription;
        public ImageView imvImage, imvEdit, imvDelete;
        public CardView card_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            tvName = itemView.findViewById(R.id.product_name);
            tvPrice = itemView.findViewById(R.id.product_price);
            tvDescription = itemView.findViewById(R.id.product_description);
            imvImage = itemView.findViewById(R.id.product_image);
            imvEdit = itemView.findViewById(R.id.imv_cart_btn);
            imvDelete = itemView.findViewById(R.id.btn_delete_product);
            card_view = itemView.findViewById(R.id.item_card_view);
        }

        public void setProduct(Product product) {
            tvName.setText(product.getName());
            tvPrice.setText("$ "+product.getPrice());
            tvDescription.setText(product.getDescription());
            Picasso.get()
                    .load(product.getImage())
                    .into(imvImage);
        }
    }
}
