package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class API_InventoryConfirmation extends AppCompatActivity {
    TableLayout tableLayout;
    Button btnProceed;
    private OkHttpClient client;

    String title, hidden_title;
    long mLastClickTime = 0;

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    DecimalFormat df = new DecimalFormat("#,###");

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DatabaseHelper myDb;

    Menu menu;
    double poCount = 0;
    String gBranch = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_confirmation);

        myDb = new DatabaseHelper(this);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = new OkHttpClient();

        tableLayout = findViewById(R.id.table_main);
        btnProceed = findViewById(R.id.btnProceed);

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
                    case R.id.nav_cutOff:
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        intent = new Intent(getBaseContext(), CutOff.class);
                        intent.putExtra("title", "Cut Off");
                        intent.putExtra("hiddenTitle", "API Cut Off");
                        startActivity(intent);
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
                    case R.id.nav_uploadOffline:
                        result = true;
                        intent = new Intent(getBaseContext(), OfflineList.class);
                        intent.putExtra("title", "Offline Pending Transactions");
                        intent.putExtra("hiddenTitle", "API Offline List");
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });

        hmReturnBranches();
        hmReturnBranches();

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] poDestination = {""};
                final int[] itr = {0};
                AlertDialog.Builder myDialog = new AlertDialog.Builder(API_InventoryConfirmation.this);
                myDialog.setCancelable(false);

                if(poCount > 0.00){

                    LinearLayout layout = new LinearLayout(getBaseContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(50,50,50,50);

                    LinearLayout.LayoutParams layoutParamsLblWarehouses = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setLayoutParams(layoutParams);

                    TextView lblPullOutDestination = new TextView(getBaseContext());
                    lblPullOutDestination.setTextSize(15);
                    lblPullOutDestination.setText("Pull Out Destination:");
                    layoutParamsLblWarehouses.setMargins(50,20,50,10);
                    lblPullOutDestination.setLayoutParams(layoutParamsLblWarehouses);
                    layout.addView(lblPullOutDestination);

                    SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                    String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                    SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                    String IPaddress = sharedPreferences2.getString("IPAddress", "");

                    Spinner cmbWarehouses = new Spinner(getBaseContext());
                    LinearLayout.LayoutParams layoutParamsCmbWarehouses = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParamsCmbWarehouses.setMargins(50,0,50,0);
                    cmbWarehouses.setLayoutParams(layoutParamsCmbWarehouses);
                    List<String> warehouses = new ArrayList<>();
                    warehouses.add("Select Warehouse");
                    warehouses = returnBranches();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(API_InventoryConfirmation.this, android.R.layout.simple_spinner_item, warehouses);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cmbWarehouses.setAdapter(adapter);
                    cmbWarehouses.setSelection(-1);

                    cmbWarehouses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            poDestination[0] = findWarehouseCode(cmbWarehouses.getSelectedItem().toString());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    layout.addView(cmbWarehouses);

                    TextView lblITRNumber = new TextView(getBaseContext());
                    lblITRNumber.setTextSize(15);
                    lblITRNumber.setText("ITR #:");
                    LinearLayout.LayoutParams layoutParamsLblITR = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsLblITR.setMargins(50,10,50,0);
                    lblITRNumber.setLayoutParams(layoutParamsLblITR);
                    layout.addView(lblITRNumber);

                    EditText txtITRNumber = new EditText(getBaseContext());
                    txtITRNumber.setTextSize(15);
                    txtITRNumber.setHint("Enter ITR (Option)");
                    LinearLayout.LayoutParams layoutParamsTxtITR = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsTxtITR.setMargins(50,0,50,10);
                    txtITRNumber.setLayoutParams(layoutParamsTxtITR);
                    txtITRNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

                    txtITRNumber.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            itr[0] = (txtITRNumber.getText().toString().isEmpty() ? 0 : Integer.parseInt(txtITRNumber.getText().toString()));
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                    layout.addView(txtITRNumber);
                    myDialog.setView(layout);
                }


                myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        try{
                            if(poDestination[0].equals("Select Warehouse")){
                                Toast.makeText(getBaseContext(), "Please select Pull Out Destination", Toast.LENGTH_SHORT).show();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_InventoryConfirmation.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String poD = poDestination[0];
                                                int sap_number = itr[0];
                                                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                                                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));

                                                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                                String IPaddress = sharedPreferences2.getString("IPAddress", "");


                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                                                String currentDateandTime = sdf.format(new Date());

                                                String URL = IPaddress + "/api/inv/count/confirm?transdate=" + currentDateandTime;

                                                JSONObject zxxz = new JSONObject();
                                                try {
                                                    zxxz.put("transdate", currentDateandTime);
                                                    zxxz.put("confirm", true);
                                                    if(sap_number == 0){
                                                        zxxz.put("po_sap",JSONObject.NULL);
                                                    }else{
                                                        zxxz.put("po_sap",sap_number);
                                                    }
                                                    zxxz.put("po_whse", poD);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                                System.out.println("body: " + zxxz);
                                                RequestBody body = RequestBody.create(JSON, zxxz.toString());
                                                okhttp3.Request request = new okhttp3.Request.Builder()
                                                        .url(URL)
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
                                                            JSONObject jsonObjectReponse = new JSONObject(sResult);
                                                            if (jsonObjectReponse.getBoolean("success")) {
                                                                runOnUiThread(new Runnable() {
                                                                    @SuppressLint({"ResourceType", "SetTextI18n"})
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            Toast.makeText(getBaseContext(), jsonObjectReponse.getString("message"), Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(getBaseContext(),"OK", Toast.LENGTH_SHORT).show();
                                                                            loadData();
                                                                        } catch (Exception ex) {
                                                                            runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    ex.printStackTrace();
                                                                                    Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            else {
                                                                String msg = jsonObjectReponse.getString("message");
                                                                if (msg.equals("Token is invalid")) {
                                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(API_InventoryConfirmation.this);
                                                                    builder.setCancelable(false);
                                                                    builder.setMessage("Your session is expired. Please login again.");
                                                                    builder.setPositiveButton("OK", (dialog, which) -> {
                                                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                                            return;
                                                                        }
                                                                        mLastClickTime = SystemClock.elapsedRealtime();
                                                                        pc.loggedOut(API_InventoryConfirmation.this);
                                                                        pc.removeToken(API_InventoryConfirmation.this);
                                                                        startActivity(uic.goTo(API_InventoryConfirmation.this, MainActivity.class));
                                                                        finish();
                                                                        dialog.dismiss();
                                                                    });
                                                                    builder.show();
                                                                } else {
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
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
                        }catch (Exception ex){
                            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                myDialog.setNegativeButton("Cancel", (dialogInterface, i1) -> dialogInterface.dismiss());

                myDialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void hmReturnBranches(){
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        String URL = IPaddress + "/api/whse/get_all";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URL)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    client = new OkHttpClient();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) {
                            try {
//                                System.out.println(response.body().string());
                                String sResult = response.body().string();
                                gBranch = sResult;
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public String findWarehouseCode(String value){
        try{
            JSONObject jsonObjectResponse = new JSONObject(gBranch);
            JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String branch = jsonObject.getString("whsecode") + "," + jsonObject.getString("whsename");
                if(value.contains(jsonObject.getString("whsename"))){
                    return jsonObject.getString("whsecode");
                }
            }
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    public List<String> returnBranches(){
        List<String> result = new ArrayList<>();
        result.add("Select Warehouse");
        System.out.println(gBranch);
        try{
            JSONObject jsonObjectResponse = new JSONObject(gBranch);
            JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String branch = jsonObject.getString("whsecode") + "," + jsonObject.getString("whsename");
                String branch = jsonObject.getString("whsename");
                result.add(branch);
            }
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void loadData() {
        tableLayout.removeAllViews();

        TableRow tableColumn = new TableRow(getBaseContext());
        String[] columns  = new String[]{"Item", "Sys. Inv.", "Good", "PO", "Var."};
        for (String s : columns) {
            TextView lblColumn1 = new TextView(getBaseContext());
            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn1.setText(s);
            lblColumn1.setPadding(10, 0, 10, 0);
            lblColumn1.setTextSize(15);
            tableColumn.addView(lblColumn1);
        }
        tableLayout.addView(tableColumn);

        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        String URL = IPaddress + "/api/inv/count/confirm?transdate=" + currentDate;
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URL)
                .method("GET", null)
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
                    appendData(sResult);
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

    public void appendData( String sResult) {
        try {
            JSONObject jsonObjectReponse;
            jsonObjectReponse = new JSONObject(sResult);
//            if (response.isSuccessful()) {
            if (jsonObjectReponse.getBoolean("success")) {
                JSONArray jsonArray = jsonObjectReponse.getJSONArray("data");
                runOnUiThread(new Runnable() {
                    @SuppressLint({"ResourceType", "SetTextI18n"})
                    @Override
                    public void run() {
                        try {
                            poCount = 0.00;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String item = jsonObject1.getString("item_code");
                                double act_qty = (jsonObject1.isNull("ending_final_count") ? 0.00 : jsonObject1.getDouble("ending_final_count")),
                                        sys_qty = (jsonObject1.isNull("quantity") ? 0.00 : jsonObject1.getDouble("quantity")),
                                        po_qty = (jsonObject1.isNull("po_final_count") ? 0.00 : jsonObject1.getDouble("po_final_count")),
                                        var = (jsonObject1.isNull("variance") ? 0.00 : jsonObject1.getDouble("variance"));
                                poCount += (jsonObject1.isNull("po_final_count") ? 0.00 : jsonObject1.getDouble("po_final_count"));
                                uiItems(item,act_qty,sys_qty, po_qty, var);
                            }
                        } catch (Exception ex) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ex.printStackTrace();
                                    Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
            else {
                String msg = jsonObjectReponse.getString("message");
                if (msg.equals("Token is invalid")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(API_InventoryConfirmation.this);
                    builder.setCancelable(false);
                    builder.setMessage("Your session is expired. Please login again.");
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        pc.loggedOut(API_InventoryConfirmation.this);
                        pc.removeToken(API_InventoryConfirmation.this);
                        startActivity(uic.goTo(API_InventoryConfirmation.this, MainActivity.class));
                        finish();
                        dialog.dismiss();
                    });
                    builder.show();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(API_InventoryConfirmation.this);
                        pc.removeToken(API_InventoryConfirmation.this);
                        startActivity(uic.goTo(API_InventoryConfirmation.this, MainActivity.class));
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

    @SuppressLint("SetTextI18n")
    public void uiItems(String item, double act_qty,double sys_qty, double po_qty,double var){
        final TableRow tableRow = new TableRow(getBaseContext());
        tableRow.setBackgroundColor(Color.WHITE);
        LinearLayout linearLayoutItem = new LinearLayout(this);
        linearLayoutItem.setPadding(10, 10, 10, 10);
        linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
        linearLayoutItem.setBackgroundColor(Color.WHITE);
        linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
        tableRow.addView(linearLayoutItem);

        LinearLayout.LayoutParams layoutParamsItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView lblColumn1 = new TextView(this);
        lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
        lblColumn1.setLayoutParams(layoutParamsItems);
//                       String v = cutWord(item);
        lblColumn1.setText(item);
        lblColumn1.setTextSize(15);
        lblColumn1.setBackgroundColor(Color.WHITE);

        lblColumn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getBaseContext(), item, Toast.LENGTH_SHORT).show();
            }
        });

        linearLayoutItem.addView(lblColumn1);

        TextView lblColumn3 = new TextView(getBaseContext());
        lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
        lblColumn3.setText(df.format(sys_qty));
        lblColumn3.setPadding(10, 0, 10, 0);
        lblColumn3.setTextSize(15);
        tableRow.addView(lblColumn3);

        TextView lblColumn2 = new TextView(getBaseContext());
        lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
        lblColumn2.setText(df.format(act_qty));
        lblColumn2.setPadding(10, 0, 10, 0);
        lblColumn2.setTextSize(15);
        tableRow.addView(lblColumn2);

        TextView lblColumn4 = new TextView(getBaseContext());
        lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
        lblColumn4.setText(df.format(po_qty));
        lblColumn4.setPadding(10, 10, 10, 10);
        lblColumn4.setTextSize(15);
        tableRow.addView(lblColumn4);

        TextView lblColumn5 = new TextView(getBaseContext());
        lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
        lblColumn5.setText(df.format(var));
        lblColumn5.setPadding(10, 10, 10, 10);
        lblColumn5.setTextSize(15);

        if(var == 0){
            lblColumn5.setTextColor(Color.BLACK);
        }else if(var > 0){
            lblColumn5.setTextColor(Color.rgb(6, 188, 212));
        }else if(var < 0){
            lblColumn5.setTextColor(Color.RED);
        }

        tableRow.addView(lblColumn5);

        tableLayout.addView(tableRow);

        View viewLine = new View(this);
        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        viewLine.setLayoutParams(layoutParamsLine);
        viewLine.setBackgroundColor(Color.GRAY);
        tableLayout.addView(viewLine);
    }

    public String cutWord(String value){
        String result;
        int limit = 10;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}
