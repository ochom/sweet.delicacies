package com.sweet.delicacies.holders;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sweet.delicacies.R;
import com.sweet.delicacies.models.Product;
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

    public void setProduct(Product product) {
        tvName.setText(product.getName());
        tvPrice.setText("$ "+product.getPrice());
        Picasso.get()
                .load(product.getImage())
                .into(imvImage);
    }
}
