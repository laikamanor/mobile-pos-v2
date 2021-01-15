package com.example.atlanticbakery;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class ScanQRCode extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static TextView resultText;
    ProgressBar progressBar;

    DatabaseHelper myDb = new DatabaseHelper(this);

    long mLastClickTime = 0;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    int userID = 0;

    //   Classes
    item_class itemc = new item_class();
    prefs_class pc = new prefs_class();
    user_class uc = new user_class();
    actualendbal_class ac = new actualendbal_class();
    ui_class uic = new ui_class();
    receivedsap_class recsap = new receivedsap_class();
    access_class accessc = new access_class();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_q_r_code);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>Scan QR Code</font>"));

        resultText =  findViewById(R.id.lblResult);
        Button btnScan = findViewById(R.id.btnScan);
        Button btnCart = findViewById(R.id.btnAddCart);
        progressBar = findViewById(R.id.progWait);
        progressBar.setVisibility(View.GONE);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean isStoreExist = ac.isTypeExist(ScanQRCode.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(ScanQRCode.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(ScanQRCode.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(ScanQRCode.this, "Store Count Pull Out");
                boolean isAuditorPullOutExist = ac.isTypeExist(ScanQRCode.this, "Auditor Count Pull Out");
                boolean isFinalPullOutExist = ac.isTypeExist(ScanQRCode.this, "Final Count Pull Out");
                boolean result = false;
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_logOut:
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        onBtnLogout();
                        break;
                    case R.id.nav_scanItem:
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        startActivity(uic.goTo(ScanQRCode.this, ScanQRCode.class));
                        finish();
                        break;
                    case R.id.nav_exploreItems:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Menu Items");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_shoppingCart:
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        startActivity(uic.goTo(ScanQRCode.this, ShoppingCart.class));
                        finish();
                        break;
                    case R.id.nav_receivedProduction:
                        if(!uc.returnWorkgroup(ScanQRCode.this).equals("Manager")){
                            if(!accessc.isUserAllowed(ScanQRCode.this,"Received from Production", userID)){
                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                            }else if(accessc.checkCutOff(ScanQRCode.this)) {
                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                            }else{
                                result = true;
                                intent = new Intent(getBaseContext(), AvailableItems.class);
                                intent.putExtra("title", "Manual Received from Production");
                                startActivity(intent);
                                finish();
                            }
                        }else if(accessc.checkCutOff(ScanQRCode.this)){
                            Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                        }else {
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "Manual Received from Production");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_receivedBranch:
                        if(!uc.returnWorkgroup(ScanQRCode.this).equals("Manager")) {
                            if (!accessc.isUserAllowed(ScanQRCode.this, "Received from Production", userID)) {
                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                            }else if(accessc.checkCutOff(ScanQRCode.this)) {
                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                            }else {
                                result = true;
                                intent = new Intent(getBaseContext(), AvailableItems.class);
                                intent.putExtra("title", "Manual Received from Other Branch");
                                startActivity(intent);
                                finish();
                            }
                        }else if(accessc.checkCutOff(ScanQRCode.this)){
                            Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                        }else {
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "Manual Received from Other Branch");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_receivedSupplier:
                        if(!uc.returnWorkgroup(ScanQRCode.this).equals("Manager")) {
                            if (!accessc.isUserAllowed(ScanQRCode.this, "Received from Production", userID)) {
                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                            }else if(accessc.checkCutOff(ScanQRCode.this)) {
                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                            } else {
                                result = true;
                                intent = new Intent(getBaseContext(), AvailableItems.class);
                                intent.putExtra("title", "Manual Received from Direct Supplier");
                                startActivity(intent);
                                finish();
                            }
                        }else if(accessc.checkCutOff(ScanQRCode.this)){
                            Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                        }else {
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "Manual Received from Direct Supplier");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_transferOut2:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Manual Transfer Out");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_storeCountListPullOut:
                        if(!accessc.checkCutOff(ScanQRCode.this)) {
                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                        }else if(isAuditorPullOutExist && isStorePullOutExist && isFinalPullOutExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (isStorePullOutExist) {
                            Toast.makeText(getBaseContext(), "You have already Store Count", Toast.LENGTH_SHORT).show();
                        }else{
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "PO Store Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_auditorCountListPullOut:
                        if(!accessc.checkCutOff(ScanQRCode.this)) {
                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                        }else if(isAuditorPullOutExist && isStorePullOutExist && isFinalPullOutExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (isAuditorPullOutExist) {
                            Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
                        }else if(!uc.returnWorkgroup(ScanQRCode.this).equals("Auditor")){
                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                        }else{
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "PO Auditor Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_finalCountListPullOut:
                        if(!accessc.checkCutOff(ScanQRCode.this)) {
                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                        }else if(isAuditorPullOutExist && isStorePullOutExist && isFinalPullOutExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (!isAuditorPullOutExist & !isStorePullOutExist) {
                            Toast.makeText(getBaseContext(), "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
                        }else if(!uc.returnWorkgroup(ScanQRCode.this).equals("Manager")){
                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                        }else {
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "PO Final Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_storeCountList:
                        if(!accessc.checkCutOff(ScanQRCode.this)) {
                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                        }else if(isAuditorExist && isStoreExist && isFinalExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (isStoreExist) {
                            Toast.makeText(getBaseContext(), "You have already Store Count", Toast.LENGTH_SHORT).show();
                        }else{
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "AC Store Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_auditorCountList:
                        if(!accessc.checkCutOff(ScanQRCode.this)) {
                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                        }else if(isAuditorExist && isStoreExist && isFinalExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (isAuditorExist) {
                            Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
                        }else if(!uc.returnWorkgroup(ScanQRCode.this).equals("Auditor")){
                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                        }else{
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "AC Auditor Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_finalCountList:
                        if(!accessc.checkCutOff(ScanQRCode.this)) {
                            Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                        }else if(isAuditorExist && isStoreExist && isFinalExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (!isAuditorExist & !isStoreExist) {
                            Toast.makeText(getBaseContext(), "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
                        }else if(!uc.returnWorkgroup(ScanQRCode.this).equals("Manager")){
                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                        }else {
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "AC Final Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_inventory:
                        result = true;
                        intent = new Intent(getBaseContext(), Inventory.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_cancelRecTrans:
                        result = true;
                        intent = new Intent(getBaseContext(), CancelRecTrans.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedSap:
                        result = true;
                        intent = new Intent(getBaseContext(), ReceivedSap.class);
                        intent.putExtra("title", "Received from SAP");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_updateActualEndingBalance:
                        result = true;
                        intent = new Intent(getBaseContext(), UpdateActualEndingBalance.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_itemReceivable:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Item Receivable");
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(getApplicationContext(), ScanCode.class));
            }
        });
        btnCart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String itemName = resultText.getText().toString();
                boolean hasStock = itemc.checkItemNameStock(ScanQRCode.this, itemName,1.00);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (itemName.equals("Result: N/A")) {
                    Toast.makeText(ScanQRCode.this, "Scan Item first", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isItemNameExist = itemc.checkItemName(ScanQRCode.this, itemName);
                    if (!isItemNameExist) {
                        Toast.makeText(ScanQRCode.this, "item not found", Toast.LENGTH_SHORT).show();
                    } else if (hasStock) {
                        saveData();
                    }else if(!hasStock) {
                        final AlertDialog.Builder myDialog = new AlertDialog.Builder(ScanQRCode.this);
                        myDialog.setTitle("Atlantic Bakery");
                        myDialog.setMessage("This item is out of stock! Are you sure you want to add to cart?");
                        myDialog.setCancelable(false);
                        myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveData();
                            }
                        });

                        myDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        myDialog.show();
                    }
                }
            }
        });
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(ScanQRCode.this);
                        startActivity(uic.goTo(ScanQRCode.this, MainActivity.class));
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

    public void loadCount(){
        Menu menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.nav_shoppingCart);
        int totalCart = myDb.countItems();
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        loadCount();
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void saveData() {
        checkItem checkItem = new checkItem();
        checkItem.execute("");
    }

    @SuppressLint("StaticFieldLeak")
    public class checkItem extends AsyncTask<String, String, String> {
        String z = "";

        final LoadingDialog loadingDialog = new LoadingDialog(ScanQRCode.this);

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected String doInBackground(String... params) {
            String itemName = resultText.getText().toString();
            double price = itemc.returnItemNamePrice(ScanQRCode.this, itemName);
            boolean isInserted = myDb.insertData(1.0, 0.00, price, 0, price, itemName,"Own Inventory","");
            if (isInserted) {
                z = "Item Added";
            } else {
                z = "Item Not Added";
            }
            resultText.setText("Result: N/A");
            return z;
        }

        @Override
        protected void onPostExecute(final String s) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ScanQRCode.this, s, Toast.LENGTH_LONG).show();
                    loadingDialog.dismissDialog();
                }
            };
            handler.postDelayed(r, 1000);
        }
    }
}