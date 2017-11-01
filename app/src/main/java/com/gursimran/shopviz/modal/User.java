package com.gursimran.shopviz.modal;

/**
 * Created by gursimransingh on 31/10/17.
 */

public class User {
    public String UserName;
    public String UserEmail;
    public String UserPassword;
    public String UserPhone;
    public String Gender;

    public User(String userName, String userEmail, String userPassword,String phone, String gender) {

        UserName = userName;
        UserEmail = userEmail;
        UserPassword = userPassword;
        UserPhone = phone;
        Gender = gender;
    }
    public User(){

    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
