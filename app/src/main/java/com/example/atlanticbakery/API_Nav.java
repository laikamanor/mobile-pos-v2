package com.example.atlanticbakery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class API_Nav extends AppCompatActivity {
    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DatabaseHelper myDb;

    private OkHttpClient client;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i__nav);
        myDb = new DatabaseHelper(this);
        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        client = new OkHttpClient();

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        pc.loggedOut(API_Nav.this);
                        pc.removeToken(API_Nav.this);
                        startActivity(uic.goTo(API_Nav.this, MainActivity.class));
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
}