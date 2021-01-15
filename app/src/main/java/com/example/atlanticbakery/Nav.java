package com.example.atlanticbakery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class Nav extends AppCompatActivity  {
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    user_class uc = new user_class();
    access_class accessc = new access_class();
    receivedsap_class recsap = new receivedsap_class();
    DatabaseHelper db = new DatabaseHelper(this);
    actualendbal_class ac = new actualendbal_class();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    int userID = 0;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>Main Menu</font>"));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final boolean[] result = {false};
                final Intent[] intent = new Intent[1];
                Handler handler = new Handler();
                LoadingDialog loadingDialog = new LoadingDialog(Nav.this);
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
                                    switch (menuItem.getItemId()) {
                                        case R.id.nav_logOut:
                                            result[0] = true;
                                            drawerLayout.closeDrawer(Gravity.START, false);
                                            onBtnLogout();
                                            break;
                                        case R.id.nav_scanItem:
                                            result[0] = true;
                                            drawerLayout.closeDrawer(Gravity.START, false);
                                            startActivity(uic.goTo(Nav.this, ScanQRCode.class));
                                            finish();
                                            break;
                                        case R.id.nav_exploreItems:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                            intent[0].putExtra("title", "Menu Items");
                                            startActivity(intent[0]);
                                            finish();
                                            break;
                                        case R.id.nav_shoppingCart:
                                            result[0] = true;
                                            drawerLayout.closeDrawer(Gravity.START, false);
                                            startActivity(uic.goTo(Nav.this, ShoppingCart.class));
                                            finish();
                                            break;
                                        case R.id.nav_receivedProduction:
                                            if (!uc.returnWorkgroup(Nav.this).equals("Manager")) {
                                                if (!accessc.isUserAllowed(Nav.this, "Received from Production", userID)) {
                                                    Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                                                } else if (accessc.checkCutOff(Nav.this)) {
                                                    Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    result[0] = true;
                                                    intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                    intent[0].putExtra("title", "Manual Received from Production");
                                                    startActivity(intent[0]);
                                                    finish();
                                                }
                                            } else if (accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "Manual Received from Production");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_receivedBranch:
                                            if (!uc.returnWorkgroup(Nav.this).equals("Manager")) {
                                                if (!accessc.isUserAllowed(Nav.this, "Received from Production", userID)) {
                                                    Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                                                } else if (accessc.checkCutOff(Nav.this)) {
                                                    Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    result[0] = true;
                                                    intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                    intent[0].putExtra("title", "Manual Received from Other Branch");
                                                    startActivity(intent[0]);
                                                    finish();
                                                }
                                            } else if (accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "Manual Received from Other Branch");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_receivedSupplier:
                                            if (!uc.returnWorkgroup(Nav.this).equals("Manager")) {
                                                if (!accessc.isUserAllowed(Nav.this, "Received from Production", userID)) {
                                                    Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                                                } else if (accessc.checkCutOff(Nav.this)) {
                                                    Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    result[0] = true;
                                                    intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                    intent[0].putExtra("title", "Manual Received from Direct Supplier");
                                                    startActivity(intent[0]);
                                                    finish();
                                                }
                                            } else if (accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Your account is already cut off", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "Manual Received from Direct Supplier");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_transferOut2:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                            intent[0].putExtra("title", "Manual Transfer Out");
                                            startActivity(intent[0]);
                                            finish();
                                            break;
                                        case R.id.nav_itemReceivable:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                            intent[0].putExtra("title", "Item Receivable");
                                            startActivity(intent[0]);
                                            finish();
                                            break;
                                        case R.id.nav_storeCountListPullOut:
                                            if (!accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                                            } else if (ac.isTypeExist(Nav.this, "PO Auditor Count") && ac.isTypeExist(Nav.this, "PO Store Count") && ac.isTypeExist(Nav.this, "PO Final Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                                            } else if (ac.isTypeExist(Nav.this, "PO Store Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Store Count", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "PO Store Count List Items");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_auditorCountListPullOut:
                                            if (!accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                                            } else if (ac.isTypeExist(Nav.this, "PO Auditor Count") && ac.isTypeExist(Nav.this, "PO Store Count") && ac.isTypeExist(Nav.this, "PO Final Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                                            } else if (ac.isTypeExist(Nav.this, "PO Auditor Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
                                            } else if (!uc.returnWorkgroup(Nav.this).equals("Auditor")) {
                                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "PO Auditor Count List Items");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_finalCountListPullOut:
                                            if (!accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                                            } else if (ac.isTypeExist(Nav.this, "PO Auditor Count") && ac.isTypeExist(Nav.this, "PO Store Count") && ac.isTypeExist(Nav.this, "PO Final Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                                            } else if (!ac.isTypeExist(Nav.this, "PO Auditor Count") & !ac.isTypeExist(Nav.this, "PO Store Count")) {
                                                Toast.makeText(getBaseContext(), "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
                                            } else if (!uc.returnWorkgroup(Nav.this).equals("Manager")) {
                                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "PO Final Count List Items");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_storeCountList:
                                            if (!accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                                            } else if (!ac.isTypeExist(Nav.this, "PO Auditor Count") && !ac.isTypeExist(Nav.this, "Store Count") && !ac.isTypeExist(Nav.this, "Final Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                                            } else if (!ac.isTypeExist(Nav.this, "Store Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Store Count", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "AC Store Count List Items");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_auditorCountList:
                                            if (!accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                                            } else if (!ac.isTypeExist(Nav.this, "Auditor Count") && !ac.isTypeExist(Nav.this, "Auditor Count") && !ac.isTypeExist(Nav.this, "Final Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                                            } else if (!ac.isTypeExist(Nav.this, "Auditor Count")) {
                                                Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
                                            } else if (!uc.returnWorkgroup(Nav.this).equals("Auditor")) {
                                                Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "AC Auditor Count List Items");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_finalCountList:
                                            if (!accessc.checkCutOff(Nav.this)) {
                                                Toast.makeText(getBaseContext(), "Cut Off first", Toast.LENGTH_SHORT).show();
                                            } else if (ac.isTypeExist(Nav.this, "Auditor Count") && ac.isTypeExist(Nav.this, "Store Count") && ac.isTypeExist(Nav.this, "Final Count")) {
                                                Toast.makeText(Nav.this, "You have already Final Count", Toast.LENGTH_SHORT).show();
                                            } else if (!ac.isTypeExist(Nav.this, "Auditor Count") & !ac.isTypeExist(Nav.this, "Store Count")) {
                                                Toast.makeText(Nav.this, "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
                                            } else if (!uc.returnWorkgroup(Nav.this).equals("Manager")) {
                                                Toast.makeText(Nav.this, "Access Denied", Toast.LENGTH_SHORT).show();
                                            } else {
                                                result[0] = true;
                                                intent[0] = new Intent(getBaseContext(), AvailableItems.class);
                                                intent[0].putExtra("title", "AC Final Count List Items");
                                                startActivity(intent[0]);
                                                finish();
                                            }
                                            break;
                                        case R.id.nav_inventory:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), Inventory.class);
                                            startActivity(intent[0]);
                                            finish();
                                            break;
                                        case R.id.nav_cancelRecTrans:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), CancelRecTrans.class);
                                            startActivity(intent[0]);
                                            finish();
                                            break;
                                        case R.id.nav_receivedSap:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), ReceivedSap.class);
                                            intent[0].putExtra("title", "Received from SAP");
                                            startActivity(intent[0]);
                                            finish();
                                            break;
                                        case R.id.nav_updateActualEndingBalance:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), UpdateActualEndingBalance.class);
                                            startActivity(intent[0]);
                                            finish();
                                            break;
                                        case R.id.nav_createUser:
                                            result[0] = true;
                                            intent[0] = new Intent(getBaseContext(), CreateUsers.class);
                                            startActivity(intent[0]);
                                            finish();
                                            break;
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

                return result[0];
            }
        });
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    pc.loggedOut(Nav.this);
                    startActivity(uic.goTo(Nav.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        loadCount();
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadCount(){
        Menu menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.nav_shoppingCart);
        int totalCart = db.countItems();
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
    }
}