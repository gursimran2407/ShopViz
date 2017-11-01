package com.gursimran.shopviz.modal;

/**
 * Created by gursimransingh on 31/10/17.
 */

public class User {
    public String UserName;
    public String UserEmail;
    public String UserPassword;
    public String UserPhone;
    public String UserGender;

    public User(String userName, String userEmail, String userPassword,String phone, String gender) {

        UserName = userName;
        UserEmail = userEmail;
        UserPassword = userPassword;
        UserPhone = phone;
        UserGender = gender;

    }
    public User(){

    }

}
