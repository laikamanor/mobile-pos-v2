package com.example.atlanticbakery;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ShoppingCart extends AppCompatActivity {
    Connection con;
    DatabaseHelper myDb;
    String discountID = "";
    String discountName = "";
    long mLastClickTime = 0;
    private RequestQueue mQueue;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    int userID = 0;

    DecimalFormat df = new DecimalFormat("#,###.00");
    DecimalFormat dfInt = new DecimalFormat("#,###");
    String salesType = "";

    private OkHttpClient client;

    Menu menu;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        myDb = new DatabaseHelper(this);

        client = new OkHttpClient();
        mQueue = Volley.newRequestQueue(this);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

        menu = navigationView.getMenu();
        MenuItem nav_UsernameLogin = menu.findItem(R.id.usernameLogin);
        nav_UsernameLogin.setTitle("Signed In " + fullName);

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
                    case R.id.nav_shoppingCart:
                        result = true;
                        intent = new Intent(getBaseContext(), ShoppingCart.class);
                        intent.putExtra("title", "Shopping Cart");
                        intent.putExtra("hiddenTitle", "API Shopping Cart");
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
                    case R.id.nav_cutOff:
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        intent = new Intent(getBaseContext(), CutOff.class);
                        intent.putExtra("title", "Cut Off");
                        intent.putExtra("hiddenTitle", "API Cut Off");
                        startActivity(intent);
                        break;
                }
                return result;
            }
        });

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @SuppressLint("WrongConstant")
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
////                boolean isStoreExist = ac.isTypeExist(ShoppingCart.this, "Store Count");
////                boolean isAuditorExist = ac.isTypeExist(ShoppingCart.this, "Auditor Count");
////                boolean isFinalExist = ac.isTypeExist(ShoppingCart.this, "Final Count");
//
////                boolean isStorePullOutExist = ac.isTypeExist(ShoppingCart.this, "Store Count Pull Out");
////                boolean isAuditorPullOutExist = ac.isTypeExist(ShoppingCart.this, "Auditor Count Pull Out");
////                boolean isFinalPullOutExist = ac.isTypeExist(ShoppingCart.this, "Final Count Pull Out");
//                boolean result = false;
//                Intent intent;
//                switch (menuItem.getItemId()) {
//                    case R.id.nav_logOut:
//                        result = true;
//                        drawerLayout.closeDrawer(Gravity.START, false);
//                        onBtnLogout();
//                        break;
//                    case R.id.nav_scanItem:
//                        result = true;
//                        drawerLayout.closeDrawer(Gravity.START, false);
//                        startActivity(uic.goTo(ShoppingCart.this, ScanQRCode.class));
//                        finish();
//                        break;
//                    case R.id.nav_exploreItems:
//                        result = true;
//                        intent = new Intent(getBaseContext(), AvailableItems.class);
//                        intent.putExtra("title", "Menu Items");
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.nav_shoppingCart:
//                        result = true;
//                        drawerLayout.closeDrawer(Gravity.START, false);
//                        startActivity(uic.goTo(ShoppingCart.this, ShoppingCart.class));
//                        finish();
//                        break;
//                    case R.id.nav_receivedProduction:
//                        if(!uc.returnWorkgroup(ShoppingCart.this).equals("Manager")){
//                            if(!accessc.isUserAllowed(ShoppingCart.this,"Received from Production", userID)){
//                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
//                            }else if(accessc.checkCutOff(ShoppingCart.this)) {
//                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
//                            }else{
//                                result = true;
//                                intent = new Intent(getBaseContext(), AvailableItems.class);
//                                intent.putExtra("title", "Manual Received from Production");
//                                startActivity(intent);
//                                finish();
//                            }
//                        }else if(accessc.checkCutOff(ShoppingCart.this)){
//                            Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
//                        }else {
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "Manual Received from Production");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_receivedBranch:
//                        if(!uc.returnWorkgroup(ShoppingCart.this).equals("Manager")) {
//                            if (!accessc.isUserAllowed(ShoppingCart.this, "Received from Production", userID)) {
//                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
//                            }else if(accessc.checkCutOff(ShoppingCart.this)) {
//                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
//                            }else {
//                                result = true;
//                                intent = new Intent(getBaseContext(), AvailableItems.class);
//                                intent.putExtra("title", "Manual Received from Other Branch");
//                                startActivity(intent);
//                                finish();
//                            }
//                        }else if(accessc.checkCutOff(ShoppingCart.this)){
//                            Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
//                        }else {
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "Manual Received from Other Branch");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_receivedSupplier:
//                        if(!uc.returnWorkgroup(ShoppingCart.this).equals("Manager")) {
//                            if (!accessc.isUserAllowed(ShoppingCart.this, "Received from Production", userID)) {
//                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
//                            }else if(accessc.checkCutOff(ShoppingCart.this)) {
//                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
//                            } else {
//                                result = true;
//                                intent = new Intent(getBaseContext(), AvailableItems.class);
//                                intent.putExtra("title", "Manual Received from Direct Supplier");
//                                startActivity(intent);
//                                finish();
//                            }
//                        }else if(accessc.checkCutOff(ShoppingCart.this)){
//                            Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
//                        }else {
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "Manual Received from Direct Supplier");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_transferOut2:
//                        result = true;
//                        intent = new Intent(getBaseContext(), AvailableItems.class);
//                        intent.putExtra("title", "Manual Transfer Out");
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.nav_storeCountListPullOut:
//                        if(!accessc.checkCutOff(ShoppingCart.this)) {
//                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
//                        }else if(ac.isTypeExist(ShoppingCart.this, "Auditor Count Pull Out") && ac.isTypeExist(ShoppingCart.this, "Store Count Pull Out") && ac.isTypeExist(ShoppingCart.this, "Final Count Pull Out")){
//                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
//                        }else if (ac.isTypeExist(ShoppingCart.this, "Store Count Pull Out")) {
//                            Toast.makeText(getBaseContext(), "You have already Store Count", Toast.LENGTH_SHORT).show();
//                        }else{
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "PO Store Count List Items");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_auditorCountListPullOut:
//                        if(!accessc.checkCutOff(ShoppingCart.this)) {
//                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
//                        }else if(ac.isTypeExist(ShoppingCart.this, "Auditor Count Pull Out") && ac.isTypeExist(ShoppingCart.this, "Store Count Pull Out") && ac.isTypeExist(ShoppingCart.this, "Final Count Pull Out")){
//                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
//                        }else if (ac.isTypeExist(ShoppingCart.this, "Auditor Count Pull Out")) {
//                            Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
//                        }else if(!uc.returnWorkgroup(ShoppingCart.this).equals("Auditor")){
//                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
//                        }else{
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "PO Auditor Count List Items");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_finalCountListPullOut:
//                        if(!accessc.checkCutOff(ShoppingCart.this)) {
//                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
//                        }else if(ac.isTypeExist(ShoppingCart.this, "Auditor Count Pull Out") && ac.isTypeExist(ShoppingCart.this, "Store Count Pull Out") && ac.isTypeExist(ShoppingCart.this, "Final Count Pull Out")){
//                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
//                        }else if (!ac.isTypeExist(ShoppingCart.this, "Auditor Count Pull Out") & !ac.isTypeExist(ShoppingCart.this, "Store Count Pull Out")) {
//                            Toast.makeText(getBaseContext(), "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
//                        }else if(!uc.returnWorkgroup(ShoppingCart.this).equals("Manager")){
//                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
//                        }else {
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "PO Final Count List Items");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_storeCountList:
//                        if(!accessc.checkCutOff(ShoppingCart.this)) {
//                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
//                        }else if(ac.isTypeExist(ShoppingCart.this, "Auditor Count") && ac.isTypeExist(ShoppingCart.this, "Store Count") && ac.isTypeExist(ShoppingCart.this, "Final Count")){
//                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
//                        }else if (ac.isTypeExist(ShoppingCart.this, "Store Count")) {
//                            Toast.makeText(getBaseContext(), "You have already Store Count", Toast.LENGTH_SHORT).show();
//                        }else{
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "AC Store Count List Items");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_auditorCountList:
//                        if(!accessc.checkCutOff(ShoppingCart.this)) {
//                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
//                        }else if(ac.isTypeExist(ShoppingCart.this, "Auditor Count") && ac.isTypeExist(ShoppingCart.this, "Store Count") && ac.isTypeExist(ShoppingCart.this, "Final Count")){
//                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
//                        }else if (ac.isTypeExist(ShoppingCart.this, "Auditor Count")) {
//                            Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
//                        }else if(!uc.returnWorkgroup(ShoppingCart.this).equals("Auditor")){
//                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
//                        }else{
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "AC Auditor Count List Items");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_finalCountList:
//                        if(!accessc.checkCutOff(ShoppingCart.this)) {
//                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
//                        }else if(ac.isTypeExist(ShoppingCart.this, "Auditor Count") && ac.isTypeExist(ShoppingCart.this, "Store Count") && ac.isTypeExist(ShoppingCart.this, "Final Count")){
//                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
//                        }else if (!ac.isTypeExist(ShoppingCart.this, "Auditor Count") & !ac.isTypeExist(ShoppingCart.this, "Store Count")) {
//                            Toast.makeText(getBaseContext(), "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
//                        }else if(!uc.returnWorkgroup(ShoppingCart.this).equals("Manager")){
//                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
//                        }else {
//                            result = true;
//                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                            intent.putExtra("title", "AC Final Count List Items");
//                            startActivity(intent);
//                            finish();
//                        }
//                        break;
//                    case R.id.nav_inventory:
//                        result = true;
//                        intent = new Intent(getBaseContext(), Inventory.class);
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.nav_cancelRecTrans:
//                        result = true;
//                        intent = new Intent(getBaseContext(), CancelRecTrans.class);
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.nav_receivedSap:
//                        result = true;
//                        intent = new Intent(getBaseContext(), ReceivedSap.class);
//                        intent.putExtra("title", "Received from SAP");
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.nav_updateActualEndingBalance:
//                        result = true;
//                        intent = new Intent(getBaseContext(), UpdateActualEndingBalance.class);
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.nav_itemReceivable:
//                        result = true;
//                        intent = new Intent(getBaseContext(), AvailableItems.class);
//                        intent.putExtra("title", "Item Receivable");
//                        startActivity(intent);
//                        finish();
//                        break;
//                }
//                return result;
//            }
//        });

        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).setTitle(Html.fromHtml("<font color='#ffffff'>Shopping Cart</font>"));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        loadData();

    }


    public void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(ShoppingCart.this);
                        startActivity(uic.goTo(ShoppingCart.this, MainActivity.class));
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
        int totalCart = myDb.countItems();
        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"SetTextI18n", "ResourceType", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadData() {
        final LinearLayout layout = findViewById(R.id.parentLayout);
        layout.removeAllViews();

        final LinearLayout.LayoutParams layoutParamsBtnback = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 70);
        layoutParamsBtnback.setMargins(20,20,0,10);
        Button btnBack = new Button(this);
        btnBack.setLayoutParams(layoutParamsBtnback);
        btnBack.setBackgroundColor(R.color.colorBlue);
        btnBack.setTextColor(Color.rgb(255, 255, 255));
        btnBack.setText("BACK");
        btnBack.setTextSize(13);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layout.addView(btnBack);


        final LinearLayout.LayoutParams layoutParamsNoItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsNoItems.setMargins(50, 20, 50, 0);
        final LinearLayout.LayoutParams layoutParamsLblError = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsLblError.setMargins(30,0,20,20);
        int totalItems = myDb.countItems();
        if (totalItems <= 0) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(layoutParamsNoItems);
            imageView.setImageResource(R.drawable.ic_sad_face);
            layout.addView(imageView);
            TextView lblNoItemFound = new TextView(this);
            lblNoItemFound.setLayoutParams(layoutParamsNoItems);
            lblNoItemFound.setText("Your Shopping Cart is currently empty. Tap the button below to shop for items.");
            lblNoItemFound.setBackgroundColor(Color.WHITE);
            lblNoItemFound.setTextSize(15);
            lblNoItemFound.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            layout.addView(lblNoItemFound);

            Button btnGotoShoppingCart = new Button(this);
            btnGotoShoppingCart.setText("Go Menu Items");
            btnGotoShoppingCart.setBackgroundResource(R.color.colorPrimary);
            btnGotoShoppingCart.setLayoutParams(layoutParamsNoItems);
            btnGotoShoppingCart.setTextColor(Color.WHITE);

            btnGotoShoppingCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Handler handler = new Handler();
                    LoadingDialog loadingDialog = new LoadingDialog(ShoppingCart.this);
                    loadingDialog.startLoadingDialog();
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
//                                        if(getIntent().getStringExtra("hiddenTitle") == "API Menu Items"){
//
//                                        }else{
//                                            Intent intent;
//                                            intent = new Intent(getBaseContext(), AvailableItems.class);
//                                            intent.putExtra("inventory_type", inventory_type);
//                                            intent.putExtra("title", "Menu Items");
//                                            startActivity(intent);
//                                        }
                                        Intent intent;
                                        intent = new Intent(getBaseContext(), APIReceived.class);
                                        intent.putExtra("title", "Menu Items");
                                        intent.putExtra("hiddenTitle", "API Menu Items");
                                        startActivity(intent);
                                    }
                                });
                            }
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    loadingDialog.dismissDialog();
                                }
                            });
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            });

            layout.addView(btnGotoShoppingCart);
        } else {
            LinearLayout.LayoutParams layoutParamsPay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsBtnRemoveItem = new LinearLayout.LayoutParams(70, 70);
            LinearLayout.LayoutParams layoutParamsItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsBtnPay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            LinearLayout.LayoutParams layoutParamsQuantity = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsTableLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsDiscount = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lpDiscountType = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsBtnRemoveItem.gravity = Gravity.RIGHT;

            layoutParamsQuantity.setMargins(20, 0, 20, 20);
            lpDiscountType.setMargins(20, 20, 20, 20);
            layoutParamsDiscount.setMargins(20, 0, 20, 20);
            layoutParamsTableLayout.setMargins(0,20,0,20);
            layoutParamsBtnPay.setMargins(30,0,30,0);
            layout.setOrientation(LinearLayout.VERTICAL);


            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);


            final TableLayout tableLayout = new TableLayout(this);
            tableLayout.setLayoutParams(layoutParamsTableLayout);
            tableLayout.setBackgroundColor(Color.rgb(244,244,244));

            String[] columns = {"Item","Price,","Qty.","Disc.", "Total","Action"};

            TableRow tableColumn = new TableRow(this);
            for (String s : columns) {
                TextView lblColumn1 = new TextView(this);
                lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblColumn1.setText(s);
                lblColumn1.setPadding(10, 0, 10, 0);
                lblColumn1.setTextSize(13);
                tableColumn.addView(lblColumn1);
            }
            tableLayout.addView(tableColumn);



            final Cursor result = myDb.getAllData();
               if(!result.equals(null)){
                   while (result.moveToNext()) {
                       final TableRow tableRow = new TableRow(this);
                       tableRow.setBackgroundColor(Color.WHITE);
                       final String item = result.getString(1);
                       double price = result.getDouble(3);
                       double quantity = result.getDouble(2);
                       double discount = result.getDouble(4);
                       double discountAmount = (price * quantity) * (discount / 100);
                       double total = (price * quantity) - discountAmount;
                       int id = result.getInt(0);

                       LinearLayout linearLayoutItem = new LinearLayout(this);
                       linearLayoutItem.setPadding(10, 10, 10, 10);
                       linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                       linearLayoutItem.setBackgroundColor(Color.WHITE);
                       linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                       tableRow.addView(linearLayoutItem);

                       TextView lblColumn2 = new TextView(this);
                       lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                       lblColumn2.setLayoutParams(layoutParamsItem);
//                       String v = cutWord(item);
                       lblColumn2.setText(item);
                       lblColumn2.setTextSize(15);
                       lblColumn2.setBackgroundColor(Color.WHITE);

                       lblColumn2.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) { 
                               Toast.makeText( ShoppingCart.this, item, Toast.LENGTH_SHORT).show();
                           }
                       });

                       linearLayoutItem.addView(lblColumn2);

                       TextView lblColumn3 = new TextView(this);
                       lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                       lblColumn3.setText(df.format(price));
                       lblColumn3.setPadding(10, 10, 10, 10);
                       lblColumn3.setBackgroundColor(Color.WHITE);
                       lblColumn3.setTextSize(13);
                       tableRow.addView(lblColumn3);

                       TextView lblColumn4 = new TextView(this);
                       lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                       lblColumn4.setText(dfInt.format(quantity));
                       lblColumn4.setPadding(10, 10, 10, 10);
                       lblColumn4.setBackgroundColor(Color.WHITE);
                       lblColumn4.setTextSize(13);
                       tableRow.addView(lblColumn4);

                       TextView lblColumn5 = new TextView(this);
                       lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
                       lblColumn5.setText(df.format(discount) + "%");
                       lblColumn5.setPadding(10, 10, 10, 10);
                       lblColumn5.setBackgroundColor(Color.WHITE);
                       lblColumn5.setTextSize(13);
                       tableRow.addView(lblColumn5);

                       TextView lblColumn6 = new TextView(this);
                       lblColumn6.setGravity(View.TEXT_ALIGNMENT_CENTER);
                       lblColumn6.setText(df.format(total));
                       lblColumn6.setPadding(10, 10, 10, 10);
                       lblColumn6.setBackgroundColor(Color.WHITE);
                       lblColumn6.setTextSize(13);
                       tableRow.addView(lblColumn6);

                       final TextView lblColumn7 = new TextView(this);
                       lblColumn7.setGravity(View.TEXT_ALIGNMENT_CENTER);
                       lblColumn7.setText("Remove");
                       lblColumn7.setPadding(10, 10, 10, 10);
                       lblColumn7.setTextColor(Color.RED);
                       lblColumn7.setTextSize(13);
                       lblColumn7.setBackgroundColor(Color.WHITE);
                       lblColumn7.setTag(id);

                       lblColumn7.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                   return;
                               }
                               mLastClickTime = SystemClock.elapsedRealtime();
                               deleteData(lblColumn7.getTag().toString());
                               computeTotal();
                               loadData();
                               toastMsg("Item Removed", 0);
                           }
                       });
                       tableRow.addView(lblColumn7);
                       tableLayout.addView(tableRow);
                       View viewLine = new View(this);
                       LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                       viewLine.setLayoutParams(layoutParamsLine);
                       viewLine.setBackgroundColor(Color.GRAY);
                       tableLayout.addView(viewLine);
                   }
               }
            layout.addView(tableLayout);


            LinearLayout layoutPay = new LinearLayout(this);
            layoutPay.setBackgroundColor(Color.WHITE);
            layoutPay.setLayoutParams(layoutParamsPay);

            final Spinner cmbDiscountType = new Spinner(this);
            cmbDiscountType.setTag("cmbDiscountType");
            cmbDiscountType.setLayoutParams(lpDiscountType);
//            List<String> discounts = sc.returnDiscounts(ShoppingCart.this);

//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, discounts);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            cmbDiscountType.setAdapter(adapter);



            cmbDiscountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        AlertDialog.Builder myDialog = new AlertDialog.Builder(ShoppingCart.this);
                        myDialog.setCancelable(false);
                        myDialog.setTitle("Senior/PWD/Employee Discount");
                        LinearLayout layout = new LinearLayout(ShoppingCart.this);
                        layout.setPadding(40, 40, 40, 40);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText txtId = new EditText(ShoppingCart.this);
                        txtId.setHint("Enter ID");
                        layout.addView(txtId);

                        final EditText txtName = new EditText(ShoppingCart.this);
                        txtName.setHint("Enter Name");
                        layout.addView(txtName);
                        myDialog.setView(layout);

                        myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (txtId.getText().toString().equals("")) {
                                    toastMsg("ID field is required", 0);
                                    cmbDiscountType.setSelection(0);
                                } else if (txtName.getText().toString().equals("")) {
                                    toastMsg("Name field is required", 0);
                                    cmbDiscountType.setSelection(0);
                                } else {
                                    discountID = txtId.getText().toString();
                                    discountName = txtName.getText().toString();
                                    computeTotal();
                                }
                            }
                        });

                        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                discountID = "";
                                discountName = "";
                                cmbDiscountType.setSelection(0);
                                dialog.dismiss();
                            }
                        });

                        myDialog.show();
                    } else {
                        computeTotal();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            final Button btnPay = new Button(this);
            btnPay.setTag("Pay");
            btnPay.setLayoutParams(layoutParamsBtnPay);
            btnPay.setTextSize(20);
            btnPay.setText("PAY");
            btnPay.setBackgroundResource(R.color.colorPrimary);
            btnPay.setTextColor(Color.WHITE);

            btnPay.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    layout.clearFocus();
                    if (myDb.countItems() <= 0) {
                        showMessage("Atlantic Bakery", "No Item found");
                    }else{
                        confirmationDialog();
                    }
                }
            });


            final TextView lblTotalItems= new TextView(this);
            lblTotalItems.setText("Total Items: 0");

            lblTotalItems.setLayoutParams(layoutParamsLblError);
            lblTotalItems.setTextColor(Color.RED);
            lblTotalItems.setTag("lblTotalItems");
            lblTotalItems.setTextSize(20);


            final TextView lblSubTotal = new TextView(this);
            lblSubTotal.setText("0.00");

            lblSubTotal.setLayoutParams(layoutParamsLblError);
            lblSubTotal.setTextColor(Color.rgb(52, 168, 83));
            lblSubTotal.setTag("lblSubTotal");
            lblSubTotal.setTextSize(20);

            layoutPay.addView(cmbDiscountType);
            layoutPay.addView(lblTotalItems);
            layoutPay.addView(lblSubTotal);
            layoutPay.addView(btnPay);
            layoutPay.setOrientation(LinearLayout.VERTICAL);
            layoutPay.setPadding(20, 20, 20, 20);
            layout.addView(layoutPay);
            computeTotal();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalCart = myDb.countItems();
        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String cutWord(String value){
        String result;
        int limit = 15;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void confirmationDialog(){
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(ShoppingCart.this);
        LinearLayout layout = new LinearLayout(ShoppingCart.this);
        layout.setPadding(40,40,40,40);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView lblARTitle = new TextView(ShoppingCart.this);
        lblARTitle.setTextSize(20);
        lblARTitle.setGravity(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(lblARTitle);

        final AutoCompleteTextView txtCustomer = new AutoCompleteTextView(ShoppingCart.this);
        txtCustomer.setHint("Customer Name");


        View root = getWindow().getDecorView().getRootView();
        Spinner cmbDiscount =root.findViewWithTag("cmbDiscountType");
        double discountType = 0.00;
        if(cmbDiscount.getSelectedItemPosition() != 0){
            String spinnerText = (String) cmbDiscount.getSelectedItem();
//            discountType = sc.getDiscountPercent(ShoppingCart.this, spinnerText);
        }

        final double getSubTotal = myDb.getSubTotal() - (myDb.getSubTotal() * (discountType / 100));
        TextView lblSubtotal = new TextView(ShoppingCart.this);
        lblSubtotal.setText("Amount Payable: " + df.format(getSubTotal));
        lblSubtotal.setTextSize(20);
        lblSubtotal.setGravity(View.TEXT_ALIGNMENT_CENTER);

        final EditText txttendered = new EditText(ShoppingCart.this);
        final TextView lblChange = new TextView(ShoppingCart.this);
        txttendered.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txttendered.setText("0.0");
        txttendered.setHint("Enter Tendered Amount");
        final double[] change = {0.00};

        txttendered.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value = txttendered.getText().toString();
                if(value.matches("")){
                    change[0] = 0 - getSubTotal;
                }else{
                    change[0] = Double.parseDouble(txttendered.getText().toString()) - getSubTotal;
                }
                lblChange.setText("Change: " + df.format(change[0]));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txttendered.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    String value = txttendered.getText().toString();
                    if(value.matches("")){
                        txttendered.setText("0");
                    }
                }
            }
        });


        double v = 0 - getSubTotal;
        lblChange.setText("Change: " + df.format(v));
        lblChange.setTextSize(20);
        lblChange.setGravity(View.TEXT_ALIGNMENT_CENTER);

         Spinner cmbTenderType = new Spinner(this);
         List<String> discounts = new ArrayList<>();

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPaddress + "/api/sales/type/get_all";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            try {
                if (response.getBoolean("success")) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int ii = 0; ii < jsonArray.length(); ii++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(ii);
                        discounts.add(jsonObject.getString("code"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, discounts);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cmbTenderType.setAdapter(adapter);
                } else {
                    String msg = response.getString("message");
                    if (msg.equals("Token is invalid")) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCart.this);
                        builder.setCancelable(false);
                        builder.setMessage("Your session is expired. Please login again.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pc.loggedOut(ShoppingCart.this);
                                pc.removeToken(ShoppingCart.this);
                                startActivity(uic.goTo(ShoppingCart.this, MainActivity.class));
                                finish();
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show()) {
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

        cmbTenderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(0 == cmbTenderType.getSelectedItem().toString().indexOf("CASH")){
                    txttendered.setText("");
                    txttendered.setEnabled(true);
                    txtCustomer.setText("");
                    txtCustomer.setEnabled(false);
                    txttendered.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txttendered, InputMethodManager.SHOW_IMPLICIT);
                }else if(0 == cmbTenderType.getSelectedItem().toString().indexOf("Agent AR Sales")){
                    txttendered.setText("");
                    txttendered.setEnabled(false);
                    txtCustomer.setText("");
                    txtCustomer.setEnabled(false);
                }
                else{
                    txttendered.setText("");
                    txttendered.setEnabled(false);
                    txtCustomer.setText("");
                    txtCustomer.setEnabled(true);
                    txtCustomer.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtCustomer, InputMethodManager.SHOW_IMPLICIT);
                }
                String URL = IPaddress + "/api/customer/get_all?transtype=sales";
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray jsonArray = response.getJSONArray("data");
                            final List<String> result = new ArrayList<>();
                            for (int ii = 0; ii < jsonArray.length(); ii++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(ii);
                                result.add(jsonObject.getString("code"));
                            }
                            if (result != null) {
                                txtCustomer.setAdapter(fillName(result));
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCart.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pc.loggedOut(ShoppingCart.this);
                                        pc.removeToken(ShoppingCart.this);
                                        startActivity(uic.goTo(ShoppingCart.this, MainActivity.class));
                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show()) {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        layout.addView(cmbTenderType);

        layout.addView(txtCustomer);
        layout.addView(lblSubtotal);
        layout.addView(txttendered);
        layout.addView(lblChange);

        myDialog.setView(layout);
        myDialog.setCancelable(false);

        myDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Handler handler = new Handler();
                LoadingDialog loadingDialog = new LoadingDialog(ShoppingCart.this);
                loadingDialog.startLoadingDialog();
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
                                    final String customerName = txtCustomer.getText().toString().trim();
                                    String tendertype = cmbTenderType.getSelectedItem().toString();
                                    if (cmbTenderType.getAdapter() == null) {
                                        toastMsg("Please select Service Type", 0);
                                    } else if (customerName.isEmpty() && cmbTenderType.getSelectedItem().toString() == "AR Sales") {
                                        toastMsg("Name field is required", 0);
                                    } else {
                                        AlertDialog.Builder dialogConfirmation = new AlertDialog.Builder(ShoppingCart.this);
                                        dialogConfirmation.setTitle("Confirm Order?");
                                        final String finalTendertype = tendertype;
                                        dialogConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String disctype;
                                                String tendered = txttendered.getText().toString();
                                                double tenderamt;
//                                          DISCOUNT TYPE
//                                                View root = getWindow().getDecorView().getRootView();
//                                                Spinner currentDiscountType = root.findViewWithTag("cmbDiscountType");
//                                                if (currentDiscountType.getSelectedItemPosition() == 0) {
//                                                    disctype = "N/A";
//                                                } else {
//                                                    disctype = currentDiscountType.getSelectedItem().toString();
//                                                }
//                                          TENDER AMOUNT
                                                if (tendered.isEmpty()) {
                                                    tenderamt = 0.00;
                                                } else if (Double.parseDouble(tendered) < 0) {
                                                    tenderamt = 0.00;
                                                } else {
                                                    tenderamt = Double.parseDouble(tendered);
                                                }
//                                          CUSTOMER
                                                try {
                                                    APISaveTransaction(finalTendertype, tenderamt, txtCustomer.getText().toString());
//                                                    insertTransaction(finalTendertype, disctype, tenderamt, customerName);
                                                    discountID = "";
                                                    discountName = "";

                                                } catch (Exception e) {
                                                    showMessage("Error", e.getMessage());
                                                }
                                            }
                                        });

                                        dialogConfirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        dialogConfirmation.show();
                                    }
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                loadingDialog.dismissDialog();
                            }
                        });
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        myDialog.show();
    }

    public ArrayAdapter<String> fillName(List<String> names){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
    }

    @SuppressLint({"WrongConstant", "ShowToast"})
    public void deleteData(String id){
        Integer deletedItem = myDb.deleteData(id);
        if(deletedItem < 0){
            Toast.makeText(this,"Item not remove", 0).show();
        }
    }

    @SuppressLint("SetTextI18n")
    public void computeTotal(){
        View root = getWindow().getDecorView().getRootView();
        TextView lblsubtotal =root.findViewWithTag("lblSubTotal");
        TextView lblTotalQuantity =root.findViewWithTag("lblTotalItems");
        Spinner cmbDiscount =root.findViewWithTag("cmbDiscountType");
        double discountType = 0.00;
        if(cmbDiscount.getSelectedItemPosition() != 0){
            String spinnerText = (String) cmbDiscount.getSelectedItem();
//            discountType = sc.getDiscountPercent(ShoppingCart.this, spinnerText);
        }

        double getSubTotal = myDb.getSubTotal() - (myDb.getSubTotal() * (discountType / 100));
        if(getSubTotal > 0){
            lblsubtotal.setText(Html.fromHtml("<font color='#606665'>" + "Amount Payable: " + "</font>₱"+  df.format(getSubTotal)));
        }else{
            lblsubtotal.setText(Html.fromHtml("<font color='#606665'>" + "Amount Payable: " + "</font>") + "₱0.00");
        }
        lblTotalQuantity.setText(Html.fromHtml("<font color='#606665'>" + "Total Items: " + "</font>"+ dfInt.format(myDb.geTotalItems())));
    }

    public void closeKeyboard(View v){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            v.clearFocus();
        }
    }

    public void toastMsg(String value, Integer duration){
        Toast.makeText(this, value, duration).show();
    }

    public void showMessage(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void insertTransaction(final String tendertype, final String disctype, final Double tenderamt, final String customer) {
//        try {
//            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
//            int userid = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
//            Integer ordernum = sc.getOrderNumber(ShoppingCart.this);
//            Double less = sc.getDiscountPercent(ShoppingCart.this, disctype);
//            double change;
//            con = cc.connectionClass(ShoppingCart.this);
//            if (con == null) {
//                Toast.makeText(ShoppingCart.this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
//            } else {
//                double getSubTotal = myDb.getSubTotal() - (myDb.getSubTotal() * (less / 100));
//                if (tenderamt > getSubTotal) {
//                    change = tenderamt - getSubTotal;
//                } else {
//                    change = 0.00;
//                }
//                double discamt = myDb.getSubTotal() * (less / 100);
//                double subtotalBefore = myDb.getSubTotalBefore();
//                Cursor result2 = myDb.getAllData();
//                if(result2.moveToNext()){
//                    inventory_type = result2.getString(7);
//                }
//
//                String query = "INSERT INTO tbltransaction2 (ornum, ordernum, transdate, cashier," +
//                        " tendertype, servicetype, delcharge, subtotal, disctype, less, vatsales, vat," +
//                        " amtdue, tenderamt, change, refund, comment, remarks, customer, tinnum, tablenum," +
//                        " pax, createdby, datecreated, datemodified, status, status2, area, gctotal, typez," +
//                        " discamt,inventory_type) VALUES ('000'," + ordernum + ",(SELECT FORMAT (GETDATE(), 'MM/dd/yyyy')),(SELECT username FROM tblusers WHERE" +
//                        " systemid=" + userid + "),'" + tendertype + "','Take Out'," +
//                        "0," + subtotalBefore + ",'" + disctype + "'," + less + ",0,0," + getSubTotal + "," + tenderamt + "," + change + "," +
//                        "0,'','','" + customer + "',0,0,0,(SELECT username FROM tblusers WHERE systemid=" + userid + ")," +
//                        "(SELECT GETDATE()),(SELECT GETDATE()),1,'Unpaid','Sales',0,(SELECT postype FROM tblusers WHERE systemid=" +
//                        "" + userid + ")," + discamt + ",'" + inventory_type + "');";
//                Statement stmt = con.createStatement();
//                stmt.executeUpdate(query);
//
//                if (!disctype.equals("N/A")) {
//                    Statement stmtSenior = con.createStatement();
//                    String querySenior = "INSERT INTO tblsenior (transnum,idno,name,disctype,datedisc,status)" +
//                            "VALUES ('" + ordernum + "','" + discountID + "','" + discountName + "','" + disctype + "'," +
//                            "(SELECT GETDATE()),3)";
//                    stmtSenior.executeUpdate(querySenior);
//                }
//                Cursor result = myDb.getAllData();
//                while (result.moveToNext()) {
//                    String itemname = result.getString(1);
//                    double quantity = Double.parseDouble(result.getString(2));
//                    double totalprice = Double.parseDouble(result.getString(5));
//                    double discount = Double.parseDouble(result.getString(4));
//                    double discountPercent = discount / 100;
//                    int free = Integer.parseInt(result.getString(6));
//                    String query2 = "Insert into tblorder2 (ordernum, category, itemname, qty, price, totalprice, dscnt, free," +
//                            "request, status, discprice, disctrans,area,gc,less,deliver,datecreated,pricebefore,discamt)" +
//                            "VALUES(" + ordernum + ",(SELECT category FROM tblitems WHERE itemname='" + itemname + "'),'" + itemname + "'" +
//                            "," + quantity + ",(SELECT price FROM tblitems WHERE itemname='" + itemname + "')," + totalprice + "," + discount + "," + free + "" +
//                            ",'',1,(SELECT SUM(price - ((" + discount + "/ 100) *price)) FROM tblitems WHERE itemname='" + itemname + "'),0,'Sales',0,0,0,(SELECT GETDATE())," +
//                            "(SELECT SUM(price * " + quantity + ") FROM tblitems WHERE itemname='" + itemname + "')," +
//                            "(SELECT " + discountPercent + " * (price * " + quantity + ") FROM tblitems WHERE itemname='" + itemname + "'));";
//                    Statement stmt2 = con.createStatement();
//                    stmt2.executeUpdate(query2);
//                }
//                con.close();
//                myDb.truncateTable();
//                showMessage("Transaction Completed", "Order #: " + ordernum + "\n" + "Change: " + change);
//                loadData();
//
//            }
//        } catch (SQLException ex) {
//            showMessage("Error", ex.getMessage());
//        }
//    }

    public void APISaveTransaction(String transtype, Double tenderAmount, String customerName){
        try{
            SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
            String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
            JSONObject jsonObject = new JSONObject();

            // create your json here
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            JSONObject jsonObjectHeader = new JSONObject();
            jsonObjectHeader.put("transdate",currentDateandTime);

            if(!transtype.equals("CASH")){
//                String URL = getString(R.string.API_URL) + "/api/customer/getall";
//                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
//                    try{
//                        if(response.getBoolean("success")){
//                            JSONArray jsonArray = response.getJSONArray("data");
//                            for(int i = 0; i < jsonArray.length(); i ++){
//                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
//                                if(customerName.trim() == jsonObject2.getString("name").trim()){
//                                    jsonObjectHeader.put("cust_code",jsonObject2.getString("code"));
//                                    continue;
//                                }
//                            }
//                        }
//                    }catch (Exception ex){
//                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
//                    @Override
//                    public Map<String, String> getHeaders() {
//                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
//                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
//                        Map<String, String> params = new HashMap<>();
//                        params.put("Content-Type", "application/json");
//                        params.put("Authorization", "Bearer " + token);
//                        return params;
//                    }
//                };
//                mQueue.add(request);
                jsonObjectHeader.put("cust_code", customerName);
                jsonObjectHeader.put("cust_name",customerName);
            }

            jsonObjectHeader.put("remarks",null);
            jsonObjectHeader.put("transtype",transtype);
            jsonObjectHeader.put("reference2",null);
            jsonObjectHeader.put("delfee",0);
            jsonObjectHeader.put("discprcnt",0);
            jsonObjectHeader.put("gc_amount",null);
            jsonObjectHeader.put("tenderamt",tenderAmount);
            jsonObjectHeader.put("sap_number",null);
            jsonObject.put("header",jsonObjectHeader);
            Cursor cursor = myDb.getAllData();
            if(cursor != null){
                JSONArray arrayRows = new JSONArray();
                while (cursor.moveToNext()){
                    JSONObject jsonObjectRow = new JSONObject();
                    jsonObjectRow.put("item_code", cursor.getString(1));
                    jsonObjectRow.put("quantity", cursor.getDouble(2));
                    jsonObjectRow.put("uom", "pc(s)");
                    jsonObjectRow.put("unit_price", cursor.getDouble(3));
                    jsonObjectRow.put("discprcnt", cursor.getDouble(4));
                    boolean free = (cursor.getInt(6) > 0);
                    jsonObjectRow.put("free", free);
                    arrayRows.put(jsonObjectRow);
                }
                jsonObject.put("rows", arrayRows);
            }

            SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
            String IPaddress = sharedPreferences2.getString("IPAddress", "");

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            System.out.println(jsonObject);
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(IPaddress + "/api/sales/new")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ShoppingCart.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    String result;
                    result = response.body().string();
                    if (response.isSuccessful()) {
                        String finalResult = result;
                        ShoppingCart.this.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(getBaseContext(), "Transaction Completed", Toast.LENGTH_SHORT).show();
                                    JSONObject jj = new JSONObject(finalResult);
                                    boolean isSuccess = jj.getBoolean("success");
                                    if (isSuccess) {
                                        JSONObject jsonObjectData = jj.getJSONObject("data");
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCart.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("Order #: " + jsonObjectData.getString("reference"));
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                myDb.truncateTable();
                                                loadData();
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();

                                    } else {
                                        String msg = jj.getString("message");
                                        if (msg.equals("Token is invalid")) {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCart.this);
                                            builder.setCancelable(false);
                                            builder.setMessage("Your session is expired. Please login again.");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    pc.loggedOut(ShoppingCart.this);
                                                    pc.removeToken(ShoppingCart.this);
                                                    startActivity(uic.goTo(ShoppingCart.this, MainActivity.class));
                                                    finish();
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();
                                        } else {
                                            Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } else {
                        try{
                            result = response.body().string();
                        }catch(Exception ex) {
//                            ShoppingCart.this.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        }
                        String finalResult1 = result;
                        ShoppingCart.this.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject jj = new JSONObject(finalResult1);
                                    Toast.makeText(getBaseContext(), "ERROR: \n" + jj.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (
                                        Exception ex) {
                                    Toast.makeText(getBaseContext(), "ERROR: \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception ex){
            Toast.makeText(ShoppingCart.this, "APISaveTransaction() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}