package com.sweet.delicacies.holders;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sweet.delicacies.FavoritesActivity;
import com.sweet.delicacies.R;
import com.sweet.delicacies.models.Product;
import com.sweet.delicacies.session.Favorites;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private Context context;
    private List<Product> favoritesList;

    public FavoritesAdapter(Context context) {
        this.context = context;
        this.favoritesList = new Favorites(context).getAll();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_product_model, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.product_name.setText(favoritesList.get(position).getName());
        holder.product_price.setText("$ "+favoritesList.get(position).getPrice());
        Picasso.get()
                .load(favoritesList.get(position).getImage())
                .into(holder.product_image_view);


        if (!new Favorites(context).isInFavorites(favoritesList.get(position).getId())){
            holder.add_to_cart.setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
        }else{
            holder.add_to_cart.setImageResource(R.drawable.ic_add_shopping_cart_black_24dp);
        }

        holder.close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        new Favorites(context).delete(favoritesList.get(position).getId());
                        favoritesList.remove(favoritesList.get(position));
                        notifyDataSetChanged();
                        notifyItemRemoved(position);

                        ((FavoritesActivity) context).updateOtherUI();
                    }

                }, 300);
            }
        });

        holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new Favorites(context).isInFavorites(favoritesList.get(position).getId())){
                    new Favorites(context).add(favoritesList.get(position));
                    ((ImageView)v).setImageResource(R.drawable.ic_shopping_cart_theme_24dp);
                }else{
                    new Favorites(context).delete(favoritesList.get(position).getId());
                    ((ImageView)v).setImageResource(R.drawable.ic_add_shopping_cart_black_24dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView product_image_view, close_button, add_to_cart;
        public TextView product_name, product_price;
        public ViewHolder(View v) {
            super(v);
            product_image_view = v.findViewById(R.id.product_image);
            close_button = v.findViewById(R.id.btn_product_remove);
            add_to_cart = v.findViewById(R.id.btn_add_to_cart);
            product_name = v.findViewById(R.id.product_name);
            product_price = v.findViewById(R.id.product_price);
        }
    }
}
