package com.example.xheiksamm.demoapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xheiksamm.demoapp.Interface.ItemClickListener;
import com.example.xheiksamm.demoapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtproductname,txtproductdescription, txtproductprice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(View itemView)
    {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtproductname=(TextView) itemView.findViewById(R.id.product_name);
        txtproductdescription=(TextView) itemView.findViewById(R.id.product_description);
        txtproductprice = (TextView) itemView.findViewById(R.id.product_price);

    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
