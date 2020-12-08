package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OfflineItems extends AppCompatActivity {
    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DatabaseHelper myDb;
    DatabaseHelper7 myDb7;

    DecimalFormat df = new DecimalFormat("#,###");

    private OkHttpClient client;
    Menu menu;
    TableLayout tableLayout;
    Button btnBack,btnCancel;

    String title, hidden_title;
    TextView txtReference,txtHeader;
    String typeTrans;
    long mLastClickTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_items);

        tableLayout = findViewById(R.id.table_main);
        btnBack = findViewById(R.id.btnBack);
        txtReference = findViewById(R.id.txtReference);
        txtHeader = findViewById(R.id.txtHeader);
        btnCancel = findViewById(R.id.btnCancel);

        myDb = new DatabaseHelper(this);
        myDb7 = new DatabaseHelper7(this);
        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        client = new OkHttpClient();

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        txtReference = findViewById(R.id.txtReference);
        String reference = getIntent().getStringExtra("reference");
        txtReference.setText(reference);


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
        typeTrans = getIntent().getStringExtra("type");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String id = getIntent().getStringExtra("id");
        String type = getIntent().getStringExtra("type");
        Cursor cursor = myDb7.getAllDataByModule(id, "id");

        if(cursor != null){
            String body = "";
            while (cursor.moveToNext()){
                body = cursor.getString(3);
            }
            loadData(body,type);
        }
        cursor.close();
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfflineItems.this);
                builder.setMessage("Are you sure want to cancel?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String id = getIntent().getStringExtra("id");
                                String reference = getIntent().getStringExtra("reference");
                                boolean isSuccess = myDb7.deleteData(id);
                                if(isSuccess){
                                    Toast.makeText(getBaseContext(), reference + " successfully cancelled!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getBaseContext(), OfflineList.class);
                                    intent.putExtra("title", title);
                                    intent.putExtra("hiddenTitle", hidden_title);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(getBaseContext(), reference + " failed to cancel", Toast.LENGTH_SHORT).show();
                                }
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
        });
    }


    public void loadData(String result,String type){
        try{
            tableLayout.removeAllViews();
            TableRow tableColumn = new TableRow(OfflineItems.this);
            String[] columns;

            if(!typeTrans.equals("Menu Items")) {
                columns = new String[]{"Item", "Qty."};
            }else{
                columns = new String[]{"Item", "Qty.", "Price", "Disc. %"};
            }
            for (String s : columns) {
                TextView lblColumn1 = new TextView(OfflineItems.this);
                lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblColumn1.setText(s);
                lblColumn1.setPadding(10, 0, 10, 0);
                lblColumn1.setTextSize(20);
                lblColumn1.setTextColor(Color.BLACK);
                tableColumn.addView(lblColumn1);
            }
            tableLayout.addView(tableColumn);

            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonObjectHeader = jsonObject.getJSONObject("header");
            System.out.println(jsonObjectHeader);
            String sHeader = "";
            if(type.equals("Menu Items") || type.equals("Transfer Item")) {
                sHeader = "Transdate: " + jsonObjectHeader.getString("transdate") + "\n" +
//                        "Remarks: " + jsonObjectHeader.getString("remarks") + "\n" +
                        (type.equals("Transfer Item") ? "" : "Trans. Type: " + jsonObjectHeader.getString("transtype") + "\n") +
                        "Discount %: " + (jsonObjectHeader.isNull("discprcnt") ? 0.00 : jsonObjectHeader.getDouble("discprcnt")) + "\n" +
                        "Tender Amount: " + (jsonObjectHeader.isNull("tenderamt") ? 0.00 : jsonObjectHeader.getDouble("tenderamt")) + "\n" +
                        "SAP #: " + (jsonObjectHeader.isNull("sap_number") ? "N/A" : jsonObjectHeader.getInt("sap_number")) + "\n";
            }else if(type.equals("Received Item")){
                sHeader = "Transdate: " + jsonObjectHeader.getString("transdate") + "\n" +
                        "Remarks: " + jsonObjectHeader.getString("remarks") + "\n" +
                        "Supplier: " + (jsonObjectHeader.isNull("supplier") ? "N/A" : jsonObjectHeader.getString("supplier")) + "\n" +
                        (type.equals("Transfer Item") ? "" : "Type: " + (jsonObjectHeader.isNull("type2") ? "N/A" : jsonObjectHeader.getString("type2")) + "\n" +
                                "SAP #: " + (jsonObjectHeader.isNull("sap_number") ? "N/A" : jsonObjectHeader.getInt("sap_number")) + "\n");

            }else if(type.equals("Item Request")){
                sHeader = "Transdate: " + jsonObjectHeader.getString("transdate") + "\n" +
                        "Due Date: " + jsonObjectHeader.getString("duedate") + "\n" +
                        "Remarks: " + jsonObjectHeader.getString("remarks") + "\n";
            }
            txtHeader.setText(sHeader);

            String arrayName = "";

            if(type.equals("Menu Items") || type.equals("Item Request")){
                arrayName = "rows";
            }else if(type.equals("Received Item") || type.equals("Transfer Item")){
                arrayName = "details";
            }

            JSONArray jsonArrayRecRow = jsonObject.getJSONArray(arrayName);
            for (int i = 0; i < jsonArrayRecRow.length(); i++) {
                JSONObject jsonObjectRecRow = jsonArrayRecRow.getJSONObject(i);

                final TableRow tableRow = new TableRow(getBaseContext());
                tableRow.setBackgroundColor(Color.WHITE);
                LinearLayout linearLayoutItem = new LinearLayout(this);
                linearLayoutItem.setPadding(10, 10, 10, 10);
                linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                linearLayoutItem.setBackgroundColor(Color.WHITE);
                linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                tableRow.addView(linearLayoutItem);

                LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView lblColumn1 = new TextView(this);
                lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblColumn1.setLayoutParams(layoutParamsItem);
//                       String v = cutWord(item);
                lblColumn1.setText(jsonObjectRecRow.getString("item_code"));
                lblColumn1.setTextSize(15);
                lblColumn1.setTextColor(Color.BLACK);
                lblColumn1.setBackgroundColor(Color.WHITE);
                linearLayoutItem.addView(lblColumn1);

                TextView lblColumn2 = new TextView(getBaseContext());
                lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblColumn2.setText(df.format(jsonObjectRecRow.getDouble("quantity")));
                lblColumn2  .setTextSize(15);
                lblColumn2.setTextColor(Color.BLACK);
                lblColumn2.setBackgroundColor(Color.WHITE);
                lblColumn2.setPadding(10, 10, 10, 10);
                tableRow.addView(lblColumn2);

                TextView lblColumn3 = new TextView(getBaseContext());
                lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);

                double price = jsonObjectRecRow.isNull("unit_price") ? 0.00 : jsonObjectRecRow.getDouble("unit_price");
                double discprcnt = jsonObjectRecRow.isNull("discprcnt") ? 0.00 : jsonObjectRecRow.getDouble("discprcnt");

                if(typeTrans.equals("Menu Items")){
                    lblColumn3.setText(df.format(price));
                    lblColumn3.setTextSize(15);
                    lblColumn3.setTextColor(Color.BLACK);
                    lblColumn3.setBackgroundColor(Color.WHITE);
                    lblColumn3.setPadding(10, 10, 10, 10);
                    tableRow.addView(lblColumn3);

                    TextView lblColumn4 = new TextView(getBaseContext());
                    lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn4.setText(df.format(discprcnt));
                    lblColumn4.setTextSize(15);
                    lblColumn4.setTextColor(Color.BLACK);
                    lblColumn4.setBackgroundColor(Color.WHITE);
                    lblColumn4.setPadding(10, 10, 10, 10);

                    tableRow.addView(lblColumn4);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tableLayout.addView(tableRow);
                        View viewLine = new View(getBaseContext());
                        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        viewLine.setLayoutParams(layoutParamsLine);
                        viewLine.setBackgroundColor(Color.GRAY);
                        tableLayout.addView(viewLine);
                    }
                });

            }
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                        pc.loggedOut(OfflineItems.this);
                        pc.removeToken(OfflineItems.this);
                        startActivity(uic.goTo(OfflineItems.this, MainActivity.class));
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
