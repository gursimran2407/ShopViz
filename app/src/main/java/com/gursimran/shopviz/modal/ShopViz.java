package com.gursimran.shopviz.modal;

import com.google.firebase.database.FirebaseDatabase;


public class ShopViz extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
