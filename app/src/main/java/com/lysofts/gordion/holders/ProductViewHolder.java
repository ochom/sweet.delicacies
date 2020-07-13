package com.lysofts.gordion.holders;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lysofts.gordion.ProductDetail;
import com.lysofts.gordion.R;
import com.lysofts.gordion.models.ProductModel;
import com.squareup.picasso.Picasso;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public TextView tvName,tvPrice;
    public ImageView imvImage, imvFavorite, imvAddCart;
    public CardView card_view;

    public ProductViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        tvName = itemView.findViewById(R.id.product_name);
        tvPrice = itemView.findViewById(R.id.product_price);
        imvImage = itemView.findViewById(R.id.product_image);
        imvAddCart = itemView.findViewById(R.id.imv_cart_btn);
        imvFavorite = itemView.findViewById(R.id.imv_favorite_btn);
        card_view = itemView.findViewById(R.id.item_card_view);

    }

    public void setProduct(ProductModel product) {
        tvName.setText(product.getName());
        tvPrice.setText("Ksh. "+product.getPrice());
        Picasso.get()
                .load(product.getImage())
                .into(imvImage);
    }
}
