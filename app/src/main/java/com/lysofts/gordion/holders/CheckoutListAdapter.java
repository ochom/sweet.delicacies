package com.lysofts.gordion.holders;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lysofts.gordion.CheckOut;
import com.lysofts.gordion.R;
import com.lysofts.gordion.models.ProductModel;
import com.lysofts.gordion.session.Cart;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckoutListAdapter extends RecyclerView.Adapter<CheckoutListAdapter.ViewHolder> {
    private Context context;
    private List<ProductModel> checkoutList;

    public CheckoutListAdapter(Context context) {
        this.context = context;
        this.checkoutList = new Cart(context).getAll();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkouk_product_model, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.product_name.setText(checkoutList.get(position).getName());
        holder.product_price.setText("Ksh. "+checkoutList.get(position).getPrice());
        Picasso.get()
                .load(checkoutList.get(position).getImage())
                .into(holder.product_image_view);

        holder.close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        new Cart(context).delete(checkoutList.get(position).getId());
                        checkoutList.remove(checkoutList.get(position));
                        notifyDataSetChanged();
                        notifyItemRemoved(position);

                        ((CheckOut) context).updateOtherUI();
                    }

                }, 300);

//                new Cart(context).delete(checkoutList.get(position).getId());
//                checkoutList.remove(checkoutList.get(position));
//                notifyDataSetChanged();
//                notifyItemRemoved(position);
//
//                ((CheckOut) context).updateOtherUI();
            }
        });
    }

    @Override
    public int getItemCount() {
        return checkoutList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView product_image_view, close_button;
        public TextView product_name, product_price;
        public ViewHolder(View v) {
            super(v);
            product_image_view = v.findViewById(R.id.product_image);
            close_button = v.findViewById(R.id.btn_product_remove);
            product_name = v.findViewById(R.id.product_name);
            product_price = v.findViewById(R.id.product_price);
        }
    }
}
