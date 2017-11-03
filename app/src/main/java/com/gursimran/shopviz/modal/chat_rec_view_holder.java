package com.gursimran.shopviz.modal;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gursimran.shopviz.R;


public class chat_rec_view_holder extends RecyclerView.ViewHolder {



    public TextView leftText;
    public TextView rightText;

    public ImageView leftImage;
    public ImageView rightImage;


    public chat_rec_view_holder(View itemView) {
        super(itemView);

        leftText = (TextView)itemView.findViewById(R.id.leftText);
        rightText = (TextView)itemView.findViewById(R.id.rightText);

        // leftImage = itemView.findViewById(R.id.leftImage);
        rightImage = itemView.findViewById(R.id.rightImage);



    }
}
