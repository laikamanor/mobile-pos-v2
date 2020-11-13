package com.example.atlanticbakery;

import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APITransfer extends AppCompatActivity {
    private OkHttpClient client;
    String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i_transfer);

        client = new OkHttpClient();
        getWebService();
    }

    public void getWebService(){
        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", "gord");
            jsonObject.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("http://192.168.11.8:5000/api/get_token")
                .method("get",body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Toast.makeText(getBaseContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                token_class token_class = gson.fromJson(response.body().string(), token_class.class);
                String tok = token_class.getToken();
                // create your json here
                JSONObject jsonObject = new JSONObject();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    JSONObject objectHeaders = new JSONObject();
                    objectHeaders.put("transtype", "MNL");
                    objectHeaders.put("transfer_id", null);
                    objectHeaders.put("sap_number", null);
                    objectHeaders.put("transdate", currentDateandTime);
                    objectHeaders.put("remarks", "done");
                    objectHeaders.put("reference2",null);
                    objectHeaders.put("supplier", null);

                    jsonObject.put("header", objectHeaders);

                    JSONArray arrayDetails = new JSONArray();
                    JSONObject objectDetails = new JSONObject();
                    objectDetails.put("item_code", "BUNNYSAL");
                    objectDetails.put("from_whse", "MTC-FG");
                    objectDetails.put("to_whse", "juan");
                    objectDetails.put("quantity", 50);
                    objectDetails.put("actualrec", 50);
                    objectDetails.put("uom", "pc(s)");
                    arrayDetails.put(objectDetails);
                    jsonObject.put("details", arrayDetails);
                    System.out.println(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://192.168.11.8:5000/api/inv/recv/new")
                        .method("post",body)
                        .addHeader("Authorization", "Bearer " + tok)
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println(response.body().string());
                    }
                });
            }
        });
    }
}