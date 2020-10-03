package com.example.atlanticbakery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import  com.android.volley.AuthFailureError;
import  com.android.volley.Request;
import  com.android.volley.RequestQueue;
import  com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class SalesInventory_Received extends AppCompatActivity {
    public static final String URL_WS = "http://192.168.9.245/api/getso";
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_inventory__received);

        mQueue = Volley.newRequestQueue(this);
        jsonParse();

    }

    private void jsonParse() {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_WS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Iterator<String> keys = response.keys();
                    while (keys.hasNext()){
                        String key = keys.next();
                        if(response.get(key) instanceof JSONObject){
                            System.out.println("KEY: " + key + "\n" + response.getString(key));
                        }
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiIsImlhdCI6MTYwMTY4NzkxMCwiZXhwIjoxNjAxNzMxMTEwfQ.eyJ1c2VyX2lkIjo1fQ.ZCDOf9TSg_q19AvNL85RwN4sqEGiV_PEIAMSq8IOhLKVuQ5PxoosIonoPy8UJUzaH6KjoiwhSaTKU-w33YUEHA");
                return params;
            }
        };
        mQueue.add(request);
    }
}