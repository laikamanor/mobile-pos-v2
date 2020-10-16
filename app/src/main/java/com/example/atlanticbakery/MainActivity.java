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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import  java.util.Objects;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb = new DatabaseHelper(this);
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DatabaseHelper4 myDb4;
    DatabaseHelper5 myDb5;

    int userid;
    //Declaring layout button,editTexts and progress bar
    Button login;
    EditText username, password;
    //End Declaring layout button,editTexts and progress bar

    long mLastClickTime = 0;
    user_class uc = new user_class();
    public static String tokenURL = R.string.API_URL + "/api/get_token?";
    private RequestQueue mQueue;
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

        //Getting values from button,editTexts and progress bar
        login = findViewById(R.id.button);
        username = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                CheckLogin checkLogin = new CheckLogin();
                checkLogin.execute("");
//                newToken(tokenURL);
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    public class CheckLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            String us = username.getText().toString();
            String ps = password.getText().toString();
            tokenURL += "username=" + us + "&password=" + ps;
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tokenURL, null, new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if(success){
                            String newToken = response.getString("token");
                            Toast.makeText(getBaseContext(), newToken,Toast.LENGTH_SHORT).show();
                            saveToken(newToken);
                            saveLoggedIn();
                            isSuccess = true;
                            z = "Login Success";
                        }else{
//                            String msgError = response.getString("message");
//                            Toast.makeText(getBaseContext(), msgError, Toast.LENGTH_SHORT).show();
                            z = response.getString("message");
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
//                if(!url.contains("get_token")){
//                    params.put("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiIsImlhdCI6MTYwMjczMzMxMiwiZXhwIjoxNjAyNzU0OTEyfQ.eyJ1c2VyX2lkIjoyfQ.Svo6UiQ7CtE2DPLbTvDUDGmXHHHLc7ZyYEwEpainGxPHz3JXS3eAc5xIyxit6nA_K3l90HEtnyzeT_gMJMT_Lg");
//                }
                    return params;
                }
            };
            mQueue.add(request);
//            Integer resultUserID = uc.checkUsernamePassword(MainActivity.this,"username", us, ps);
//            if (us.trim().equals("") || ps.trim().equals("")) {
//                z = "Please enter Username and Password";
//            }else if(resultUserID > 0){
//                isSuccess = true;
//                z = "Login Success";
//                userid = resultUserID;
//            }else{
//                z = "Invalid Credentials";
//            }
//            return z;
            return  z;
        }

        @Override
        protected void onPostExecute(final String s) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (isSuccess) {
                        saveLoggedIn();
                        openMainMenu();
                        myDb.truncateTable();
                        myDb2.truncateTable();
                        myDb3.truncateTable();
                        myDb4.truncateTable();
                        myDb5.truncateTable();
                    }
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    loadingDialog.dismissDialog();
                }
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
        editor.putString("userid",Integer.toString(userid)).apply();
        uc.insetLoginLogs(MainActivity.this, susername);
        uc.checkCutOff(MainActivity.this, susername);
    }

    public void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token).apply();
    }

    public  void openMainMenu(){
        Intent intent = new Intent(this, Nav.class);
        startActivity(intent);
        finish();
    }
}