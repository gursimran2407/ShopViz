package com.gursimran.shopviz.modal;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gursimran.shopviz.R;

/**
 * Created by gursimransingh on 03/11/17.
 */

public class chat_image extends RecyclerView.ViewHolder {

    public ImageView leftImage;
    public ImageView rightImage;

    public chat_image(View itemView) {
        super(itemView);
        leftImage = itemView.findViewById(R.id.leftImage);
        rightImage = itemView.findViewById(R.id.rightImage);
    }
}
