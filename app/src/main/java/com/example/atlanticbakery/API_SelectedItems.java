package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class API_SelectedItems extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatabaseHelper4 myDb4;
    DatabaseHelper3 myDb3;
    long mLastClickTime = 0;
    Button btnProceed,btnBack;
    DecimalFormat df = new DecimalFormat("#,###");
    String title,hiddenTitle;
    private OkHttpClient client;
    private RequestQueue mQueue;

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    DatabaseHelper myDb;
    TextView lblSelectedDate;

    Menu menu;
    @SuppressLint({"WrongConstant", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i__selected_items);

        myDb = new DatabaseHelper(this);
        client = new OkHttpClient();
        myDb4 = new DatabaseHelper4(this);
        myDb3 = new DatabaseHelper3(this);
        btnProceed = findViewById(R.id.btnProceed);
        btnBack = findViewById(R.id.btnBack);
        title = getIntent().getStringExtra("title");
        hiddenTitle = getIntent().getStringExtra("hiddenTitle");
        mQueue = Volley.newRequestQueue(this);

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


        btnProceed.setOnClickListener(view -> {
            final AlertDialog.Builder myDialog = new AlertDialog.Builder(API_SelectedItems.this);
            myDialog.setCancelable(false);
            LinearLayout layout = new LinearLayout(getBaseContext());
            layout.setPadding(40, 40, 40, 40);
            layout.setOrientation(LinearLayout.VERTICAL);

            Spinner cmbBranch = new Spinner(getBaseContext());
            EditText txtSAPNumber = new EditText(getBaseContext());
            if(hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Item Request")){
                TextView lblBranch = new TextView(getBaseContext());
                lblBranch.setText((hiddenTitle.equals("API Received Item")) ? "*From Branch:" : "*To Warehouse:");
                lblBranch.setTextSize(15);
                lblBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblBranch);

                List<String> discounts = returnBranches();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, discounts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cmbBranch.setAdapter(adapter);
                layout.addView(cmbBranch);


                if(hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Transfer Item")){
                    TextView lblSAPNumber = new TextView(getBaseContext());
                    lblSAPNumber.setText("SAP #:");
                    lblSAPNumber.setTextSize(15);
                    lblSAPNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    layout.addView(lblSAPNumber);

                    txtSAPNumber.setTextSize(15);
                    txtSAPNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    txtSAPNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(txtSAPNumber);
                }
            }

           if(hiddenTitle.equals("API Item Request")){
               Button btnPickDate = new Button(getBaseContext());
               btnPickDate.setText("Pick Due Date");
               btnPickDate.setBackgroundResource(R.color.colorPrimary);
               btnPickDate.setTextColor(Color.WHITE);
               btnPickDate.setGravity(View.TEXT_ALIGNMENT_CENTER);

               btnPickDate.setOnClickListener(new View.OnClickListener() {
                   @RequiresApi(api = Build.VERSION_CODES.N)
                   @Override
                   public void onClick(View view) {
                       showDatePickerDialog();
                   }
               });
               layout.addView(btnPickDate);

               lblSelectedDate = new TextView(getBaseContext());
               lblSelectedDate.setText("N/A");
               lblSelectedDate.setTextSize(15);
               lblSelectedDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
               layout.addView(lblSelectedDate);
           }

            TextView lblRemarks = new TextView(getBaseContext());
            lblRemarks.setTextColor(Color.rgb(0,0,0));
            lblRemarks.setText("*Remarks:");
            lblRemarks.setTextSize(15);
            lblRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
            layout.addView(lblRemarks);

            EditText txtRemarks = new EditText(API_SelectedItems.this);
            txtRemarks.setTextColor(Color.rgb(0,0,0));
            txtRemarks.setTextSize(15);
            txtRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
            layout.addView(txtRemarks);

            String supplier = "";
            Cursor cursor = myDb3.getAllData(hiddenTitle);
            if(cursor != null){
                if(cursor.moveToNext()){
                    supplier = cursor.getString(2);
                }
            }

            String finalSupplier = supplier;
            myDialog.setPositiveButton("Submit", (dialogInterface, i) -> {
                if(cmbBranch.getSelectedItemPosition() <= 0 && hiddenTitle.equals("API Received Item")){
                    Toast.makeText(getBaseContext(), "Please select from Warehouse", Toast.LENGTH_SHORT).show();
                }else if(cmbBranch.getSelectedItemPosition() <= 0 && hiddenTitle.equals("API Transfer Item")){
                    Toast.makeText(getBaseContext(), "Please select to Warehouse", Toast.LENGTH_SHORT).show();
                }else if(cmbBranch.getSelectedItemPosition() <= 0 && hiddenTitle.equals("API Item Request")) {
                    Toast.makeText(getBaseContext(), "Please select to Warehouse", Toast.LENGTH_SHORT).show();
                }else if(txtRemarks.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Remarks field is empty", Toast.LENGTH_SHORT).show();
                }else if(hiddenTitle.equals("API Item Request") && lblSelectedDate.getText().toString() == "N/A"){
                    Toast.makeText(getBaseContext(), "Please select Due Date", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(hiddenTitle.equals("API Received Item")){
                        int sapNumber = (txtSAPNumber.getText().toString().isEmpty() ? 0 : Integer.parseInt(txtSAPNumber.getText().toString()));
                        apiSaveManualReceived(cmbBranch.getSelectedItem().toString(), txtRemarks.getText().toString(), sapNumber);
                    }else if(hiddenTitle.equals("API Transfer Item")){
                        apiSaveTransferItem(cmbBranch.getSelectedItem().toString(), txtRemarks.getText().toString());
                    }else if(hiddenTitle.equals("API Item Request")){
                        apiItemRequest(txtRemarks.getText().toString(),cmbBranch.getSelectedItem().toString());
                    }else if(hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count")){
                        apiSaveInventoryCount(txtRemarks.getText().toString());
                    }
                    else{
                        apiSaveDataRec(finalSupplier, txtRemarks.getText().toString());
                    }
                }
            });

            myDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            myDialog.setView(layout);
            myDialog.show();
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        int totalCart = myDb.countItems();
        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");

        loadItems();
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
                .setPositiveButton("Yes", (dialog, which) -> {
                    pc.loggedOut(API_SelectedItems.this);
                    pc.removeToken(API_SelectedItems.this);
                    startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
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

    public void apiSaveTransferItem(String toBranch,String remarks){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        JSONObject jsonObject = new JSONObject();
        try {
            // create your json here
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());


            JSONObject objectHeaders = new JSONObject();
//            objectHeaders.put("transtype", "TRFR");
            objectHeaders.put("sap_number", null);
            objectHeaders.put("transdate", currentDateandTime);
            objectHeaders.put("remarks", remarks);
            objectHeaders.put("reference2", null);

            jsonObject.put("header", objectHeaders);

            JSONArray arrayDetails = new JSONArray();

            Cursor cursor = myDb4.getAllData(title);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSONObject objectDetails = new JSONObject();
                    objectDetails.put("item_code", cursor.getString(1));
                    objectDetails.put("to_whse", toBranch);
//                    objectDetails.put("to_whse", null);
                    objectDetails.put("quantity", cursor.getDouble(2));
                    objectDetails.put("uom", "pc(s)");
                    arrayDetails.put(objectDetails);
                }
                jsonObject.put("details", arrayDetails);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(IPaddress + "/api/inv/trfr/new")
                .method("POST", body)
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
                String result;
                try {
                    result = response.body().string();

                    JSONObject jj = new JSONObject(result);
                    boolean isSuccess = jj.getBoolean("success");
                    if (isSuccess) {
                        API_SelectedItems.this.runOnUiThread(() -> {
                            myDb4.truncateTable();
                            Toast.makeText(getBaseContext(), "Transaction Completed", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            intent = new Intent(getBaseContext(), API_SelectedItems.class);
                            intent.putExtra("title", title);
                            intent.putExtra("hiddenTitle", hiddenTitle);
                            startActivity(intent);
                            finish();
                        });
                    }else {
                        API_SelectedItems.this.runOnUiThread(() -> {
                            try {
                                System.out.println(jj.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void apiItemRequest(String remarks,String fromWarehouse){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        JSONObject jsonObject = new JSONObject();
        try {
            // create your json here
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());


            JSONObject objectHeaders = new JSONObject();
            objectHeaders.put("transdate", currentDateandTime);
            objectHeaders.put("duedate", lblSelectedDate.getText().toString() + " 00:00");
            objectHeaders.put("remarks", remarks);
            objectHeaders.put("reference2", null);

            jsonObject.put("header", objectHeaders);

            JSONArray arrayDetails = new JSONArray();

            Cursor cursor = myDb4.getAllData(title);

//            SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
//            String branch = Objects.requireNonNull(sharedPreferences2.getString("whse", ""));
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSONObject objectDetails = new JSONObject();
                    objectDetails.put("item_code", cursor.getString(1));
//                    objectDetails.put("to_whse", null);
                    objectDetails.put("quantity", cursor.getDouble(2));
                    objectDetails.put("uom", "pc(s)");
                    objectDetails.put("from_whse", fromWarehouse);
                    arrayDetails.put(objectDetails);
                }
                jsonObject.put("rows", arrayDetails);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        System.out.println(jsonObject);
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(IPaddress + "/api/inv/item_request/new")
                .method("POST", body)
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
                String result;
                try {
                    result = response.body().string();

                    JSONObject jj = new JSONObject(result);
                    boolean isSuccess = jj.getBoolean("success");
                    if (isSuccess) {
                        API_SelectedItems.this.runOnUiThread(() -> {
                            myDb4.truncateTable();
                            Toast.makeText(getBaseContext(), "Transaction Completed", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            intent = new Intent(getBaseContext(), API_SelectedItems.class);
                            intent.putExtra("title", title);
                            intent.putExtra("hiddenTitle", hiddenTitle);
                            startActivity(intent);
                            finish();
                        });
                    }else {
                        API_SelectedItems.this.runOnUiThread(() -> {
                            try {
                                System.out.println(jj.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int returnDate(String value){
        int result = 0;
        SimpleDateFormat y = new SimpleDateFormat(value, Locale.getDefault());
        result = Integer.parseInt(y.format(new Date()));
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        lblSelectedDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
    }


    public void apiSaveManualReceived(String fromBranch,String remarks,Integer sapNumber){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        JSONObject jsonObject = new JSONObject();
        try {
            // create your json here
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            JSONObject objectHeaders = new JSONObject();
            objectHeaders.put("transtype", "MNL");
            objectHeaders.put("transfer_id", null);
            objectHeaders.put("sap_number", (sapNumber <= 0 ? null: sapNumber));
            objectHeaders.put("transdate", currentDateandTime);
            objectHeaders.put("remarks", remarks);
            objectHeaders.put("reference2", null);
            objectHeaders.put("supplier", null);

            jsonObject.put("header", objectHeaders);


            JSONArray arrayDetails = new JSONArray();

            Cursor cursor = myDb4.getAllData(title);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSONObject objectDetails = new JSONObject();
                    objectDetails.put("item_code", cursor.getString(1));
                    objectDetails.put("from_whse", fromBranch);
//                    objectDetails.put("to_whse", null);
                    objectDetails.put("quantity", cursor.getDouble(2));
                    objectDetails.put("actualrec", cursor.getDouble(2));
                    objectDetails.put("uom", "pc(s)");
                    arrayDetails.put(objectDetails);
                }
                jsonObject.put("details", arrayDetails);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(IPaddress + "/api/inv/recv/new")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                formatResponse(e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String answer = response.body().string();
                formatResponse(answer);
            }
        });
    }

    public void formatResponse(String temp){
        if(!temp.isEmpty() && temp.substring(0,1).equals("{")){
            try{
                JSONObject jj = new JSONObject(temp );
                boolean isSuccess = jj.getBoolean("success");
                if (isSuccess) {
                    myDb4.truncateTable();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Transaction Completed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent;
                    intent = new Intent(getBaseContext(), API_SelectedItems.class);
                    intent.putExtra("title", title);
                    intent.putExtra("hiddenTitle", hiddenTitle);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = jj.getString("message");
                    if(msg.equals("Token is invalid")){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                        builder.setCancelable(false);
                        builder.setMessage("Your session is expired. Please login again.");
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            pc.loggedOut(API_SelectedItems.this);
                            pc.removeToken(API_SelectedItems.this);
                            startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
                            finish();
                            dialog.dismiss();
                        });
                        builder.show();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Error \n" +  msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }catch (Exception ex){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ex.printStackTrace();
                        Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            Runnable r = () -> {
                Toast.makeText(getBaseContext(), temp, Toast.LENGTH_SHORT).show();
            };
        }
    }

    public List<String> returnBranches(){
        List<String> result = new ArrayList<>();
        result.add("Select Warehouse");
        try{
            SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
            String IPaddress = sharedPreferences2.getString("IPAddress", "");
            String URL = IPaddress + "/api/whse/get_all?transtype=TRFR";
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String branch = jsonObject.getString("whsecode");
                            result.add(branch);
                        }
                    } else {
                        String msg = response.getString("message");
                        if(msg.equals("Token is invalid")){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                            builder.setCancelable(false);
                            builder.setMessage("Your session is expired. Please login again.");
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                pc.loggedOut(API_SelectedItems.this);
                                pc.removeToken(API_SelectedItems.this);
                                startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
                                finish();
                                dialog.dismiss();
                            });
                            builder.show();
                        }else{
                            Toast.makeText(getBaseContext(), "Error \n" +  msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
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
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadItems() {
        Cursor cursor;
        int count = (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API System Transfer Item") ? myDb3.countItems(hiddenTitle) : myDb4.countItems(title));
//                int count = myDb4.countItems(title);

        if (hiddenTitle.equals("API Received from SAP")) {
            count = myDb3.countSAPItems(hiddenTitle);
        } else if (hiddenTitle.equals("API Inventory Count")) {
            count = myDb3.countItems(hiddenTitle);
        } else if (hiddenTitle.equals("API Pull Out Count")) {
            count = myDb3.countItems(hiddenTitle);
        } else if (hiddenTitle.equals("API System Transfer Item")) {
            count = myDb3.countItems(hiddenTitle);
        } else {
            count = myDb4.countItems(title);
        }

        LinearLayout layout = findViewById(R.id.layoutNoItems);

        if (count == 0) {
            layout.setVisibility(View.VISIBLE);
            Button btnGoto = findViewById(R.id.btnGoto);
            btnGoto.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent;
                intent = new Intent(getBaseContext(), APIReceived.class);
                intent.putExtra("title", title);
                intent.putExtra("hiddenTitle", hiddenTitle);
                startActivity(intent);
            });
            btnProceed.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.GONE);

            btnProceed.setVisibility(View.VISIBLE);

            TableRow tableColumn = new TableRow(API_SelectedItems.this);
            final TableLayout tableLayout = findViewById(R.id.table_main);
            tableLayout.removeAllViews();
            String[] columns = (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item") ? new String[]{"Item", "Del. Qty.", "Act. Qty.", "Var.", "Action"} : new String[]{"Item", "Qty."});

            for (String s : columns) {
                TextView lblColumn1 = new TextView(API_SelectedItems.this);
                lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblColumn1.setText(s);
                lblColumn1.setPadding(10, 0, 10, 0);
                tableColumn.addView(lblColumn1);
            }
            tableLayout.addView(tableColumn);
            cursor = (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count") || hiddenTitle.equals("API System Transfer Item") ? myDb3.getAllData(hiddenTitle) : myDb4.getAllData(title));

//                    cursor = myDb4.getAllData(title);

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    if (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count") || hiddenTitle.equals("API System Transfer Item")) {
                        if (cursor.getInt(6) == 1) {
                            final TableRow tableRow = new TableRow(API_SelectedItems.this);
                            tableRow.setBackgroundColor(Color.WHITE);
                            String itemName = cursor.getString((hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count") || hiddenTitle.equals("API System Transfer Item") ? 3 : 1));
                            String v = cutWord(itemName);
                            double quantity = 0.00;

                            if (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item")) {
                                quantity = cursor.getDouble(4);
                            } else if (hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count")) {
                                quantity = cursor.getDouble(5);
                            } else {
                                quantity = cursor.getDouble(2);
                            }

                            final int id = cursor.getInt(0);

                            LinearLayout linearLayoutItem = new LinearLayout(this);
                            linearLayoutItem.setPadding(10, 10, 10, 10);
                            linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                            linearLayoutItem.setBackgroundColor(Color.WHITE);
                            linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            tableRow.addView(linearLayoutItem);

                            LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                            TextView lblColumn1 = new TextView(this);
                            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn1.setLayoutParams(layoutParamsItem);
//                       String v = cutWord(item);
                            lblColumn1.setText(itemName);
                            lblColumn1.setTextSize(13);
                            lblColumn1.setBackgroundColor(Color.WHITE);

                            lblColumn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getBaseContext(), itemName, Toast.LENGTH_SHORT).show();
                                }
                            });

                            linearLayoutItem.addView(lblColumn1);

                            TextView lblColumn2 = new TextView(API_SelectedItems.this);
                            lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn2.setText(df.format(quantity));
                            lblColumn2.setTextSize(13);
                            lblColumn2.setBackgroundColor(Color.WHITE);
                            lblColumn2.setPadding(10, 10, 10, 10);
                            tableRow.addView(lblColumn2);

                            if (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item")) {
                                TextView lblColumn4 = new TextView(API_SelectedItems.this);
                                lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                lblColumn4.setText(df.format(cursor.getDouble(5)));
                                lblColumn4.setBackgroundColor(Color.WHITE);
                                lblColumn4.setPadding(10, 10, 10, 10);
                                lblColumn4.setTextSize(13);
                                tableRow.addView(lblColumn4);

                                TextView lblColumn5 = new TextView(API_SelectedItems.this);
                                lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                double variance = cursor.getDouble(5) - quantity;
                                lblColumn5.setText(df.format(variance));
                                lblColumn5.setBackgroundColor(Color.WHITE);
                                lblColumn5.setTextSize(13);
                                lblColumn5.setPadding(10, 10, 10, 10);
                                tableRow.addView(lblColumn5);
                            }

                            TextView lblColumn3 = new TextView(API_SelectedItems.this);
                            lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn3.setTag(id);
                            lblColumn3.setBackgroundColor(Color.WHITE);
                            lblColumn3.setText("Remove");
                            lblColumn3.setTextSize(13);
                            lblColumn3.setPadding(10, 10, 10, 10);
                            lblColumn3.setTextColor(Color.RED);

                            lblColumn3.setOnClickListener(view -> {
                                boolean deletedItem;
                                deletedItem = (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item") ? myDb3.removeData(Integer.toString(id)) : myDb3.deleteData(Integer.toString(id)));
                                if (!deletedItem) {
                                    Toast.makeText(API_SelectedItems.this, "Item not remove", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(API_SelectedItems.this, "Item removed", Toast.LENGTH_SHORT).show();
                                    Intent intent;
                                    intent = new Intent(getBaseContext(), API_SelectedItems.class);
                                    intent.putExtra("title", title);
                                    intent.putExtra("hiddenTitle", hiddenTitle);
                                    startActivity(intent);
                                    finish();
                                }


                                if (myDb4.countItems(title).equals(0)) {
                                    tableLayout.removeAllViews();
                                    btnProceed.setVisibility(View.GONE);
                                }
                            });

                            tableRow.addView(lblColumn3);

                            tableLayout.addView(tableRow);
                        }
                        View viewLine = new View(this);
                        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        viewLine.setLayoutParams(layoutParamsLine);
                        viewLine.setBackgroundColor(Color.GRAY);
                        tableLayout.addView(viewLine);
                    } else {
                        final TableRow tableRow = new TableRow(API_SelectedItems.this);
                        tableRow.setBackgroundColor(Color.WHITE);
                        String itemName = cursor.getString(1);
                        String v = cutWord(itemName);
                        double quantity = cursor.getDouble(2);
                        final int id = cursor.getInt(0);

                        LinearLayout linearLayoutItem = new LinearLayout(this);
                        linearLayoutItem.setPadding(10, 10, 10, 10);
                        linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                        linearLayoutItem.setBackgroundColor(Color.WHITE);
                        linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        tableRow.addView(linearLayoutItem);

                        LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView lblColumn1 = new TextView(this);
                        lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn1.setLayoutParams(layoutParamsItem);
                        lblColumn1.setBackgroundColor(Color.WHITE);
                        lblColumn1.setText(itemName);
                        lblColumn1.setTextSize(15);
                        lblColumn1.setBackgroundColor(Color.WHITE);

                        lblColumn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getBaseContext(), itemName, Toast.LENGTH_SHORT).show();
                            }
                        });

                        linearLayoutItem.addView(lblColumn1);


                        TextView lblColumn2 = new TextView(API_SelectedItems.this);
                        lblColumn2.setBackgroundColor(Color.WHITE);
                        lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn2.setText(df.format(quantity));
                        lblColumn2.setTextSize(15);
                        lblColumn2.setPadding(10, 10, 10, 10);
                        tableRow.addView(lblColumn2);

                        TextView lblColumn3 = new TextView(API_SelectedItems.this);
                        lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn3.setBackgroundColor(Color.WHITE);
                        lblColumn3.setTag(id);
                        lblColumn3.setText("Remove");
                        lblColumn3.setTextSize(13);
                        lblColumn3.setPadding(10, 10, 10, 10);
                        lblColumn3.setTextColor(Color.RED);

                        lblColumn3.setOnClickListener(view -> {
                            int deletedItem;
                            deletedItem = myDb4.deleteData(Integer.toString(id));
                            if (deletedItem < 0) {
                                Toast.makeText(API_SelectedItems.this, "Item not remove", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(API_SelectedItems.this, "Item removed", Toast.LENGTH_SHORT).show();
                                Intent intent;
                                intent = new Intent(getBaseContext(), API_SelectedItems.class);
                                intent.putExtra("title", title);
                                intent.putExtra("hiddenTitle", hiddenTitle);
                                startActivity(intent);
                                finish();
                            }

                            if (myDb4.countItems(title).equals(0)) {
                                tableLayout.removeAllViews();
                                btnProceed.setVisibility(View.GONE);
                            }
                        });

                        tableRow.addView(lblColumn3);

                        tableLayout.addView(tableRow);

                        View viewLine = new View(this);
                        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        viewLine.setLayoutParams(layoutParamsLine);
                        viewLine.setBackgroundColor(Color.GRAY);
                        tableLayout.addView(viewLine);

                    }
                }
            }
        }
    }

    public void apiSaveDataRec(String supplier, String remarks) {
        Cursor cursor = myDb3.getAllData(hiddenTitle);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String sap_number = cursor.getString(1);
                String fromBranch = cursor.getString(2);

                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                JSONObject jsonObject = new JSONObject();
                try {
                    // create your json here
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    JSONObject objectHeaders = new JSONObject();

                    String transType;
                    if(cursor.getInt(7) <= 0 && hiddenTitle.equals("API Received from SAP")){
                        transType = "SAPPO";
                    }else if(cursor.getInt(7) > 0 && hiddenTitle.equals("API Received from SAP")){
                        transType = "SAPIT";
                    }else {
                        transType = "TRFR";
                    }

                    objectHeaders.put("transtype", transType);
                  if(hiddenTitle.equals("API Received from SAP")){
                      objectHeaders.put("transfer_id", null);
                  }else {
                      objectHeaders.put("base_id", (cursor.getInt(9) <= 0 ? null : cursor.getInt(9)));
                  }
                    objectHeaders.put("sap_number", (hiddenTitle.equals("API Received from SAP") ? (sap_number.isEmpty() ? null : sap_number) : null));
                    objectHeaders.put("transdate", currentDateandTime);
                    objectHeaders.put("remarks", remarks);
                    objectHeaders.put((hiddenTitle.equals("API Received from SAP") ? "reference2" : "ref2"), null);
                    objectHeaders.put("supplier", (cursor.getInt(7) == 0) ? supplier : null);
                    jsonObject.put("header", objectHeaders);

                    JSONArray arrayDetails = new JSONArray();

                    Cursor cursor2 = myDb3.getAllData(hiddenTitle);
                    while (cursor2.moveToNext()) {
                        if(cursor2.getInt(6) == 1) {
                            JSONObject objectDetails = new JSONObject();
                            String itemName = cursor2.getString(3);
                            Double deliveredQty = cursor2.getDouble(4);
                            Double actualQty = cursor2.getDouble(5);

                            objectDetails.put("item_code", itemName);
                            objectDetails.put("from_whse", fromBranch);
                            objectDetails.put("to_whse", cursor2.getString(8));
                            objectDetails.put("quantity", deliveredQty);
                            objectDetails.put("actualrec", actualQty);
                            objectDetails.put("uom", "pc(s)");
                            arrayDetails.put(objectDetails);
                        }
                    }
                    jsonObject.put("details", arrayDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(jsonObject);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPaddress = sharedPreferences2.getString("IPAddress", "");

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPaddress + "/api/inv/recv/new")
                        .method("POST", body)
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
                        String result = "";
                        try {
                            result = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String finalResult = result;
                        API_SelectedItems.this.runOnUiThread(() -> {
                            try {
                                JSONObject jj = new JSONObject(finalResult);
                                boolean isSuccess = jj.getBoolean("success");
                                if (isSuccess) {
                                    myDb3.truncateTable();
                                    Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent;
                                    intent = new Intent(getBaseContext(), API_SelectedItems.class);
                                    intent.putExtra("title", title);
                                    intent.putExtra("hiddenTitle", hiddenTitle);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String msg = jj.getString("message");
                                    if (msg.equals("Token is invalid")) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("Your session is expired. Please login again.");
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            pc.loggedOut(API_SelectedItems.this);
                                            pc.removeToken(API_SelectedItems.this);
                                            startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
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
                        });
                    }
                });
            }
        }
    }

    public void apiSaveInventoryCount(String remarks) {
        Cursor cursor = myDb3.getAllData(hiddenTitle);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                JSONObject jsonObject = new JSONObject();
                try {
                    // create your json here
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    JSONObject objectHeaders = new JSONObject();

                    objectHeaders.put("transdate", currentDateandTime);
                    objectHeaders.put("remarks", remarks);
                    jsonObject.put("header", objectHeaders);

                    JSONArray arrayDetails = new JSONArray();

                    Cursor cursor2 = myDb3.getAllData(hiddenTitle);
                    while (cursor2.moveToNext()) {
                        if(cursor2.getInt(6) == 1) {
                            JSONObject objectDetails = new JSONObject();
                            String itemName = cursor2.getString(3);
                            Double deliveredQty = cursor2.getDouble(4);
                            Double actualQty = cursor2.getDouble(5);

                            objectDetails.put("item_code", itemName);

                            if(hiddenTitle.equals("API Inventory Count")){
                                objectDetails.put("quantity", deliveredQty);
                            }

                            objectDetails.put((hiddenTitle.equals("API Inventory Count") ? "actual_count" : "quantity"), actualQty);
                            objectDetails.put("uom", "pc(s)");
                            arrayDetails.put(objectDetails);
                        }
                    }
                    jsonObject.put("rows", arrayDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPaddress = sharedPreferences2.getString("IPAddress", "");

                String isInvCount = (hiddenTitle.equals("API Inventory Count") ? "inv/count" : "pulloutreq");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPaddress + "/api/" + isInvCount + "/create")
                        .method("POST", body)
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
                        String result = "";
                        try {
                            result = response.body().string();
                            System.out.println(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String finalResult = result;
                        API_SelectedItems.this.runOnUiThread(() -> {
                            try {
                                JSONObject jj = new JSONObject(finalResult);
                                boolean isSuccess = jj.getBoolean("success");
                                if (isSuccess) {
                                    myDb3.truncateTable();
                                    Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent;
                                    intent = new Intent(getBaseContext(), API_SelectedItems.class);
                                    intent.putExtra("title", title);
                                    intent.putExtra("hiddenTitle", hiddenTitle);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String msg = jj.getString("message");
                                    if (msg.equals("Token is invalid")) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("Your session is expired. Please login again.");
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            pc.loggedOut(API_SelectedItems.this);
                                            pc.removeToken(API_SelectedItems.this);
                                            startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
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
                        });
                    }
                });
            }
        }
    }

    public String cutWord(String value){
        String result;
        int limit = 10;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}