package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class APIReceived extends AppCompatActivity {

    private RequestQueue mQueue;
    ProgressBar progressBar;
    Button btnDone,btnSearch;
    TextView lblSapNumber,lblFromBranch,lblSelectedType;
    AutoCompleteTextView txtSearch;
    Spinner spinner;

    String title, hidden_title;

    DatabaseHelper4 myDb4;
    DatabaseHelper3 myDb3;
    DatabaseHelper myDb;

    DecimalFormat df = new DecimalFormat("#,###");

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    long mLastClickTime = 0;
    private OkHttpClient client;

    Menu menu;

//    JSONObject globalJsonObject;

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
        lblSapNumber = findViewById(R.id.lblSapNumber);
        lblFromBranch = findViewById(R.id.lblFromBranch);
        lblSelectedType = findViewById(R.id.lblSelectedType);
        txtSearch = findViewById(R.id.txtSearch);
        spinner = findViewById(R.id.spinner);

        client = new OkHttpClient();

//        globalJsonObject = new JSONObject();

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

        if (myDb3.countItems(hidden_title) > 0 && hidden_title.equals("API Received from SAP")) {
            lblFromBranch.setVisibility(View.VISIBLE);
            lblSapNumber.setVisibility(View.VISIBLE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            loadSelectedSAPNumberItems();

        }else if (myDb3.countItems(hidden_title) <= 0 && hidden_title.equals("API Received from SAP")) {
            List<String> items = Arrays.asList("IT", "PO");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            getItems(0);
        }
        else if (myDb3.countItems(hidden_title) <= 0 && hidden_title.equals("API System Transfer Item")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            getItems(0);
        } else if (myDb3.countItems(hidden_title) <= 0 && hidden_title.equals("API Received Item")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            getItems(0);
        } else if (myDb3.countItems(hidden_title) <= 0 && hidden_title.equals("API Item Request")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            getItems(0);
        } else if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            getItems(0);
        } else if (hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")) {
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            getItems(0);
        } else if (myDb3.countItems(hidden_title) > 0 && hidden_title.equals(("API System Transfer Item"))) {
            lblFromBranch.setVisibility(View.VISIBLE);
            lblSapNumber.setVisibility(View.VISIBLE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            loadSelectedSAPNumberItems();
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDb3.countItems(hidden_title) > 0 && hidden_title.equals("API Received from SAP")) {
                    loadSelectedSAPNumberItems();
                } else {
                    getItems(0);
                }
            }
        });

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    Toast.makeText(getBaseContext(), "PRESSED", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
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

    @SuppressLint("SetTextI18n")
    public void loadSelectedSAPNumberItems() {
        Handler handler = new Handler();
        progressBar.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> {
            GridLayout gridLayout = findViewById(R.id.grid);
            gridLayout.removeAllViews();
            Cursor cursor = myDb3.getAllData(hidden_title);
            if(cursor != null){
                while (cursor.moveToNext()){
                    final int id = cursor.getInt(0);
                    final String sapNumber = cursor.getString(1);
                    final String fromBranch = (hidden_title.equals("API System Transfer Item") ? cursor.getString(8) : cursor.getString(2));
                    final String itemName = cursor.getString(3);
                    final double quantity = cursor.getDouble(4);
                    final boolean isSelected = (cursor.getInt(6) > 0);

                    lblSapNumber.setText("IT#: " + sapNumber);
                    lblFromBranch.setText("Branch: " + fromBranch);

                    CardView cardView = new CardView(APIReceived.this);
                    LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(190, 200);
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
                        if(isSelected){
                            Toast.makeText(getBaseContext(), "This item is selected",Toast.LENGTH_SHORT).show();
                        }else{
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
                    txtItemName.setTextSize(13);
                    txtItemName.setVisibility(View.VISIBLE);

                    TextView txtItemLeft = new TextView(APIReceived.this);
                    txtItemLeft.setLayoutParams(layoutParamsItemLeft);
                    txtItemLeft.setTextSize(10);
                    txtItemLeft.setVisibility(View.VISIBLE);
                    txtItemLeft.setText(df.format(quantity) + " qty.");
                    txtItemLeft.setTextColor(Color.parseColor("#34A853"));

                    if(isSelected){
                        linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                        txtItemName.setTextColor(Color.rgb(250, 250, 250));
                        txtItemLeft.setTextColor(Color.rgb(250, 250, 250));
                    }else{
                        linearLayout.setBackgroundColor(Color.rgb(250, 250, 250));
                        txtItemName.setTextColor(Color.rgb(28, 28, 28));
                        txtItemLeft.setTextColor(Color.parseColor("#34A853"));
                    }
                    cardView.addView(linearLayout);
                    linearLayout.addView(txtItemName);
                    linearLayout.addView(txtItemLeft);
                }
//                txtSearch.setAdapter(fillItems(globalList));
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
                                selectedSapNumber = jsonObject.getString("DocNum");
                                sap_number = jsonObject.getString("DocNum");

                                if(spinner.getSelectedItemPosition() == 0){
                                    fromBranch = jsonObject.getString("FromWhsCod");
                                }else{
                                    fromBranch = supplier;
                                }

                                itemName = jsonObject.getString("Dscription");
                                toBranch = jsonObject.getString("WhsCode");
                                quantity = jsonObject.getDouble("Quantity");
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
                                    Toast.makeText(getBaseContext(), "Error Connection \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void appendData( String sResult) {
        try {
            JSONObject jsonObjectResponse = new JSONObject();
            List<String> listItems = new ArrayList<String>();
            jsonObjectResponse = new JSONObject(sResult);
//            if (!globalJsonObject.toString().equals("{}") && !API_ItemInfo.isSubmit) {
//                jsonObjectResponse = globalJsonObject;
//            } else {
////                jsonObjectReponse = new JSONObject(sResult);
//                globalJsonObject = new JSONObject(sResult);
//                jsonObjectResponse = new JSONObject(sResult);
//            }
//            jsonObjectReponse = new JSONObject(sResult);
//            if (response.isSuccessful()) {
//            JSONObject finalJsonObjectResponse = jsonObjectResponse;
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    Toast.makeText(getBaseContext(), finalJsonObjectResponse.toString(), Toast.LENGTH_SHORT).show();
//                }
//            });

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

                                            stockQuantity = jsonObject1.getDouble("quantity");
                                        } else if (hidden_title.equals("API Pull Out Count") && Integer.parseInt(isManager) <= 0) {
                                            stockQuantity = jsonObject1.getDouble("quantity");
                                        }

                                        if(Integer.parseInt(isManager) > 0 && hidden_title.equals("API Inventory Count")){
                                            store_quantity = jsonObject1.getInt("sales_count");
                                            auditor_quantity = jsonObject1.getInt("auditor_count");
                                            variance_quantity = jsonObject1.getInt("variance");
                                        }

                                        if(Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")){
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
                                        item = jsonObject1.getString("DocNum");
                                        docEntry1 = jsonObject1.getInt("DocEntry");
                                        break;
                                }
                                listItems.add(item);
                                String supplier = "";
                                if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 1) {
                                    supplier = jsonObject1.getString("CardCode");
                                }
                                uiItems(item, price, stockQuantity, docEntry1, supplier,store_quantity,auditor_quantity, variance_quantity);
                            }
                        } catch (Exception ex) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ex.printStackTrace();
                                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
             else {
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
                    Toast.makeText(getBaseContext(), "Front-end Error: \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    public void uiItems(String item, Double price, Double stockQuantity, int docEntry1, String supplier, int store_quantity, int auditor_quantity, int variance_quantity){
            GridLayout gridLayout = findViewById(R.id.grid);
            CardView cardView = new CardView(getBaseContext());
            LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(190, 200);
            layoutParamsCv.setMargins(20, 10, 10, 10);
            cardView.setLayoutParams(layoutParamsCv);
            cardView.setRadius(12);
            cardView.setCardElevation(5);

            cardView.setVisibility(View.VISIBLE);
            gridLayout.addView(cardView);
            final LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setBackgroundColor(Color.rgb(255,255,255));
            LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
            linearLayout.setLayoutParams(layoutParamsLinear);
            linearLayout.setTag(item);

            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
            linearLayout.setVisibility(View.VISIBLE);

            String finalItem = item;
            int finalDocEntry = docEntry1;
            double finalPrice = price;
            double finalStockQuantity = stockQuantity;
            String finalSupplier = supplier;
            linearLayout.setOnClickListener(view -> {
                if (hidden_title.equals("API Menu Items") && finalStockQuantity <= 0) {
                    Toast.makeText(getBaseContext(), "This item is not available", Toast.LENGTH_SHORT).show();
                } else if (hidden_title.equals("API Menu Items")) {
                    if (myDb.checkItem(item)) {
                        Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                    }

                } else if (hidden_title.equals("API Received Item")) {
                    if (myDb4.checkItem(item, title)) {
                        Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                    }

                } else if (hidden_title.equals("API Transfer Item")) {
                    if (myDb4.checkItem(item, title)) {
                        Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                    }

                } else if (hidden_title.equals("API Transfer Item")) {
                    if (finalStockQuantity <= 0) {
                        Toast.makeText(getBaseContext(), "This item is not available", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                    }
                } else if (hidden_title.equals("API Item Request")) {
                    if (myDb4.checkItem(item, title)) {
                        Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                    }

                } else if (hidden_title.equals("API Inventory Count")) {
                    if (myDb3.checkItem(item, hidden_title)) {
                        Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                    }
                }
                else if (hidden_title.equals("API Pull Out Count")) {
                    if (myDb3.checkItem(item, hidden_title)) {
                        Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                    }
                }
                else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier,stockQuantity,store_quantity,auditor_quantity,variance_quantity);
                }
            });

            cardView.addView(linearLayout);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(20, 0, 20, 0);
            LinearLayout.LayoutParams layoutParamsItemLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsItemLeft.setMargins(20, -50, 0, 10);

            TextView txtItemName = new TextView(getBaseContext());
            txtItemName.setText(item);
            txtItemName.setTextColor(Color.rgb(0,0,0));
            txtItemName.setLayoutParams(layoutParams);
            txtItemName.setTextSize(13);
            txtItemName.setVisibility(View.VISIBLE);
            linearLayout.addView(txtItemName);

            if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")) {
                TextView txtItemLeft = new TextView(getBaseContext());
                txtItemLeft.setLayoutParams(layoutParamsItemLeft);
                txtItemLeft.setTextColor(Color.rgb(0,0,0));
                txtItemLeft.setTextSize(10);
                txtItemLeft.setVisibility(View.VISIBLE);
                if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item")) {
                    txtItemLeft.setText(df.format(stockQuantity) + " available");
                    if (stockQuantity == 0) {
                        txtItemLeft.setTextColor(Color.rgb(252,28,28));
                    } else if (stockQuantity <= 10) {
                        txtItemLeft.setTextColor(Color.rgb(247, 154, 22));
                    } else if (stockQuantity > 11) {
                        txtItemLeft.setTextColor(Color.rgb(30, 203, 6));
                    }
                }

                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String isManager = sharedPreferences2.getString("isManager", "");
                if(Integer.parseInt(isManager) > 0 && hidden_title.equals("API Inventory Count")){
                    txtItemLeft.setText(df.format(variance_quantity) + " variance");
                    if(variance_quantity < 0){
                        txtItemLeft.setTextColor(Color.rgb(252,28,28));
                    }else{
                        txtItemLeft.setTextColor(Color.rgb(6, 188, 212));
                    }
                }
                if(Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")){
                    txtItemLeft.setText(df.format(variance_quantity) + " variance");
                    if(variance_quantity < 0){
                        txtItemLeft.setTextColor(Color.rgb(252,28,28));
                    }else{
                        txtItemLeft.setTextColor(Color.rgb(6, 188, 212));
                    }
                }

                if(stockQuantity <= 0  && hidden_title.equals("API Inventory Count")){
                    linearLayout.setBackgroundColor(Color.rgb(94, 94, 94));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(255,255,255));
                }
                if(stockQuantity <= 0  && hidden_title.equals("API Pull Out Count") && Integer.parseInt(isManager) <= 0){
                    linearLayout.setBackgroundColor(Color.rgb(94, 94, 94));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(255,255,255));
                }

                if (myDb4.checkItem(item, title) && hidden_title.equals("API Received Item")) {
                    linearLayout.setBackgroundColor(Color.rgb(252,28,28));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(255,255,255));
                } else if (hidden_title.equals("API Transfer Item") && myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252,28,28));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(255,255,255));
                } else if (hidden_title.equals("API Item Request") && myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252,28,28));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(252,28,28));
                } else if (hidden_title.equals("API Menu Items") && myDb.checkItem(item)) {
                    linearLayout.setBackgroundColor(Color.rgb(252,28,28));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(255,255,255));
                }else if(hidden_title.equals("API Inventory Count") && myDb3.checkItem(item, hidden_title)){
                    linearLayout.setBackgroundColor(Color.rgb(252,28,28));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(255,255,255));
                }else if(hidden_title.equals("API Pull Out Count") && myDb3.checkItem(item, hidden_title)){
                    linearLayout.setBackgroundColor(Color.rgb(252,28,28));
                    txtItemName.setTextColor(Color.rgb(255,255,255));
                    txtItemLeft.setTextColor(Color.rgb(255,255,255));
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

    public void navigateDone(){
        Intent intent;
        intent = new Intent(getBaseContext(), (hidden_title.equals("API Menu Items") ? ShoppingCart.class  : API_SelectedItems.class));
        intent.putExtra("title",title);
        intent.putExtra("hiddenTitle",hidden_title);
        startActivity(intent);
    }
}