package com.example.atlanticbakery;

import android.annotation.SuppressLint;
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
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.atlanticbakery.Adapter.CustomExpandableListAdapter;
import com.example.atlanticbakery.Helper.FragmentNavigationManager_APIReceived;
import com.example.atlanticbakery.Interface.NavigationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.TreeMap;

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
    Spinner spinnerItemGroup;
    TextView lblItemGroup;

    String title, hidden_title;

    DatabaseHelper4 myDb4;
    DatabaseHelper3 myDb3;
    DatabaseHelper myDb;
    DatabaseHelper8 myDb8;
    DatabaseHelper7 myDb7;
    DatabaseHelper9 myDb9;

    DecimalFormat df = new DecimalFormat("#,###");

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    navigation_class navc = new navigation_class();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    //    private String[] items;

    private ExpandableListView expandableListView;
    private List<String> listTitle;
    private Map<String, List<String>> listChild;
    private NavigationManager navigationManager;


    long mLastClickTime = 0;
    private OkHttpClient client;
    JSONObject globalJsonObject;
    Button btnBack,btnRefresh;
    String appName = "";
    @SuppressLint({"WrongConstant", "SetTextI18n"})
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
        myDb9 = new DatabaseHelper9(this);
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
        lblItemGroup = findViewById(R.id.lblItemGroup);
        spinnerItemGroup = findViewById(R.id.spinnerItemGroup);
        btnRemove.setVisibility(View.GONE);
        appName = getString(R.string.app_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        GridLayout gridLayout = findViewById(R.id.grid);
        if(width <= 720){
            gridLayout.setColumnCount(2);
            gridLayout.setRowCount(2);
        }else{
            gridLayout.setColumnCount(3);
            gridLayout.setRowCount(3);
        }

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


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        expandableListView = (ExpandableListView)findViewById(R.id.navList);
        navigationManager = FragmentNavigationManager_APIReceived.getmInstance(this);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

        @SuppressLint("InflateParams") View listReaderView = getLayoutInflater().inflate(R.layout.nav_header, null,false);
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

        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                globalJsonObject = new JSONObject();
                mLastClickTime = SystemClock.elapsedRealtime();
                getItems(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if(hidden_title.equals("API Menu Items")){
            myItemGroups my = new myItemGroups();
            my.execute("");

            spinnerItemGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    globalJsonObject = new JSONObject();
                    loadData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

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
        ExpandableListAdapter adapter = new CustomExpandableListAdapter(this, listTitle, listChild);
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
        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }



    @SuppressLint("SetTextI18n")
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
        String password;
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

                Cursor cursor = myDb3.getAllData(hidden_title);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        final String fromBranch = (hidden_title.equals("API System Transfer Item") ? cursor.getString(8) : cursor.getString(2));
                        if(fromBranch.isEmpty()){
                            lblFromBranch.setVisibility(View.GONE);
                        }else{
                            lblFromBranch.setVisibility(View.VISIBLE);
                        }
                    }
                }
                cursor.close();
                btnBack.setVisibility(View.VISIBLE);
                lblSapNumber.setVisibility(View.VISIBLE);
                lblSelectedType.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                lblType.setVisibility(View.GONE);
                spinnerType.setVisibility(View.GONE);
                loadSelectedSAPNumberItems();
            } else {
                List<String> items;
                if(appName.equals("Atlantic Bakery")){
                    items = Arrays.asList("IT", "PO");
                }else{
                    items = Arrays.asList("IT", "PO","DN","AR");
                }
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

        } else if (hidden_title.equals("API System Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Received from Production") || hidden_title.equals("API Confirm Issue For Production") || hidden_title.equals("API Received from Item Request")) {
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
        }
        else if (hidden_title.equals("API Received Item")) {
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
            spinnerItemGroup.setVisibility(View.VISIBLE);
            lblItemGroup.setVisibility(View.VISIBLE);
            lblFromBranch.setVisibility(View.GONE);
            lblSapNumber.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            lblType.setVisibility(View.GONE);
            spinnerType.setVisibility(View.GONE);
            getItems(0);
        }else if (hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")) {
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

    private class myItemGroups extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();
                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPAddress = sharedPreferences2.getString("IPAddress", "");

                SharedPreferences sharedPreferences1 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = sharedPreferences1.getString("token", "");

//                System.out.println("IP Address: " + IPAddress);
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPAddress + "/api/item/item_grp/getall")
                        .addHeader("Authorization", "Bearer " + token)
                        .method("GET", null)
                        .build();
                Response response = null;

                response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if(s != null) {
                    if(s.substring(0,1).equals("{")){
//                        System.out.println("tite");
                        JSONObject jsonObject1 = new JSONObject(s);
                        String msg = jsonObject1.getString("message");
                        if (jsonObject1.getBoolean("success")) {
                            List<String> tenderTypes = new ArrayList<>();
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            tenderTypes.add("All");
                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                            String isSales = Objects.requireNonNull(sharedPreferences.getString("isSales", ""));
                            String isProduction = Objects.requireNonNull(sharedPreferences.getString("isProduction", ""));
                            for (int ii = 0; ii < jsonArray.length(); ii++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(ii);
//                                System.out.println("issales: " + isSales + "\n" + "isProduction: " + isProduction);
                                if(isSales.equals("1") && !isProduction.equals("1")){
                                    if(!jsonObject.getString("code").toLowerCase().equals("Raw materials and packaging".toLowerCase())){
                                        tenderTypes.add(jsonObject.getString("code"));
                                    }
                                }else if(!isSales.equals("1") && !isProduction.equals("1")){
                                    if(!jsonObject.getString("code").toLowerCase().equals("Raw materials and packaging".toLowerCase())){
                                        tenderTypes.add(jsonObject.getString("code"));
                                    }
                                }
                                else{
                                    tenderTypes.add(jsonObject.getString("code"));
                                }
//                                if(isSales.equals("1")){
//                                    if(jsonObject.getString("code").toLowerCase().equals("Raw materials and packaging".toLowerCase())){
//
//                                    }else{
//                                        tenderTypes.add(jsonObject.getString("code"));
//                                    }
//                                }else{
//                                    tenderTypes.add(jsonObject.getString("code"));
//                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(APIReceived.this, android.R.layout.simple_spinner_item, tenderTypes);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerItemGroup.setAdapter(adapter);
                            globalJsonObject = new JSONObject();
                            loadData();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    globalJsonObject = new JSONObject();
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getBaseContext(), "Validation \n" + msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    else{
                        globalJsonObject = new JSONObject();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Validation \n" + s, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Cursor cursor = myDb8.getAllData();
                            while (cursor.moveToNext()){
                                String module = cursor.getString(3);
//                                System.out.println("Moduleee: " + module);
                                if(module.contains("Item Group")){
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(cursor.getString(4));
                                        String msg = jsonObject1.getString("message");
                                        if (jsonObject1.getBoolean("success")) {
                                            List<String> tenderTypes = new ArrayList<>();
                                            tenderTypes.add("All");
                                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                            String isSales = Objects.requireNonNull(sharedPreferences.getString("isSales", ""));
                                            String isProduction = Objects.requireNonNull(sharedPreferences.getString("isProduction", ""));
                                            for (int ii = 0; ii < jsonArray.length(); ii++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(ii);
//                                                System.out.println("issales: " + isSales + "\n" + "isProduction: " + isProduction);
                                                if(isSales.equals("1") && !isProduction.equals("1")){
                                                    if(!jsonObject.getString("code").toLowerCase().equals("Raw materials and packaging".toLowerCase())){
                                                        tenderTypes.add(jsonObject.getString("code"));
                                                    }
                                                }else if(!isSales.equals("1") && !isProduction.equals("1")){
                                                    if(!jsonObject.getString("code").toLowerCase().equals("Raw materials and packaging".toLowerCase())){
                                                        tenderTypes.add(jsonObject.getString("code"));
                                                    }
                                                }
                                                else{
                                                    tenderTypes.add(jsonObject.getString("code"));
                                                }
                                            }
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(APIReceived.this, android.R.layout.simple_spinner_item, tenderTypes);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinnerItemGroup.setAdapter(adapter);
                                            globalJsonObject = new JSONObject();
                                            loadData();
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    globalJsonObject = new JSONObject();
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getBaseContext(), "Validation \n" + msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } catch (JSONException ex) {
                                        globalJsonObject = new JSONObject();
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        globalJsonObject = new JSONObject();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Validation \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        Toast.makeText(getBaseContext(), "RECEIVED", Toast.LENGTH_SHORT).show();
        int totalCart = myDb.countItems();
//        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
//        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");

        if (hidden_title.equals("API Received from SAP")) {
            lblSelectedType.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        }

        if(!hidden_title.equals("API Menu Items")){
            globalJsonObject = new JSONObject();
            loadData();
        }
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

    public void onBtnLogout(){
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

    public void uiItems2(int id, String itemName, String sapNumber, double quantity, String fromBranch,boolean isSelected,int receivedQuantity,String uom){
        GridLayout gridLayout = findViewById(R.id.grid);
        CardView cardView = new CardView(APIReceived.this);
        LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(300, 300);
        layoutParamsCv.setMargins(20, 10, 10, 10);
        cardView.setLayoutParams(layoutParamsCv);
        cardView.setRadius(12);
        cardView.setCardElevation(5);


        System.out.println("item: " + itemName + "\n quantity: " + quantity + "\n delivered: " + receivedQuantity);

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
//                System.out.println("receivedQuantity: " + receivedQuantity);
                intent.putExtra("receivedQuantity", receivedQuantity);
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
        layoutParamsItemLeft.setMargins(20, (hidden_title.equals("API Received from Production") || hidden_title.equals("API Received from Item Request") ? - 100 : -50), 0, 10);

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
        txtItemLeft.setText((hidden_title.equals("API Received from Production") || hidden_title.equals("API Received from Item Request") ? (hidden_title.equals("API Received from Item Request") ? "Delivered Qty.: " : "Planned Qty: ") + df.format(quantity).replace(".0","") + " " + uom + '\n' + "Received Qty: "  + df.format(receivedQuantity) : df.format(quantity)  + uom));
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

                    lblSapNumber.setText("#: " + sapNumber);
                    lblFromBranch.setText("Branch: " + fromBranch);
                    listItems.add(itemName);
                    String uom = cursor.getString(11);
                    int received_quantity = cursor.getInt(12);

                    if (!txtSearch.getText().toString().trim().isEmpty()) {
                        if (txtSearch.getText().toString().trim().toLowerCase().contains(itemName.toLowerCase())) {
                            uiItems2(id,itemName,sapNumber,quantity,fromBranch,isSelected,received_quantity,uom);
                        }
                    }else{
                        uiItems2(id,itemName,sapNumber,quantity,fromBranch,isSelected,received_quantity,uom);
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
        }else if(spinner.getSelectedItemPosition() == 1){
            appendURL = "/api/sapb1/podetails/" + docEntry;
        }else if(spinner.getSelectedItemPosition() == 2){
            appendURL = "/api/sapb1/dndetails/" + docEntry;
        }
        else{
            appendURL = "/api/sapb1/ardetails/" + docEntry;
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

                                itemName = jsonObject.getString("itemcode");
                                toBranch = jsonObject.getString("whscode");
                                quantity = jsonObject.getDouble("quantity");

                                int ss = 0;
                                if(spinner.getSelectedItemPosition() == 0){
                                    ss = 1;
                                }else if(spinner.getSelectedItemPosition() == 1){
                                    ss = 0;
                                }else if(spinner.getSelectedItemPosition() == 2) {
                                    ss = 2;
                                }else {
                                    ss = 3;
                                }
                                String uom = jsonObject.getString("unitmsr");
//                                System.out.println("UOM: " + uom);
//                                isSAPIT_int = (supplier.equals("")  ? 3 : ss);
                                isSAPIT_int = ss;
                                baseID = 0;
                                boolean isSuccess = myDb3.insertData(sap_number, fromBranch, itemName, quantity, 0, isSAPIT_int, toBranch, baseID,hidden_title,0,uom,0);
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
        String appendURL= "/api/inv/trfr/forrec/getdetails/" + id;
//        System.out.println(appendURL);
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
//                            System.out.println("array: " + jsonArray);
                            int countError = 0;
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        toBranch,
                                        uom;
                                Double quantity;

                                fromBranch = jsonObject.getString("from_whse");
                                itemName = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("quantity");
                                toBranch = jsonObject.getString("to_whse");
                                uom = jsonObject.getString("uom");
                                int int_quantity = jsonObject.getInt("quantity");
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0,uom,0);
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
                    } else if (hidden_title.equals("API Received from Item Request")) {
                        appendURL = "/api/inv/item_request/get_all";
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
                    } else if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 2) {
//                        globalJsonObject = new JSONObject();
                        appendURL = "/api/sapb1/getdn";
                    } else if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 3) {
//                        globalJsonObject = new JSONObject();
                        appendURL = "/api/sapb1/getar";
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
                    else if (hidden_title.equals("API Issue For Production")) {
                        appendURL = "/api/production/order/get_all?mode=issue";
                    }
                    else if (hidden_title.equals("API Received from Production")) {
                        appendURL = "/api/production/order/get_all?mode=receive";
                    }else if(hidden_title.equals("API Confirm Issue For Production")){
                        SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        String branch = sharedPreferences2.getString("branch", "");
                        appendURL = "/api/production/issue_for_prod/get_all?mode=confirm&docstatus=&branch=" + branch;
                    }

                    SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                    String IPaddress = sharedPreferences2.getString("IPAddress", "");

                    if(hidden_title.equals("API Menu Items")){
                        appendURL += "?item_group=" + ( spinnerItemGroup.getSelectedItem().toString().equals("All") ? "" : spinnerItemGroup.getSelectedItem().toString());
                    }

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
                                    MyAppendData myAppendData = new MyAppendData(sResult,false);
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
                        MyAppendData myAppendData = new MyAppendData(globalJsonObject.toString(),false);
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
                    if(!module.equals("Item Group")) {
                        globalJsonObject = new JSONObject(cursor.getString(4));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if(!module.equals("Item Group")) {
                    MyAppendData myAppendData = new MyAppendData(cursor.getString(4),false);
                    myAppendData.execute("");
                }
            }
        }
    }

    private class MyAppendData extends AsyncTask<String, Void, String> {
        String sResult = "";
        boolean sItemGroup = false;
        public MyAppendData(String result,boolean isItemGroup){
            sResult = result;
            sItemGroup = isItemGroup;
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
                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String isSales = Objects.requireNonNull(sharedPreferences.getString("isSales", ""));
                String isProduction = Objects.requireNonNull(sharedPreferences.getString("isProduction", ""));
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
                                    String item;
                                    String uom = "";
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
                                            item = jsonObject1.has("item_code") ? jsonObject1.getString("item_code") : jsonObject1.getString("code");
//                                    JSONObject jsonObjectItem = jsonObject1.getJSONObject("item");
                                            if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item")) {
                                                price = jsonObject1.getDouble("price");
                                                uom = jsonObject1.getString("uom");
                                            }
                                            if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Inventory Count")) {

                                                stockQuantity = jsonObject1.isNull("quantity") ? 0.00 : jsonObject1.getDouble("quantity");
                                                uom = jsonObject1.getString("uom");
                                            } else if (hidden_title.equals("API Pull Out Count") && Integer.parseInt(isManager) <= 0) {
                                                stockQuantity = jsonObject1.getDouble("quantity");
                                                uom = jsonObject1.getString("uom");
                                            }

                                            if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Inventory Count")) {
                                                store_quantity = jsonObject1.getInt("sales_count");
                                                auditor_quantity = jsonObject1.getInt("auditor_count");
                                                variance_quantity = jsonObject1.getInt("variance");
                                                uom = jsonObject1.getString("uom");
                                            }

                                            if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")) {
                                                store_quantity = jsonObject1.getInt("sales_count");
                                                auditor_quantity = jsonObject1.getInt("auditor_count");
                                                variance_quantity = jsonObject1.getInt("variance");
                                                uom = jsonObject1.getString("uom");
                                            }

                                            break;
                                        case "API System Transfer Item":
                                        case "API Issue For Production":
                                        case "API Received from Production":
                                        case "API Confirm Issue For Production":
                                        case "API Received from Item Request":
                                        case "API ":
                                            item = jsonObject1.getString("reference");
                                            docEntry1 = jsonObject1.getInt("id");
                                            break;
                                        default:
                                            item = jsonObject1.getString("docnum");
                                            docEntry1 = jsonObject1.getInt("docentry");
                                            break;
                                    }
                                    String supplier = "";
                                    if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 1) {
                                        supplier = jsonObject1.getString("cardcode");
                                    }
                                    System.out.println("anu hidden title mo?  " + hidden_title);
                                    if(hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request")){
                                        stockQuantity -= myDb7.getDecreaseQuantity(item);
                                        stockQuantity += myDb7.getIncreaseQuantity(item);
                                        uom = jsonObject1.has("uom") ? jsonObject1.getString("uom") : "";
                                    }
                                    if(hidden_title.equals("API Menu Items")){
                                        if(isSales.equals("1") && !isProduction.equals("1")){
                                            if(!jsonObject1.getString("item_group").toLowerCase().equals("Raw materials and packaging".toLowerCase())){
                                                if(!spinnerItemGroup.getSelectedItem().toString().equals("All")){
                                                    if(spinnerItemGroup.getSelectedItem().toString().equals(jsonObject1.getString("item_group"))){
                                                        listItems.add(item);
                                                        loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom);
                                                    }
                                                }else{
                                                    listItems.add(item);
                                                    loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom);
                                                }
                                            }
                                        }else if(!isSales.equals("1") && !isProduction.equals("1")){
                                            if(!jsonObject1.getString("item_group").toLowerCase().equals("Raw materials and packaging".toLowerCase())){
                                                if(!spinnerItemGroup.getSelectedItem().toString().equals("All")){
                                                    if(spinnerItemGroup.getSelectedItem().toString().equals(jsonObject1.getString("item_group"))){
                                                        listItems.add(item);
                                                        loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom);
                                                    }
                                                }else{
                                                    listItems.add(item);
                                                    loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom);
                                                }
                                            }
                                        }
                                        else{
                                            if(!spinnerItemGroup.getSelectedItem().toString().equals("All")){
                                                if(spinnerItemGroup.getSelectedItem().toString().equals(jsonObject1.getString("item_group"))){
                                                    listItems.add(item);
                                                    loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom);
                                                }
                                            }else{
                                                listItems.add(item);
                                                loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom);
                                            }
                                        }
                                    }else{
                                        listItems.add(item);
                                        loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom);
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

    public void loadUIItems(String item, double price, double stockQuantity, int docEntry1, String supplier, int store_quantity, int auditor_quantity, int variance_quantity, String uom){
        if (!txtSearch.getText().toString().trim().isEmpty()) {
            if (txtSearch.getText().toString().trim().contains(item)) {
                uiItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity,uom);
            }
        }else{



            uiItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity,uom);
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
    public void uiItems(String item, Double price, Double stockQuantity, int docEntry1, String supplier, int store_quantity, int auditor_quantity, int variance_quantity,String uom) {
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
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
                }

            } else if (hidden_title.equals("API Received Item")) {
                if (myDb4.checkItem(item, title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
                }
            } else if (hidden_title.equals("API Transfer Item")) {
                if (myDb4.checkItem(item, title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
                }
            } else if (hidden_title.equals("API Item Request")) {
                if (myDb4.checkItem(item, title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
                }
            } else if (hidden_title.equals("API Inventory Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
                }
            }
            else if (hidden_title.equals("API Received from Production")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
                }
            }
            else if (hidden_title.equals("API Pull Out Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
                }
            } else {
                anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom);
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
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Transfer Item")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Item Request")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                }
            } else if (hidden_title.equals("API Menu Items")) {
                if (myDb.checkItem(item)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Inventory Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Pull Out Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            }
            linearLayout.addView(txtItemLeft);
        }
    }

    public void anotherFunction(String finalItem, double finalPrice, Integer finalDocEntry, String finalSupplier,double quantity,int store_quantity, int auditor_quantity, int variance_quantity,String uom){
        if (hidden_title.equals("API Received Item") || hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")) {
            Intent intent;
            intent = new Intent(getBaseContext(), API_ItemInfo.class);
            intent.putExtra("title", title);
            intent.putExtra("hiddenTitle", hidden_title);
            intent.putExtra("item", finalItem);
            intent.putExtra("quantity", quantity);
            intent.putExtra("uom", uom);
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
        } else if (hidden_title.equals("API Received from SAP") || hidden_title.equals("API System Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Received from Production")  || hidden_title.equals("API Confirm Issue For Production") || hidden_title.equals("API Received from Item Request")) {
            AlertDialog.Builder myDialog = new AlertDialog.Builder(APIReceived.this);
            myDialog.setCancelable(false);
            myDialog.setTitle("Confirmation");
//            System.out.println("ID: " + finalDocEntry);
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
                    }else if(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Confirm Issue For Production")){
                        insertIssueProduction(finalDocEntry, finalItem);
                    }
                    else if(hidden_title.equals("API Received from Production")){
//                        System.out.println("hidden: " + hidden_title);
                        insertReceivedProduction(finalDocEntry, finalItem);
                    }
                    else if(hidden_title.equals("API Received from Item Request")){
//                        System.out.println("hidden: " + hidden_title);
                        insertReceivedItemRequest(finalDocEntry, finalItem);
                    }
                    else{
                        insertSystemTransfer(finalDocEntry, finalItem);
                    }
                }
            });
            myDialog.setNegativeButton("No", (dialogInterface, i1) -> dialogInterface.dismiss());
            myDialog.show();
        }
    }

    public void insertReceivedProduction(int id, String referenceNumber){
        String appendURL= "/api/production/order/details/" + id + "?mode=receive";
//        System.out.println("URL: " +  appendURL);
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
//                            System.out.println("array: " + jsonArray);
                            int countError = 0;
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        toBranch,
                                        uom;
                                Double quantity;

                                fromBranch = jsonObject.getString("whsecode");
                                itemName = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("planned_qty");
                                toBranch = jsonObject.getString("whsecode");
                                uom = jsonObject.getString("uom");
                                int int_quantity = jsonObject.getInt("planned_qty");
                                int int_received_quantity = jsonObject.isNull("received_qty") ? 0 : jsonObject.getInt("received_qty");
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0,uom,int_received_quantity);
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

    public void insertReceivedItemRequest(int id, String referenceNumber){
        String appendURL= "/api/inv/item_request/details/" + id;
//        System.out.println("URL: " +  appendURL);
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPaddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject jsonObjectData;
                            jsonObjectData = response.getJSONObject("data");
//                            System.out.println("array: " + jsonArray);
                            int countError = 0;
                            JSONArray jsonArray = jsonObjectData.getJSONArray("request_rows");
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        toBranch,
                                        uom;
                                Double quantity;

                                fromBranch = jsonObject.getString("from_whse");
                                itemName = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("quantity");
                                toBranch = jsonObject.getString("to_whse");
                                uom = jsonObject.getString("uom");
                                int int_quantity = jsonObject.getInt("quantity");
                                int int_received_quantity = jsonObject.isNull("deliverqty") ? 0 : jsonObject.getInt("deliverqty");
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0,uom,int_received_quantity);
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

    public void insertIssueProduction(int id, String reference){
        String appendURL= (hidden_title.equals("API Issue For Production") ? "/api/production/item_to_issue/get_all/" : "/api/production/issue_for_prod/details/") + id + (hidden_title.equals("API Issue For Production") ? "" : "?mode=confirm");
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPAddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPAddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            boolean isSuccess = myDb9.insertData(appendURL,"GET", "Issue For Production", response.toString(),currentDate);
                            if(isSuccess){
                                Toast.makeText(getBaseContext(), reference + " added" , Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(), API_IssueProductionItems.class);
                                intent.putExtra("title", title);
                                intent.putExtra("id", id);
                                intent.putExtra("reference", reference);
                                intent.putExtra("hiddenTitle", hidden_title);
                                startActivity(intent);
                                finish();
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