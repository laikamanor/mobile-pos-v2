package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ItemReceivable extends AppCompatActivity {
    String title,hiddenTitle;
    Button btnProceed;
    DecimalFormat df = new DecimalFormat("#,###");
    SharedPreferences sharedPreferences;
    DatabaseHelper3 myDb3;
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();

    private OkHttpClient client;
    TextView lblTransactionNumber;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    DatabaseHelper myDb;

    Menu menu;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_receivable);

        myDb = new DatabaseHelper(this);
        myDb3 = new DatabaseHelper3(this);
        client = new OkHttpClient();

        btnProceed = findViewById(R.id.btnProceed);
        lblTransactionNumber = findViewById(R.id.lblTransactionNumber);
        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        title = getIntent().getStringExtra("title");
        hiddenTitle = getIntent().getStringExtra("hiddenTitle");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));
        loadItems();

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
                        intent.putExtra("title", "System Transfer Item");
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
                }
                return result;
            }
        });


        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder myDialog = new AlertDialog.Builder(ItemReceivable.this);
                myDialog.setCancelable(false);
                LinearLayout layout = new LinearLayout(getBaseContext());
                layout.setPadding(40, 40, 40, 40);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView lblRemarks = new TextView(getBaseContext());
                lblRemarks.setText("*Remarks:");
                lblRemarks.setTextSize(15);
                lblRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblRemarks);

                EditText txtRemarks = new EditText(ItemReceivable.this);
                txtRemarks.setTextSize(15);
                txtRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(txtRemarks);

                myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ItemReceivable.this);
                        builder.setMessage("Are you sure want to confirm?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        apiSaveData(txtRemarks.getText().toString());
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        int totalCart = myDb.countItems();
        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
    }

    public void apiSaveData(String remarks){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        JSONObject jsonObject = new JSONObject();
        try {
            int baseID = 0;
            Cursor cursor = myDb3.getAllData(hiddenTitle);
            if(cursor != null){
                if(cursor.moveToNext()){
                    baseID = cursor.getInt(9);
                }
            }

            // create your json here
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            JSONObject objectHeaders = new JSONObject();
            objectHeaders.put("transtype", "TRFR");
            objectHeaders.put("base_id", baseID);
            objectHeaders.put("sap_number", null);
            objectHeaders.put("transdate", currentDateandTime);
            objectHeaders.put("remarks", remarks);
            objectHeaders.put("reference2", null);
            objectHeaders.put("supplier", null);
            jsonObject.put("header", objectHeaders);
            JSONArray arrayDetails = new JSONArray();
            jsonObject.put("details", arrayDetails);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(IPaddress + "/api/inv/recv/new")
                .method("post", body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ItemReceivable.this.runOnUiThread(() -> {
                   Toast.makeText(getBaseContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String result;
                try {
                    assert response.body() != null;
                    result = response.body().string();
                    if(response.isSuccessful()){
                        JSONObject jj = new JSONObject(result);
                        boolean isSuccess = jj.getBoolean("success");
                        if (isSuccess) {
                            ItemReceivable.this.runOnUiThread(() -> {
                                try{
                                    myDb3.truncateTable();
                                    Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent;
                                    intent = new Intent(getBaseContext(), API_SelectedItems.class);
                                    intent.putExtra("title", title);
                                    intent.putExtra("hiddenTitle", hiddenTitle);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    ItemReceivable.this.runOnUiThread(() -> {
                                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        } else {
                            ItemReceivable.this.runOnUiThread(() -> {
                                try{
                                    Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();

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
                        pc.loggedOut(ItemReceivable.this);
                        pc.removeToken(ItemReceivable.this);
                        startActivity(uic.goTo(ItemReceivable.this, MainActivity.class));
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


    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadItems() {
        TableRow tableColumn = new TableRow(getBaseContext());
        final TableLayout tableLayout = findViewById(R.id.table_main);
        tableLayout.removeAllViews();
        String[] columns = {"Item", "Qty."};

        for (String s : columns) {
            TextView lblColumn1 = new TextView(getBaseContext());
            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn1.setText(s);
            lblColumn1.setPadding(10, 0, 10, 0);
            tableColumn.addView(lblColumn1);
        }
        tableLayout.addView(tableColumn);
        try {
            Cursor cursor = myDb3.getAllData(hiddenTitle);
            if(cursor != null) {
                while (cursor.moveToNext()) {
                    lblTransactionNumber.setText("Reference #: " + cursor.getString(1));
                    final TableRow tableRow = new TableRow(getBaseContext());
                    String itemName = cursor.getString(3);
                    String v = cutWord(itemName);
                    double quantity = cursor.getDouble(4);

                    TextView lblColumn1 = new TextView(getBaseContext());
                    lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn1.setText(v);
                    lblColumn1.setPadding(10, 0, 10, 0);
                    tableRow.addView(lblColumn1);

                    TextView lblColumn2 = new TextView(getBaseContext());
                    lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn2.setText(df.format(quantity));
                    lblColumn2.setPadding(10, 10, 10, 10);
                    tableRow.addView(lblColumn2);

                    tableLayout.addView(tableRow);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String cutWord(String value){
        String result;
        int limit = 15;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}