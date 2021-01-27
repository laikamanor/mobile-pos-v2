package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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

import com.example.atlanticbakery.Adapter.CustomExpandableListAdapter;
import com.example.atlanticbakery.Helper.FragmentNavigationManager_InventoryConfirmation;
import com.example.atlanticbakery.Interface.NavigationManager;

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
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API_InventoryConfirmation extends AppCompatActivity {
    TableLayout tableLayout;
    Button btnProceed;
    private OkHttpClient client;

    String title, hidden_title;
    long mLastClickTime = 0;

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    navigation_class navc = new navigation_class();
    DecimalFormat df = new DecimalFormat("#,###");

    DatabaseHelper myDb;

    Menu menu;
    double poCount = 0;
    String gBranch = "";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
//    private String[] items;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> listTitle;
    private Map<String, List<String>> listChild;
    private NavigationManager navigationManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_confirmation);

        myDb = new DatabaseHelper(this);


        client = new OkHttpClient();

        tableLayout = findViewById(R.id.table_main);
        btnProceed = findViewById(R.id.btnProceed);

        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        expandableListView = (ExpandableListView)findViewById(R.id.navList);
        navigationManager = FragmentNavigationManager_InventoryConfirmation.getmInstance(this);

        View listReaderView = getLayoutInflater().inflate(R.layout.nav_header, null,false);
        TextView txtName = listReaderView.findViewById(R.id.txtName);
        txtName.setText(fullName + " - v" + BuildConfig.VERSION_NAME);
        expandableListView.addHeaderView(listReaderView);

        genData();
        addDrawersItem();
        setupDrawer();

        if(savedInstanceState == null){
            selectFirstItemDefault();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));


        hmReturnBranches();
        hmReturnBranches();

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double [] actualCash = {0.0};
                AlertDialog.Builder myDialog = new AlertDialog.Builder(API_InventoryConfirmation.this);
                myDialog.setCancelable(false);

                LinearLayout layout = new LinearLayout(getBaseContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(50,50,50,50);

                LinearLayout.LayoutParams layoutParamsLblWarehouses = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(layoutParams);

                LinearLayout.LayoutParams layoutParamsLblITR = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParamsLblITR.setMargins(50,20,50,0);

                LinearLayout.LayoutParams layoutParamsTxtITR = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParamsTxtITR.setMargins(50,0,50,10);

                TextView lblActualCash = new TextView(getBaseContext());
                lblActualCash.setTextSize(15);
                lblActualCash.setTextColor(Color.rgb(0,0,0));
                lblActualCash.setText("*Actual Cash:");
                lblActualCash.setLayoutParams(layoutParamsLblITR);
                layout.addView(lblActualCash);

                EditText txtActualCash = new EditText(getBaseContext());
                txtActualCash.setTextSize(15);
                txtActualCash.setLayoutParams(layoutParamsTxtITR);
                txtActualCash.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                layout.addView(txtActualCash);


                txtActualCash.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        actualCash[0] = (txtActualCash.getText().toString().isEmpty() ||txtActualCash.getText().toString().equals(".") ? 0.00 : Double.parseDouble(txtActualCash.getText().toString()));
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                TextView lblRemarks = new TextView(getBaseContext());
                lblRemarks.setTextColor(Color.rgb(0,0,0));
                lblRemarks.setText("*Remarks:");
                lblRemarks.setTextSize(15);
                lblRemarks.setLayoutParams(layoutParamsLblITR);
                lblRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblRemarks);

                EditText txtRemarks = new EditText(API_InventoryConfirmation.this);
                txtRemarks.setTextColor(Color.rgb(0,0,0));
                txtRemarks.setTextSize(15);
                txtRemarks.setLayoutParams(layoutParamsTxtITR);
                txtRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(txtRemarks);

                myDialog.setView(layout);

                myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        try{
                            if(txtRemarks.getText().toString().trim().isEmpty()){
                                Toast.makeText(getBaseContext(), "Please enter remarks", Toast.LENGTH_SHORT).show();
                            }
                            if(actualCash[0] <= 0.00) {
                                Toast.makeText(getBaseContext(), "Please enter actual cash", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_InventoryConfirmation.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                                                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));

                                                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                                String IPAddress = sharedPreferences2.getString("IPAddress", "");


                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                String currentDateTime = sdf.format(new Date());

                                                String URL = IPAddress + "/api/inv/count/confirm?transdate=" + currentDateTime;

                                                JSONObject zxxz = new JSONObject();
                                                try {
                                                    zxxz.put("transdate", currentDateTime);
                                                    zxxz.put("confirm", true);
                                                    zxxz.put("actual_cash", actualCash[0]);
                                                    zxxz.put("remarks", txtRemarks.getText().toString().trim());
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


    public void changePassword(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(API_InventoryConfirmation.this);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(API_InventoryConfirmation.this);
                    builder.setMessage("Are you sure want to submit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    API_InventoryConfirmation.myChangePassword myChangePassword = new API_InventoryConfirmation.myChangePassword(txtPassword.getText().toString().trim());
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
        LoadingDialog loadingDialog = new LoadingDialog(API_InventoryConfirmation.this);
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(API_InventoryConfirmation.this);
                        builder.setMessage("We redirect you to Login Page")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pc.loggedOut(API_InventoryConfirmation.this);
                                        pc.removeToken(API_InventoryConfirmation.this);
                                        startActivity(uic.goTo(API_InventoryConfirmation.this, MainActivity.class));
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    public void selectFirstItemDefault(){
        if(navigationManager != null){
            String firstItem = listTitle.get(0);
            navigationManager.showFragment(firstItem);
            getSupportActionBar().setTitle(firstItem);
        }
    }

    public void addDrawersItem(){
        adapter = new CustomExpandableListAdapter(this, listTitle, listChild);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedItem = ((List)listChild.get(listTitle.get(groupPosition)))
                        .get(childPosition).toString();
                getSupportActionBar().setTitle(selectedItem);
                Intent intent;
                if(selectedItem.equals("Received from SAP")){
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received from SAP");
                    intent.putExtra("hiddenTitle", "API Received from SAP");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Received from System Transfer Item")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received from System Transfer Item");
                    intent.putExtra("hiddenTitle", "API System Transfer Item");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Manual Received Item")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received Item");
                    intent.putExtra("hiddenTitle", "API Received Item");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Manual Transfer Item")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Transfer Item");
                    intent.putExtra("hiddenTitle", "API Transfer Item");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Sales")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Sales");
                    intent.putExtra("hiddenTitle", "API Menu Items");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Issue For Production")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Issue For Production");
                    intent.putExtra("hiddenTitle", "API Issue For Production");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Confirm Issue For Production")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Confirm Issue For Production");
                    intent.putExtra("hiddenTitle", "API Confirm Issue For Production");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Received from Production")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received from Production");
                    intent.putExtra("hiddenTitle", "API Received from Production");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Item Request")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Item Request");
                    intent.putExtra("hiddenTitle", "API Item Request");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Item Request For Transfer")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Item Request For Transfer");
                    intent.putExtra("hiddenTitle", "API Item Request For Transfer");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Production Order List")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Production Order List");
                    intent.putExtra("hiddenTitle", "API Production Order List");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Inventory Count")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Inventory Count");
                    intent.putExtra("hiddenTitle", "API Inventory Count");
                    startActivity(intent);
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Pull out Request")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Pull Out Request");
                    intent.putExtra("hiddenTitle", "API Pull Out Count");
                    startActivity(intent);
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Logout")){
                    onBtnLogout();
                }
                else if(selectedItem.equals("Logs")){
                    intent = new Intent(getBaseContext(), API_SalesLogs.class);
                    intent.putExtra("title", "Inventory Logs");
                    intent.putExtra("hiddenTitle", "API Inventory Logs");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Cut Off")){
                    intent = new Intent(getBaseContext(), CutOff.class);
                    intent.putExtra("title", "Cut Off");
                    intent.putExtra("hiddenTitle", "API Cut Off");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Inventory Confirmation")){
                    intent = new Intent(getBaseContext(), API_InventoryConfirmation.class);
                    intent.putExtra("title", "Inv. and P.O Count Confirmation");
                    intent.putExtra("hiddenTitle", "API Inventory Count Confirmation");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Change Password")){
                    changePassword();
                }
                else if(selectedItem.equals("Offline Pending Transactions")){
                    intent = new Intent(getBaseContext(), OfflineList.class);
                    intent.putExtra("title", "Offline Pending Transactions");
                    intent.putExtra("hiddenTitle", "API Offline List");
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }

    public void setupDrawer(){
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close){
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void genData(){
        List<String>title = navc.getTitles(getString(R.string.app_name));
        listChild = new TreeMap<>();
        int iterate = getString(R.string.app_name).equals("Atlantic Bakery") ? 5 : 4;
        int titleIndex = 0;
        while (iterate >= 0){
            listChild.put(title.get(titleIndex),navc.getItem(title.get(titleIndex)));
            titleIndex += 1;
            iterate -= 1;
        }
        listTitle = new ArrayList<>(listChild.keySet());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu item) {
        getMenuInflater().inflate(R.menu.main_menu,item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
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
