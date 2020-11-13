package com.example.atlanticbakery;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Base64;
import  android.view.View;
import android.widget.Button;
import  android.widget.EditText;
import  android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import  java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb = new DatabaseHelper(this);
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DatabaseHelper4 myDb4;
    DatabaseHelper5 myDb5;

    int userid;
    String fullName="",whse = "";
    //Declaring layout button,editTexts and progress bar
    Button login;
    EditText username, password;
    //End Declaring layout button,editTexts and progress bar

    long mLastClickTime = 0;
    user_class uc = new user_class();
    private RequestQueue mQueue;

    private OkHttpClient client;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.app_name) + "</font>"));

        mQueue = Volley.newRequestQueue(this);

        myDb2 = new DatabaseHelper2(this);
        myDb3 = new DatabaseHelper3(this);
        myDb4 = new DatabaseHelper4(this);
        myDb5 = new DatabaseHelper5(this);

        client = new OkHttpClient();

        //Getting values from button,editTexts and progress bar
        login = findViewById(R.id.button);
        username = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

//                CheckLogin checkLogin = new CheckLogin();
//                checkLogin.execute("");
                apiLogin();
            }
        });
    }

    public void apiLogin() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(10);
                    } catch (InterruptedException ex) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String us = username.getText().toString();
                            String ps = password.getText().toString();
                            try {
                                // create your json here
//                                JSONObject jsonObject = new JSONObject();
//                                try {
//                                    jsonObject.put("username", us);
//                                    jsonObject.put("password", ps);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//                                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                String IPaddress = sharedPreferences2.getString("IPAddress", "");
                                okhttp3.Request request = new okhttp3.Request.Builder()
                                        .url(IPaddress + "/api/auth/login?username="+ us + "&password=" + ps)
                                        .method("get", null)
                                        .build();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Handler handler = new Handler();
                                                LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
                                                loadingDialog.startLoadingDialog();
                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        synchronized (this) {
                                                            try {
                                                                wait(10);
                                                            } catch (InterruptedException ignored) {
                                                            }
                                                            handler.post(() -> {
                                                                try {
                                                                    if (response.isSuccessful()) {
                                                                        String msg;
                                                                        assert response.body() != null;
                                                                        String result = response.body().string();
                                                                        JSONObject jsonObject1 = new JSONObject(result);

                                                                        if (jsonObject1.getBoolean("success")) {
                                                                            JSONObject jsonObjectData = jsonObject1.getJSONObject("data");
                                                                            userid = jsonObjectData.getInt("id");
                                                                            String resultToken = jsonObject1.getString("token");
                                                                            fullName = jsonObjectData.getString("fullname");
                                                                            whse = jsonObjectData.getString("whse");
                                                                            System.out.println(whse);
                                                                            saveToken(resultToken);
                                                                            saveLoggedIn();
                                                                            msg = "Login Success";
                                                                            openAPIMainMenu();
                                                                            myDb.truncateTable();
                                                                            myDb2.truncateTable();
                                                                            myDb3.truncateTable();
                                                                            myDb4.truncateTable();
                                                                            myDb5.truncateTable();
                                                                        } else {
                                                                            msg = jsonObject1.getString("message");
                                                                        }
                                                                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } catch (Exception ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                            });
                                                        }
                                                        runOnUiThread(loadingDialog::dismissDialog);

                                                    }
                                                };
                                                Thread thread = new Thread(runnable);
                                                thread.start();
                                            }
                                        });
                                    }
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @SuppressLint("StaticFieldLeak")
    public class CheckLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String us = username.getText().toString();
        String ps = password.getText().toString();
//        String zxc = tokenURL + "username=" + us + "&password=" + ps;

        final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);


        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            Integer resultUserID = uc.checkUsernamePassword(MainActivity.this,"username", us, ps);
            if (us.trim().equals("") || ps.trim().equals("")) {
                z = "Please enter Username and Password";
            }else if(resultUserID > 0){
                isSuccess = true;
                z = "Login Success";
                userid = resultUserID;
            }else{
                z = "Invalid Credentials";
            }
            return z;
        }

        @Override
        protected void onPostExecute(final String s) {
            Handler handler = new Handler();
            Runnable r = () -> {
                if(isSuccess){
                    openMainMenu();
                    saveLoggedIn();
                    myDb.truncateTable();
                    myDb2.truncateTable();
                    myDb3.truncateTable();
                    myDb4.truncateTable();
                    myDb5.truncateTable();
                    Toast.makeText(getBaseContext(), z, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismissDialog();
            };
            handler.postDelayed(r, 500);
        }
    }

    public  void saveLoggedIn(){
        String susername = username.getText().toString();
        String spassword = Base64.encodeToString(password.getText().toString() .getBytes(),0);
        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",susername).apply();
        editor.putString("password",spassword).apply();
        editor.putString("fullname",fullName).apply();
        editor.putString("userid",Integer.toString(userid)).apply();
        editor.putString("whse",whse).apply();
//        uc.insetLoginLogs(MainActivity.this, susername);
//        uc.checkCutOff(MainActivity.this, susername);
    }

    public void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token).apply();
    }

    public  void openAPIMainMenu(){
        Intent intent = new Intent(this, API_Nav.class);
        startActivity(intent);
        finish();
    }

    public  void openMainMenu(){
        Intent intent = new Intent(this, Nav.class);
        startActivity(intent);
        finish();
    }
}