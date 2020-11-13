package com.example.atlanticbakery;
import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonObject;

public class token_class {
    private boolean success;

    private JsonObject data;

    private String token;

    private String msg;

    private int userID;

    @SerializedName("body")
    private String text;

    public boolean isSuccess() {
        return success;
    }

    public JsonObject getData() {
        return data;
    }

    public String getToken() {
        return token;
    }

    public String getMsg(){
        return msg;
    }

    public String getText() {
        return text;
    }

    public Integer getID(){
        return userID;
    }
}
