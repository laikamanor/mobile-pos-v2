package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIReceived extends AppCompatActivity {

    private RequestQueue mQueue;
    ProgressBar progressBar;
    Button btnDone,btnSearch;
    TextView lblSapNumber,lblFromBranch,lblSelectedType,lblType;
    AutoCompleteTextView txtSearch;
    Spinner spinner,spinnerType;
    Button btnRemove;

    String title, hidden_title;

    DatabaseHelper4 myDb4;
    DatabaseHelper3 myDb3;
    DatabaseHelper myDb;
    DatabaseHelper8 myDb8;
    DatabaseHelper7 myDb7;

    DecimalFormat df = new DecimalFormat("#,###");

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    long mLastClickTime = 0;
    private OkHttpClient client;

    Menu menu;

    JSONObject globalJsonObject;
    Button btnBack,btnRefresh;
    private long backPressedTime;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i_received);
        mQueue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progWait);
        btnDone = findViewById(R.id.btnDone);
        btnSearch = findViewById(R.id.btnSearch);
        myDb4 = new DatabaseHelper4(this);
        myDb3 = new DatabaseHelper3(this);
        myDb = new DatabaseHelper(this);
        myDb8 = new DatabaseHelper8(this);
        myDb7 = new DatabaseHelper7(this);
        lblSapNumber = findViewById(R.id.lblSapNumber);
        lblFromBranch = findViewById(R.id.lblFromBranch);
        lblSelectedType = findViewById(R.id.lblSelectedType);
        lblType = findViewById(R.id.lblType);
        txtSearch = findViewById(R.id.txtSearch);
        btnBack = findViewById(R.id.btnBack);
        spinner = findViewById(R.id.spinner);
        spinnerType = findViewById(R.id.spinnerType);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setVisibility(View.GONE);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSearch.setText("");
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txtSearch.getText().toString().isEmpty()){
                    btnRemove.setVisibility(View.GONE);
                }else{
                    btnRemove.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        client = new OkHttpClient();

        globalJsonObject = new JSONObject();

        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

        menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.usernameLogin);
        nav_shoppingCart.setTitle("Signed In " + fullName);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            boolean result = false;
            Intent intent;
            switch (menuItem.getItemId()) {
                case R.id.nav_logOut:
                    result = true;
                    drawerLayout.closeDrawer(Gravity.START, false);
                    onBtnLogout();
                    break;
                case R.id.nav_changePassword:
                    result = true;
                    drawerLayout.closeDrawer(Gravity.START, false);
                    changePassword();
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
                case R.id.nav_receivedSap:
                    result = true;
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received from SAP");
                    intent.putExtra("hiddenTitle", "API Received from SAP");
                    startActivity(intent);
                    finish();
                    break;
                case R.id.nav_systemTransferItem:
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
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                getItems(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadData();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalJsonObject = new JSONObject();
                loadData();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb3.truncateTable();
                Intent intent;
                intent = new Intent(getBaseContext(), APIReceived.class);
                intent.putExtra("title", title);
                intent.putExtra("hiddenTitle", hidden_title);
                startActivity(intent);
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
                globalJsonObject = new JSONObject();
                loadData();
            }
        });
    }

    public void changePassword(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(APIReceived.this);
        myDialog.setCancelable(false);
        myDialog.setMessage("*Enter Your New Password");
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setPadding(40, 0, 40, 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,20);
        EditText txtPassword = new EditText(getBaseContext());
        txtPassword.setTextSize(15);
        txtPassword.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtPassword.setTransformationMethod(new PasswordTransformationMethod());
        txtPassword.setLayoutParams(layoutParams);
        layout.addView(txtPassword);

        CheckBox checkPassword = new CheckBox(getBaseContext());
        checkPassword.setText("Show Password");
        checkPassword.setTextSize(15);
        checkPassword.setGravity(View.TEXT_ALIGNMENT_CENTER);
        checkPassword.setLayoutParams(layoutParams);

        checkPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    txtPassword.setTransformationMethod(null);
                }else{
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
                txtPassword.setSelection(txtPassword.length());
            }
        });

        layout.addView(checkPassword);

        myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(txtPassword.getText().toString().trim().isEmpty()){
                    Toast.makeText(getBaseContext(), "Password field is required", Toast.LENGTH_SHORT).show();
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                    builder.setMessage("Are you sure want to submit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    APIReceived.myChangePassword myChangePassword = new APIReceived.myChangePassword(txtPassword.getText().toString().trim());
                                    myChangePassword.execute();
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
        });

        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        myDialog.setView(layout);
        myDialog.show();
    }

    private class myChangePassword extends AsyncTask<String, Void, String> {
        String password = "";
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        public myChangePassword(String sPassword) {
            password = sPassword;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPAddress = sharedPreferences2.getString("IPAddress", "");

                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("password", password);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPAddress + "/api/user/change_pass")
                        .method("PUT", body)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = null;

                response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if(s != null) {
                    JSONObject jsonObjectResponse = new JSONObject(s);
                    loadingDialog.dismissDialog();
                    Toast.makeText(getBaseContext(), jsonObjectResponse.getString("message"), Toast.LENGTH_SHORT).show();

                    if(jsonObjectResponse.getBoolean("success")){

                        AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                        builder.setMessage("We redirect you to Login Page")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pc.loggedOut(APIReceived.this);
                                        pc.removeToken(APIReceived.this);
                                        startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                });
            }
        }
    }


    public void loadData() {
        if (hidden_title.equals("API Received from SAP")) {
            if (myDb3.countItems(hidden_title) > 0) {
                lblFromBranch.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                lblSapNumber.setVisibility(View.VISIBLE);
                lblSelectedType.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                lblType.setVisibility(View.GONE);
                spinnerType.setVisibility(View.GONE);
                loadSelectedSAPNumberItems();
            } else {
                List<String> items = Arrays.asList("IT", "PO");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                lblFromBranch.setVisibility(View.GONE);
                lblSapNumber.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                lblSelectedType.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                lblType.setVisibility(View.GONE);
                spinnerType.setVisibility(View.GONE);
                getItems(0);
            }

        } else if (hidden_title.equals("API System Transfer Item")) {
            if(myDb3.countItems(hidden_title) <= 0){
                lblFromBranch.setVisibility(View.GONE);
                lblSapNumber.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                lblSelectedType.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                lblType.setVisibility(View.GONE);
                spinnerType.setVisibility(View.GONE);
                getItems(0);
            }else {
                lblFromBranch.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                lblSapNumber.setVisibility(View.VISIBLE);
                lblSelectedType.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                lblType.setVisibility(View.GONE);
                spinnerType.setVisibility(View.GONE);
                loadSelectedSAPNumberItems();
            }
        } else if (hidden_title.equals("API Received Item")) {
            if (myDb3.countItems(hidden_title) <= 0) {
                lblFromBranch.setVisibility(View.GONE);
                lblSapNumber.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                lblType.setVisibility(View.VISIBLE);
                spinnerType.setVisibility(View.VISIBLE);
                List<String> items = Arrays.asList("Select Type","SAPIT", "SAPPO");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(APIReceived.this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerType.setAdapter(adapter);
                lblSelectedType.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                getItems(0);
            }
        }
        else if (hidden_title.equals("API Transfer Item")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            lblType.setVisibility(View.GONE);
            spinnerType.setVisibility(View.GONE);
            getItems(0);
        }
        else if (hidden_title.equals("API Item Request")) {
            if (myDb3.countItems(hidden_title) <= 0) {
                lblFromBranch.setVisibility(View.GONE);
                lblSapNumber.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                lblSelectedType.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                lblType.setVisibility(View.GONE);
                spinnerType.setVisibility(View.GONE);
                getItems(0);
            }
        } else if (hidden_title.equals("API Menu Items")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            lblType.setVisibility(View.GONE);
            spinnerType.setVisibility(View.GONE);
            getItems(0);
        } else if (hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            lblType.setVisibility(View.GONE);
            spinnerType.setVisibility(View.GONE);
            getItems(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        Toast.makeText(getBaseContext(), "RECEIVED", Toast.LENGTH_SHORT).show();
        int totalCart = myDb.countItems();
        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");

        if (hidden_title.equals("API Received from SAP")) {
            lblSelectedType.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        }

        globalJsonObject = new JSONObject();
        loadData();
    }


    @Override
    public void onBackPressed() {
//        if(backPressedTime + 2000 > System.currentTimeMillis()){
//            super.onBackPressed();
//            return;
//        }
//        else{
//            Toast.makeText(getBaseContext(), "Press back again to close " + title, Toast.LENGTH_SHORT).show();
//        }
//        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    pc.loggedOut(APIReceived.this);
                    pc.removeToken(APIReceived.this);
                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public ArrayAdapter<String> fillItems(List<String> items){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, items);
    }

    public void uiItems2(int id, String itemName, String sapNumber, double quantity, String fromBranch,boolean isSelected){
        GridLayout gridLayout = findViewById(R.id.grid);
        CardView cardView = new CardView(APIReceived.this);
        LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(300, 300);
        layoutParamsCv.setMargins(20, 10, 10, 10);
        cardView.setLayoutParams(layoutParamsCv);
        cardView.setRadius(12);
        cardView.setCardElevation(5);

        cardView.setVisibility(View.VISIBLE);
        gridLayout.addView(cardView);
        final LinearLayout linearLayout = new LinearLayout(APIReceived.this);
        LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
        linearLayout.setLayoutParams(layoutParamsLinear);
        linearLayout.setTag(id);

        linearLayout.setOnClickListener(view -> {
            if (isSelected) {
                Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent;
                intent = new Intent(getBaseContext(), API_ItemInfo.class);
                intent.putExtra("title", title);
                intent.putExtra("hiddenTitle", hidden_title);
                intent.putExtra("item", itemName);
                intent.putExtra("sapNumber", sapNumber);
                intent.putExtra("quantity", Double.toString(quantity));
                intent.putExtra("fromBranch", fromBranch);
                intent.putExtra("deliveredQuantity", quantity);
                intent.putExtra("id", id);
                startActivity(intent);
//                                loadSelectedSAPNumberItems();
            }
        });
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.setVisibility(View.VISIBLE);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(20, 0, 20, 0);
        LinearLayout.LayoutParams layoutParamsItemLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsItemLeft.setMargins(20, -50, 0, 10);

        TextView txtItemName = new TextView(APIReceived.this);
        String cutWord = cutWord(itemName, 25);
        txtItemName.setText(cutWord);
        txtItemName.setLayoutParams(layoutParams);
        txtItemName.setTextSize(15);
        txtItemName.setVisibility(View.VISIBLE);

        TextView txtItemLeft = new TextView(APIReceived.this);
        txtItemLeft.setLayoutParams(layoutParamsItemLeft);
        txtItemLeft.setTextSize(13);
        txtItemLeft.setVisibility(View.VISIBLE);
        txtItemLeft.setText(df.format(quantity) + " qty.");
        txtItemLeft.setTextColor(Color.parseColor("#34A853"));

        if (isSelected) {
            linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
            txtItemName.setTextColor(Color.rgb(250, 250, 250));
            txtItemLeft.setTextColor(Color.rgb(250, 250, 250));
        } else {
            linearLayout.setBackgroundColor(Color.rgb(250, 250, 250));
            txtItemName.setTextColor(Color.rgb(28, 28, 28));
            txtItemLeft.setTextColor(Color.parseColor("#34A853"));
        }
        cardView.addView(linearLayout);
        linearLayout.addView(txtItemName);
        linearLayout.addView(txtItemLeft);
    }

    @SuppressLint("SetTextI18n")
    public void loadSelectedSAPNumberItems() {
        Handler handler = new Handler();
        progressBar.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> {
            GridLayout gridLayout = findViewById(R.id.grid);
            gridLayout.removeAllViews();
            Cursor cursor = myDb3.getAllData(hidden_title);
            if(cursor != null) {
                List<String> listItems = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    final int id = cursor.getInt(0);
                    final String sapNumber = cursor.getString(1);
                    final String fromBranch = (hidden_title.equals("API System Transfer Item") ? cursor.getString(8) : cursor.getString(2));
                    final String itemName = cursor.getString(3);
                    final double quantity = cursor.getDouble(4);
                    final boolean isSelected = (cursor.getInt(6) > 0);

                    lblSapNumber.setText("IT#: " + sapNumber);
                    lblFromBranch.setText("Branch: " + fromBranch);
                    listItems.add(itemName);

                    if (!txtSearch.getText().toString().trim().isEmpty()) {
                        if (txtSearch.getText().toString().trim().toLowerCase().contains(itemName.toLowerCase())) {
                            uiItems2(id,itemName,sapNumber,quantity,fromBranch,isSelected);
                        }
                    }else{
                        uiItems2(id,itemName,sapNumber,quantity,fromBranch,isSelected);
                    }

                }
                txtSearch.setAdapter(fillItems(listItems));
            }
            cursor.close();
            progressBar.setVisibility(View.GONE);
        },500);
        btnDone.setOnClickListener(view -> navigateDone());
    }

    public void insertSAPItems(Integer docEntry, String supplier){
        String appendURL;
        if(spinner.getSelectedItemPosition() == 0){
            appendURL = "/api/sapb1/itdetails/" + docEntry;
        }else{
            appendURL = "/api/sapb1/podetails/" + docEntry;
        }

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPaddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray jsonArray;
                            jsonArray = response.getJSONArray("data");
                            int countError = 0;
                            String selectedSapNumber = "N/A";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String sap_number,
                                        fromBranch,
                                        itemName,
                                        toBranch;
                                Double quantity;
                                int isSAPIT_int;
                                int baseID;
                                selectedSapNumber = jsonObject.getString("docnum");
                                sap_number = jsonObject.getString("docnum");

                                if(spinner.getSelectedItemPosition() == 0){
                                    fromBranch = jsonObject.getString("fromwhscod");
                                }else{
                                    fromBranch = supplier;
                                }

                                itemName = jsonObject.getString("dscription");
                                toBranch = jsonObject.getString("whscode");
                                quantity = jsonObject.getDouble("quantity");
                                isSAPIT_int = (supplier.equals("") ? 1 : 0);
                                baseID = 0;
                                boolean isSuccess = myDb3.insertData(sap_number, fromBranch, itemName, quantity, 0, isSAPIT_int, toBranch, baseID,hidden_title,0);
                                if (!isSuccess) {
                                    countError += 1;
                                }
                            }

                            if (countError <= 0) {
                                Toast.makeText(APIReceived.this, "'" + selectedSapNumber + "' added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(), APIReceived.class);
                                intent.putExtra("title", title);
                                intent.putExtra("hiddenTitle", hidden_title);
                                startActivity(intent);
                                finish();
//                                Intent intent = new Intent(getBaseContext(), ItemReceivable.class);
//                                intent.putExtra("title", title);
//                                intent.putExtra("hiddenTitle", hidden_title);
//                                startActivity(intent);
//                                finish();
                            } else {
                                Toast.makeText(APIReceived.this, "'" + docEntry + "' not added", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }

    public void insertSystemTransfer(Integer id,String referenceNumber){
        String appendURL= "/api/inv/trfr/getdetails/" + id;

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPaddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject jsonObjectData = response.getJSONObject("data");
                            JSONArray jsonArray;
                            jsonArray = jsonObjectData.getJSONArray("transrow");
                            int countError = 0;
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        toBranch;
                                Double quantity;

                                fromBranch = jsonObject.getString("from_whse");
                                itemName = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("quantity");
                                toBranch = jsonObject.getString("to_whse");
                                int int_quantity = jsonObject.getInt("quantity");
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0);
                                if (!isSuccess) {
                                    countError += 1;
                                }
                            }

                            if (countError <= 0) {
                                Toast.makeText(APIReceived.this, "'" + selectedSapNumber + "' added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(), APIReceived.class);
                                intent.putExtra("title", title);
                                intent.putExtra("hiddenTitle", hidden_title);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(APIReceived.this, "'" + id + "' not added", Toast.LENGTH_SHORT).show();
                        }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }

    public void getItems(int docEntry) {
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        progressBar.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(10);
                    } catch (InterruptedException ex) {
                    }
                    String appendURL = "";
                    if (docEntry > 0) {
                        appendURL = "/api/sapb1/itdetails/" + docEntry;
                    } else if (hidden_title.equals("API Received Item")) {
                        appendURL = "/api/item/getall";
                    } else if (hidden_title.equals("API Item Request")) {
                        appendURL = "/api/item/getall";
                    } else if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item")) {
                        appendURL = "/api/inv/whseinv/getall";
                    } else if (hidden_title.equals("API System Transfer Item")) {
                        appendURL = "/api/inv/trfr/forrec";
                    } else if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 0) {
//                        globalJsonObject = new JSONObject();
                        appendURL = "/api/sapb1/getit";
                    } else if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 1) {
//                        globalJsonObject = new JSONObject();
                        appendURL = "/api/sapb1/getpo";
                    } else if (hidden_title.equals("API Inventory Count")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
                        String currentDate = sdf.format(new Date());
                        appendURL = "/api/inv/count/create?date=" + currentDate;
                    } else if (hidden_title.equals("API Pull Out Count")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String currentDate = sdf.format(new Date());
                        String isInvCount = (hidden_title.equals("API Inventory Count") ? "inv/count" : "pulloutreq");
                        appendURL = "/api/" + isInvCount + "/create?date=" + currentDate;
                    }

                    SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                    String IPaddress = sharedPreferences2.getString("IPAddress", "");

                    String URL = IPaddress + appendURL;
//                    System.out.println(URL);
                    if (globalJsonObject.toString().equals("{}")) {
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
                                        e.printStackTrace();
                                        if(hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Menu Items")){

                                        }else{
                                            Toast.makeText(getBaseContext(), "Error Connection \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

//                                        Toast.makeText(getBaseContext(), "Error Connection" + (hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Menu Items") ? "\n" + e.getMessage() + "\n" + "The data is from Resources" : "\n" + e.getMessage()) , Toast.LENGTH_SHORT).show();

                                        if(hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") ){
                                            loadOffline("Stock");
                                        }else if(hidden_title.equals("API Received Item")|| hidden_title.equals("API Item Request")){
                                            loadOffline("Item");

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, okhttp3.Response response) {
                                try {
//                                System.out.println(response.body().string());
                                    String sResult = response.body().string();
//                                    System.out.println(sResult);
                                    MyAppendData myAppendData = new MyAppendData(sResult);
                                    myAppendData.execute("");
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
                    }else{
                        MyAppendData myAppendData = new MyAppendData(globalJsonObject.toString());
                        myAppendData.execute("");
                    }
                }
            runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                navigateDone();
            }
        });
    }

    public void loadOffline(String fromModule){
        Cursor cursor = myDb8.getAllData();
        while (cursor.moveToNext()){
            String module = cursor.getString(3);
            if(module.contains(fromModule)){
                try {
                    globalJsonObject = new JSONObject(cursor.getString(4));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                MyAppendData myAppendData = new MyAppendData(cursor.getString(4));
                myAppendData.execute("");
            }
        }
    }

    private class MyAppendData extends AsyncTask<String, Void, String> {
        String sResult = "";
        public MyAppendData(String result){
            sResult = result;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @SuppressLint({"ResourceType", "SetTextI18n"})
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {
            return sResult;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObjectResponse = new JSONObject();
                List<String> listItems = new ArrayList<String>();
                if (!globalJsonObject.toString().equals("{}") && !API_ItemInfo.isSubmit) {
                    jsonObjectResponse = globalJsonObject;
                } else {
//                jsonObjectReponse = new JSONObject(sResult);
                    globalJsonObject = new JSONObject(s);
                    jsonObjectResponse = new JSONObject(s);
                }

                if (jsonObjectResponse.getBoolean("success")) {
                    JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
                    runOnUiThread(new Runnable() {
                        @SuppressLint({"ResourceType", "SetTextI18n"})
                        @Override
                        public void run() {
                            try {
                                GridLayout gridLayout = findViewById(R.id.grid);
                                gridLayout.removeAllViews();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String item = "";
                                    double price = 0.00;
                                    double stockQuantity = 0.00;
                                    int docEntry1 = 0;
                                    int store_quantity = 0, auditor_quantity = 0, variance_quantity = 0;

                                    switch (hidden_title) {
                                        case "API Item Request":
                                            item = jsonObject1.getString("item_name");
                                            break;
                                        case "API Menu Items":
                                        case "API Transfer Item":
                                        case "API Inventory Count":
                                        case "API Pull Out Count":
                                        case "API Received Item":
                                            SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                            String isManager = sharedPreferences2.getString("isManager", "");
                                            item = jsonObject1.getString("item_code");
//                                    JSONObject jsonObjectItem = jsonObject1.getJSONObject("item");
                                            if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item")) {
                                                price = jsonObject1.getDouble("price");
                                            }
                                            if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Inventory Count")) {

                                                stockQuantity = jsonObject1.isNull("quantity") ? 0.00 : jsonObject1.getDouble("quantity");
                                            } else if (hidden_title.equals("API Pull Out Count") && Integer.parseInt(isManager) <= 0) {
                                                stockQuantity = jsonObject1.getDouble("quantity");
                                            }

                                            if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Inventory Count")) {
                                                store_quantity = jsonObject1.getInt("sales_count");
                                                auditor_quantity = jsonObject1.getInt("auditor_count");
                                                variance_quantity = jsonObject1.getInt("variance");
                                            }

                                            if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")) {
                                                store_quantity = jsonObject1.getInt("sales_count");
                                                auditor_quantity = jsonObject1.getInt("auditor_count");
                                                variance_quantity = jsonObject1.getInt("variance");
                                            }

                                            break;
                                        case "API System Transfer Item":
                                            item = jsonObject1.getString("reference");
                                            docEntry1 = jsonObject1.getInt("id");
                                            break;
                                        default:
                                            item = jsonObject1.getString("docnum");
                                            docEntry1 = jsonObject1.getInt("docentry");
                                            break;
                                    }
                                    listItems.add(item);
                                    String supplier = "";
                                    if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 1) {
                                        supplier = jsonObject1.getString("cardcode");
                                    }
                                    if(hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request")){
                                        stockQuantity -= myDb7.getDecreaseQuantity(item);
                                        stockQuantity += myDb7.getIncreaseQuantity(item);
                                    }
                                    if (!txtSearch.getText().toString().trim().isEmpty()) {
                                        if (txtSearch.getText().toString().trim().contains(item)) {
                                            uiItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity);
                                        }
                                    }else{
                                        uiItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity);
                                    }
                                }
                            } catch (Exception ex) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        ex.printStackTrace();
                                        Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    String msg = jsonObjectResponse.getString("message");
                    if (msg.equals("Token is invalid")) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                        builder.setCancelable(false);
                        builder.setMessage("Your session is expired. Please login again.");
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            pc.loggedOut(APIReceived.this);
                            pc.removeToken(APIReceived.this);
                            startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                            finish();
                            dialog.dismiss();
                        });
                        builder.show();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtSearch.setAdapter(fillItems(listItems));
                    }
                });
            } catch (JSONException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Front-end Error: \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                });
            }

            runOnUiThread(new Runnable() {
                @SuppressLint({"ResourceType", "SetTextI18n"})
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private int getWidthResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return width;
    }

    @SuppressLint("SetTextI18n")
    public void uiItems(String item, Double price, Double stockQuantity, int docEntry1, String supplier, int store_quantity, int auditor_quantity, int variance_quantity){
                GridLayout gridLayout = findViewById(R.id.grid);
//                gridLayout.setColumnCount(3);
                CardView cardView = new CardView(getBaseContext());
                LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(300, 300);
                layoutParamsCv.setMargins(20, 10, 10, 10);
                cardView.setLayoutParams(layoutParamsCv);
                cardView.setRadius(12);
                cardView.setCardElevation(5);

                cardView.setVisibility(View.VISIBLE);
                gridLayout.addView(cardView);
                final LinearLayout linearLayout = new LinearLayout(getBaseContext());
                linearLayout.setBackgroundColor(Color.rgb(255, 255, 255));
                LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
                linearLayout.setLayoutParams(layoutParamsLinear);
                linearLayout.setTag("Linear" + item);

                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                linearLayout.setVisibility(View.VISIBLE);

                String finalItem = item;
                int finalDocEntry = docEntry1;
                double finalPrice = price;
                double finalStockQuantity = stockQuantity;
                String finalSupplier = supplier;
                linearLayout.setOnClickListener(view -> {
                     if (hidden_title.equals("API Menu Items")) {
                         if (myDb.checkItem(item)) {
                             Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                         } else {
                             anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity);
                         }

                     }else if (hidden_title.equals("API Received Item")) {
                        if (myDb4.checkItem(item, title)) {
                            Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                        } else {
                            anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity);
                        }
                    } else if (hidden_title.equals("API Transfer Item")) {
                         if (myDb4.checkItem(item, title)) {
                             Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                         } else {
                             anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity);
                         }
                    } else if (hidden_title.equals("API Item Request")) {
                        if (myDb4.checkItem(item, title)) {
                            Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                        } else {
                            anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity);
                        }

                    } else if (hidden_title.equals("API Inventory Count")) {
                        if (myDb3.checkItem(item, hidden_title)) {
                            Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                        } else {
                            anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity);
                        }
                    } else if (hidden_title.equals("API Pull Out Count")) {
                        if (myDb3.checkItem(item, hidden_title)) {
                            Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                        } else {
                            anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity);
                        }
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity);
                    }
                });

                cardView.addView(linearLayout);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(20, 0, 20, 0);
                LinearLayout.LayoutParams layoutParamsItemLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParamsItemLeft.setMargins(20, -50, 0, 10);

                TextView txtItemName = new TextView(getBaseContext());
                txtItemName.setTag(item);
                txtItemName.setText(cutWord(item, 35));
                txtItemName.setTextColor(Color.rgb(0, 0, 0));
                txtItemName.setLayoutParams(layoutParams);
                txtItemName.setTextSize(15);
                txtItemName.setVisibility(View.VISIBLE);
                linearLayout.addView(txtItemName);

                if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")) {
                    TextView txtItemLeft = new TextView(getBaseContext());
                    txtItemLeft.setLayoutParams(layoutParamsItemLeft);
                    txtItemLeft.setTextColor(Color.rgb(0, 0, 0));
                    txtItemLeft.setTextSize(13);
                    txtItemLeft.setVisibility(View.VISIBLE);
                    if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item")) {
                        txtItemLeft.setText(df.format(stockQuantity) + " available");
                        if (stockQuantity <= 0) {
                            txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                        } else if (stockQuantity <= 10) {
                            txtItemLeft.setTextColor(Color.rgb(247, 154, 22));
                        } else if (stockQuantity > 11) {
                            txtItemLeft.setTextColor(Color.rgb(30, 203, 6));
                        }
                    }

                    SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                    String isManager = sharedPreferences2.getString("isManager", "");
                    if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Inventory Count")) {
                        txtItemLeft.setText(df.format(variance_quantity) + " variance");
                        if (variance_quantity < 0) {
                            txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                        } else {
                            txtItemLeft.setTextColor(Color.rgb(6, 188, 212));
                        }
                    }
                    if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")) {
                        txtItemLeft.setText(df.format(variance_quantity) + " variance");
                        if (variance_quantity < 0) {
                            txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                        } else {
                            txtItemLeft.setTextColor(Color.rgb(6, 188, 212));
                        }
                    }

                    if (stockQuantity <= 0 && hidden_title.equals("API Inventory Count")) {
                        linearLayout.setBackgroundColor(Color.rgb(94, 94, 94));
                        txtItemName.setTextColor(Color.rgb(255, 255, 255));
                        txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                    }
                    if (stockQuantity <= 0 && hidden_title.equals("API Pull Out Count") && Integer.parseInt(isManager) <= 0) {
                        linearLayout.setBackgroundColor(Color.rgb(94, 94, 94));
                        txtItemName.setTextColor(Color.rgb(255, 255, 255));
                        txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                    }

                    if (hidden_title.equals("API Received Item")) {
                        if(myDb4.checkItem(item, title)){
                            linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                            txtItemName.setTextColor(Color.rgb(255, 255, 255));
                            txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                        }
                    } else if (hidden_title.equals("API Transfer Item")) {
                        if(myDb4.checkItem(item, title)){
                            linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                            txtItemName.setTextColor(Color.rgb(255, 255, 255));
                            txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                        }
                    } else if (hidden_title.equals("API Item Request")) {
                        if(myDb4.checkItem(item, title)){
                            linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                            txtItemName.setTextColor(Color.rgb(255, 255, 255));
                            txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                        }
                    } else if (hidden_title.equals("API Menu Items")) {
                        if(myDb.checkItem(item)){
                            linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                            txtItemName.setTextColor(Color.rgb(255, 255, 255));
                            txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                        }
                    } else if (hidden_title.equals("API Inventory Count")) {
                        if(myDb3.checkItem(item, hidden_title)){
                            linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                            txtItemName.setTextColor(Color.rgb(255, 255, 255));
                            txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                        }
                    } else if (hidden_title.equals("API Pull Out Count")) {
                        if(myDb3.checkItem(item, hidden_title)){
                            linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                            txtItemName.setTextColor(Color.rgb(255, 255, 255));
                            txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                        }
                    }
                    linearLayout.addView(txtItemLeft);
                }
            }

    public void anotherFunction(String finalItem, double finalPrice, Integer finalDocEntry, String finalSupplier,double quantity,int store_quantity, int auditor_quantity, int variance_quantity){
        if (hidden_title.equals("API Received Item") || hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")) {
            Intent intent;
            intent = new Intent(getBaseContext(), API_ItemInfo.class);
            intent.putExtra("title", title);
            intent.putExtra("hiddenTitle", hidden_title);
            intent.putExtra("item", finalItem);
            intent.putExtra("quantity", quantity);
            if (hidden_title.equals("API Menu Items")) {
                intent.putExtra("price", finalPrice);
            }

            SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
            String isManager = sharedPreferences2.getString("isManager", "");
            if(Integer.parseInt(isManager) > 0 && hidden_title.equals("API Inventory Count")){
                intent.putExtra("store_quantity", store_quantity);
                intent.putExtra("auditor_quantity", auditor_quantity);
                intent.putExtra("variance_quantity",variance_quantity);
            }
            if(Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")){
                intent.putExtra("store_quantity", store_quantity);
                intent.putExtra("auditor_quantity", auditor_quantity);
                intent.putExtra("variance_quantity",variance_quantity);
            }
            startActivity(intent);
        } else if (hidden_title.equals("API Received from SAP") || hidden_title.equals("API System Transfer Item")) {
            AlertDialog.Builder myDialog = new AlertDialog.Builder(APIReceived.this);
            myDialog.setCancelable(false);
            myDialog.setTitle("Confirmation");
            myDialog.setMessage("Are you sure you want to select '" + finalItem + "'?");
            myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if(hidden_title.equals("API Received from SAP")){
                        insertSAPItems(finalDocEntry, finalSupplier);
                    }else{
                        insertSystemTransfer(finalDocEntry, finalItem);
                    }
                }
            });
            myDialog.setNegativeButton("No", (dialogInterface, i1) -> dialogInterface.dismiss());
            myDialog.show();
        }
    }

    public String cutWord(String value, int limit){
        String result;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }

    public void navigateDone() {
        if (hidden_title.equals("API Received Item") && spinnerType.getSelectedItem().toString() == "Select Type") {
            Toast.makeText(getBaseContext(), "Please select Type", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent;
        intent = new Intent(getBaseContext(), (hidden_title.equals("API Menu Items") ? ShoppingCart.class : API_SelectedItems.class));
        intent.putExtra("title", title);
        if(hidden_title.equals("API Received Item") && spinnerType.getSelectedItem().toString() != "Select Type" && !spinnerType.getSelectedItem().toString().isEmpty()){
            intent.putExtra("type", spinnerType.getSelectedItem().toString());
        }
        intent.putExtra("hiddenTitle", hidden_title);
        startActivity(intent);
    }
}