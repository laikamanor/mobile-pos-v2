package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class CutOff extends AppCompatActivity {
    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DatabaseHelper myDb;
    Menu menu;

    private OkHttpClient client;
    TextView lblTitle;
    Button btnCutOff;

    String title,hidden_title;
    private long backPressedTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutoff);
        client = new OkHttpClient();

        myDb = new DatabaseHelper(this);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

        menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.usernameLogin);
        nav_shoppingCart.setTitle("Signed In " + fullName);

        int totalCart = myDb.countItems();
        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean result = false;
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_logOut:
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        onBtnLogout();
                        break;
                    case R.id.nav_cutOff:
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        intent = new Intent(getBaseContext(), CutOff.class);
                        intent.putExtra("title", "Cut Off");
                        intent.putExtra("hiddenTitle", "API Cut Off");
                        startActivity(intent);
                        break;
                    case R.id.nav_exploreItems:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Menu Items");
                        intent.putExtra("hiddenTitle", "API Menu Items");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_shoppingCart:
                        result = true;
                        intent = new Intent(getBaseContext(), ShoppingCart.class);
                        intent.putExtra("title", "Shopping Cart");
                        intent.putExtra("hiddenTitle", "API Shopping Cart");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedItem:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Received Item");
                        intent.putExtra("hiddenTitle", "API Received Item");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_transferItem:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Transfer Item");
                        intent.putExtra("hiddenTitle", "API Transfer Item");
                        startActivity(intent);
                        finish();
                        break;
                    case  R.id.nav_receivedSap:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Received from SAP");
                        intent.putExtra("hiddenTitle", "API Received from SAP");
                        startActivity(intent);
                        finish();
                        break;
                    case  R.id.nav_systemTransferItem:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Received from System Transfer Item");
                        intent.putExtra("hiddenTitle", "API System Transfer Item");
                        startActivity(intent);
                        finish();
                        break;
                    case  R.id.nav_itemRequest:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Item Request");
                        intent.putExtra("hiddenTitle", "API Item Request");
                        startActivity(intent);
                        finish();
                        break;
                    case  R.id.nav_InventoryCount:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Inventory Count");
                        intent.putExtra("hiddenTitle", "API Inventory Count");
                        startActivity(intent);
                        finish();
                        break;
                    case  R.id.nav_invConfirmation:
                        result = true;
                        intent = new Intent(getBaseContext(), API_InventoryConfirmation.class);
                        intent.putExtra("title", "Inv. and P.O Count Confirmation");
                        intent.putExtra("hiddenTitle", "API Inventory Count Confirmation");
                        startActivity(intent);
                        finish();
                        break;
                    case  R.id.nav_pullOutCount:
                        result = true;
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Pull Out Request");
                        intent.putExtra("hiddenTitle", "API Pull Out Count");
                        startActivity(intent);
                        finish();
                        break;
                    case  R.id.nav_invLogs:
                        result = true;
                        intent = new Intent(getBaseContext(), API_SalesLogs.class);
                        intent.putExtra("title", "Inventory Logs");
                        intent.putExtra("hiddenTitle", "API Inventory Logs");
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });

        lblTitle = findViewById(R.id.lblTitle);
        btnCutOff = findViewById(R.id.btnCutOff);
        btnCutOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cutOffFunction();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiCutOff();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(CutOff.this);
                        pc.removeToken(CutOff.this);
                        startActivity(uic.goTo(CutOff.this, MainActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void uiCutOff() {
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

                                SharedPreferences sharedPreferences0 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                                String token = sharedPreferences0.getString("token", "");

                                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                String whseCode = sharedPreferences2.getString("whse", "");

                                SharedPreferences sharedPreferences3 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                String IPaddress = sharedPreferences3.getString("IPAddress", "");
                                System.out.println(IPaddress + "/api/whse/get_all?whsecode=" + whseCode);
                                okhttp3.Request request = new okhttp3.Request.Builder()
                                        .url(IPaddress + "/api/whse/get_all?whsecode=" + whseCode)
                                        .addHeader("Authorization", "Bearer " + token)
                                        .addHeader("Content-Type", "application/json")
                                        .method("GET", null)
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
                                        CutOff.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Handler handler = new Handler();
                                                LoadingDialog loadingDialog = new LoadingDialog(CutOff.this);
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
                                                                    assert response.body() != null;
                                                                    String result = response.body().string();
//                                                                                    System.out.println(result);
                                                                    JSONObject jsonObject1 = new JSONObject(result);
                                                                    if (response.isSuccessful()) {


                                                                        if (jsonObject1.getBoolean("success")) {
                                                                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                                                                                int whseID = jsonObjectData.getInt("id");
                                                                                boolean isCutOff = jsonObjectData.getBoolean("cutoff");
                                                                                String cutOffText = isCutOff ? "Enabled" : "Disabled";
                                                                                String cutOffTextReverse = !isCutOff ? "Enabled" : "Disabled";
                                                                                lblTitle.setText("Cut Off Status: " + cutOffText);
                                                                                btnCutOff.setText( cutOffTextReverse+ " Cut Off");
                                                                            }
                                                                        } else {
                                                                            System.out.println(jsonObject1.getString("message"));
                                                                        }

                                                                    } else {
                                                                        System.out.println(jsonObject1.getString("message"));
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
//                        finish();
    }

    public void cutOffFunction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String cutOffTitle = btnCutOff.getText().toString();
        cutOffTitle = cutOffTitle.replace(" Cut Off","");
        builder.setMessage("Are you sure want to " + cutOffTitle + " Cut Off?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

                                                SharedPreferences sharedPreferences0 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                                                String token = sharedPreferences0.getString("token", "");

                                                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                String whseCode = sharedPreferences2.getString("whse", "");

                                                SharedPreferences sharedPreferences3 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                                String IPaddress = sharedPreferences3.getString("IPAddress", "");
                                                System.out.println(IPaddress + "/api/whse/get_all?whsecode=" + whseCode);
                                                okhttp3.Request request = new okhttp3.Request.Builder()
                                                        .url(IPaddress + "/api/whse/get_all?whsecode=" + whseCode)
                                                        .addHeader("Authorization", "Bearer " + token)
                                                        .addHeader("Content-Type", "application/json")
                                                        .method("GET", null)
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
                                                        CutOff.this.runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                Handler handler = new Handler();
                                                                LoadingDialog loadingDialog = new LoadingDialog(CutOff.this);
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
                                                                                    assert response.body() != null;
                                                                                    String result = response.body().string();
//                                                                                    System.out.println(result);
                                                                                    JSONObject jsonObject1 = new JSONObject(result);
                                                                                    if (response.isSuccessful()) {


                                                                                        if (jsonObject1.getBoolean("success")) {
                                                                                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                                JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                                                                                                int whseID = jsonObjectData.getInt("id");
                                                                                                boolean isCutOff = jsonObjectData.getBoolean("cutoff");

                                                                                                String URL2 = IPaddress + "/api/whse/cutoff/" + whseID;
                                                                                                JSONObject jsonObjectHeader = new JSONObject();
                                                                                                jsonObjectHeader.put("cutoff", !isCutOff);
                                                                                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                                                                                RequestBody body = RequestBody.create(JSON, jsonObjectHeader.toString());

                                                                                                okhttp3.Request request = new okhttp3.Request.Builder()
                                                                                                        .url(URL2)
                                                                                                        .method("PUT", body)
                                                                                                        .addHeader("Authorization", "Bearer " + token)
                                                                                                        .addHeader("Content-Type", "application/json")
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
                                                                                                    public void onResponse(Call call, okhttp3.Response response) {
                                                                                                        try {
                                                                                                            String sResult = response.body().string();
                                                                                                            JSONObject jsonObjectResponse = new JSONObject(sResult);
                                                                                                            if(response.isSuccessful()){
                                                                                                                runOnUiThread(new Runnable() {
                                                                                                                    @Override
                                                                                                                    public void run() {
                                                                                                                        try {
                                                                                                                            Toast.makeText(getBaseContext(), jsonObjectResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                                                                                                            uiCutOff();
                                                                                                                        } catch (JSONException e) {
                                                                                                                            e.printStackTrace();
                                                                                                                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                                                                                                        }
                                                                                                                    }
                                                                                                                });

                                                                                                            }else{
                                                                                                                runOnUiThread(new Runnable() {
                                                                                                                    @Override
                                                                                                                    public void run() {
                                                                                                                        try {
                                                                                                                            Toast.makeText(getBaseContext(), jsonObjectResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                                                                                                        } catch (JSONException e) {
                                                                                                                            e.printStackTrace();
                                                                                                                            Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                            }

                                                                                                        } catch (Exception ex) {
                                                                                                            ex.printStackTrace();
                                                                                                            runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }else{
                                                                                            System.out.println(jsonObject1.getString("message"));
                                                                                        }

                                                                                    }else{
                                                                                        System.out.println(jsonObject1.getString("message"));
                                                                                    }
                                                                                } catch (Exception ex) {
                                                                                    ex.printStackTrace();
                                                                                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }
                        };
                        Thread thread = new Thread(runnable);
                        thread.start();
//                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
