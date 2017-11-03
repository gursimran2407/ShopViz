package com.gursimran.shopviz.modal;


import com.google.firebase.auth.FirebaseAuth;

public class ChatMessage {

    private String msgText;
    private String msgUser;
    private String imgUser;




    public ChatMessage(String msgText, String msgUser){
        this.msgText = msgText;
        this.msgUser = msgUser;


    }

    public ChatMessage(String imgUser) {
        this.imgUser = imgUser;
        this.msgUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public ChatMessage(){

    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public String getImgUser() {
        return imgUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }
}
