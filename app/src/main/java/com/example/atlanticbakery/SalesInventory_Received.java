package com.example.atlanticbakery;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
    public static final String URL_WS = "http://192.168.11.8:5000/api/user/get_all_users";
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    LinearLayout linearLayout = findViewById(R.id.linear);
                    linearLayout.removeAllViews();
                    JSONArray getArray = response.getJSONArray("data");
                    for(int i = 0; i < getArray.length(); i++){
                        JSONObject objects = getArray.getJSONObject(i);
                        for (int a = 0; a < objects.names().length(); a++){
                            TextView textView = new TextView(getBaseContext());
                            textView.setTextSize(15);
                            textView.setText("KEY: " + objects.names().getString(a) + "\n VALUE: " + objects.get(objects.names().getString(a)) + "\n");
                            linearLayout.addView(textView);
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
                params.put("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiIsImlhdCI6MTYwMjA0MDEyMiwiZXhwIjoxNjAyMDYxNzIyfQ.eyJ1c2VyX2lkIjoxfQ.AqU6quCXyW6tcKji35mNfnTE4rh0KFfGFQHVVJQ99Z_Rhkk48GTQb13LrweU3C56f5B1MqFn_Idfy6d4WYZ3aA");
                return params;
            }
        };
        mQueue.add(request);
    }
}