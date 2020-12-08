package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class API_ItemInfo extends AppCompatActivity {
    String title = "", hidden_title = "", itemName = "";
    TextView lblItem, lblPrice, lblReceivedQuantity, lblVariance, lblPercent;
    EditText txtDiscount, txtTotalPrice, txtQuantity;
    CheckBox checkFree;
    Button btnPlus, btnMinus, btnAddCart,btnBack;

    DatabaseHelper4 myDb4;
    DatabaseHelper3 myDb3;
    DatabaseHelper myDb;
    Double deliveredQuantity;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();

    Menu menu;
    long mLastClickTime;

    static boolean isSubmit = false;
    ProgressBar progressBar;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i__item_info);
        lblItem = findViewById(R.id.lblItemName);
        lblPrice = findViewById(R.id.lblPrice);
        lblReceivedQuantity = findViewById(R.id.lblReceivedQuantity);
        lblVariance = findViewById(R.id.lblVariance);
        lblPercent = findViewById(R.id.lblPercent);
        txtDiscount = findViewById(R.id.txtDisount);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        checkFree = findViewById(R.id.checkFree);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        txtQuantity = findViewById(R.id.txtQuantity);
        btnAddCart = findViewById(R.id.btnAddCart);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        deliveredQuantity = getIntent().getDoubleExtra("deliveredQuantity", 0);

        myDb = new DatabaseHelper(this);
        myDb4 = new DatabaseHelper4(this);
        myDb3 = new DatabaseHelper3(this);


        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");
        itemName = getIntent().getStringExtra("item");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));
        lblItem.setText(itemName);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
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
                        intent.putExtra("title", "Pull Out Count");
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


        btnMinus.setOnClickListener(view -> {
            txtQuantity.setText(minusPlus("-"));
            txtQuantity.setSelection(txtQuantity.getText().length());
        });
        btnPlus.setOnClickListener(view -> {
            txtQuantity.setText(minusPlus("+"));
            txtQuantity.setSelection(txtQuantity.getText().length());
        });

        btnAddCart.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            progressBar.setVisibility(View.VISIBLE);
            btnAddCart.setEnabled(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//            int qty = (txtQuantity.getText().toString().isEmpty() ? 0 : Integer.parseInt(txtQuantity.getText().toString()));
                    if ((txtQuantity.getText().toString().isEmpty()) && (hidden_title.equals("API Menu Items") || hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API System Transfer Item") || hidden_title.equals("API Item Request"))) {
                        Toast.makeText(getBaseContext(), "Input atleast 1 quantity", Toast.LENGTH_SHORT).show();
                        txtQuantity.requestFocus();
//                        progressBar.setVisibility(View.GONE);
//                        btnAddCart.setEnabled(true);
//            }else if (qty <= 0) {
//                Toast.makeText(getBaseContext(), "Input atleast 1 quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        double quantity = (txtQuantity.getText().toString().isEmpty() ? 0.00 : Double.parseDouble(txtQuantity.getText().toString()));
                        int int_quantity =  (txtQuantity.getText().toString().isEmpty() ? 0 : Integer.parseInt(txtQuantity.getText().toString()));
//                double double_quantity = getIntent().getDoubleExtra("quantity",0.0);
                        boolean isInserted = false;
                        if(hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request")){
                            isInserted = myDb4.insertData(itemName, quantity, title, 1);
                        }else if(hidden_title.equals("API Received from SAP")){
                            int id = getIntent().getIntExtra("id",0);
                            isInserted = myDb3.updateSelected(Integer.toString(id),1, quantity);
                        }else if(hidden_title.equals("API System Transfer Item")){
                            int id = getIntent().getIntExtra("id",0);
                            isInserted = myDb3.updateSelected(Integer.toString(id),1, quantity);
                        }
                        else if(hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")){
                            double double_quantity =  getIntent().getDoubleExtra("quantity",0.0);
                            isInserted = myDb3.insertData("", "", itemName, double_quantity, int_quantity, 0, "", 0,hidden_title,1);
                        }
                        else if(hidden_title.equals("API Menu Items")){
                            double discountPercent = (txtDiscount.getText().toString().isEmpty() ? 0.00 : Double.parseDouble(txtDiscount.getText().toString()));
                            double price = getIntent().getDoubleExtra("price",0.00);
                            int isFree = (checkFree.isChecked() ? 1 : 0);
                            double totalPrice = (txtTotalPrice.getText().toString().isEmpty() ? 0.00 : Double.parseDouble(txtTotalPrice.getText().toString()));
                            isInserted = myDb.insertData(quantity, discountPercent, price, isFree, totalPrice, itemName, "Main Inventory");
                        }
                        if (isInserted) {
                            isSubmit = true;
                            Toast.makeText(getBaseContext(), "Item Added", Toast.LENGTH_SHORT).show();
                            closeKeyboard();
//                    Intent intent;
//                    intent = new Intent(getBaseContext(), APIReceived.class);
//                    intent.putExtra("title", title);
//                    intent.putExtra("hiddenTitle", hidden_title);
//                    startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "Item Not Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    btnAddCart.setEnabled(true);
                }
            },500);
        });

        txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double received_quantity;
                int quantity;
                if (txtQuantity.getText().toString().isEmpty()) {
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(txtQuantity.getText().toString());
                }

                if(hidden_title.equals("API Menu Items")){
                    double price = Double.parseDouble(lblPrice.getText().toString().replace("₱",""));
                    double priceBefore = price * quantity;
                    double discount = (txtDiscount.getText().toString().isEmpty() ? 0.00 : Double.parseDouble(txtDiscount.getText().toString()));
                    if(!checkFree.isChecked()){
                        if(discount < 0){
                            txtTotalPrice.setText(Double.toString(priceBefore));
                        }else{
                            double discountedTotalPrice = (priceBefore - (discount / 100) * priceBefore);
                            txtTotalPrice.setText(Double.toString(discountedTotalPrice));
                        }
                    }else {
                        txtTotalPrice.setText("0.00");
                        txtDiscount.setText("0");
                    }
                }

                if(hidden_title.equals("API Received from SAP") || hidden_title.equals("API System Transfer Item")){
                    received_quantity = Double.parseDouble(lblReceivedQuantity.getText().toString().replace("Delivered Qty.: ", ""));
                    double variance = quantity - received_quantity;
                    lblVariance.setText("Variance: " + variance);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    btnAddCart.callOnClick();
                    return true;
                }
                return false;
            }
        });

//        txtDiscount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    btnAddCart.callOnClick();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        txtTotalPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    btnAddCart.callOnClick();
//                    return true;
//                }
//                return false;
//            }
//        });

        txtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title.equals("Menu Items")) {
                    if (txtDiscount.hasFocus()) {
                        double price = Double.parseDouble(lblPrice.getText().toString().replace("₱",""));
                        double discountPercent;
//                                , getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                        double qty = (txtQuantity.getText().toString().isEmpty() ? 0.00 : Double.parseDouble(txtQuantity.getText().toString()));
                        double priceBefore = qty * price;
                        try {
                            if (Double.parseDouble(txtDiscount.getText().toString()) > 25) {
                                txtDiscount.setText("25");
                                txtDiscount.setSelection(txtDiscount.getText().length() - 1);
                            }
                            if (txtDiscount.getText().toString().isEmpty()) {
                                txtDiscount.setText("0");
                                txtDiscount.setSelection(txtDiscount.getText().length());
                            } else if (Integer.parseInt(txtQuantity.getText().toString()) < 0) {
                                txtDiscount.setText("0");
                                txtDiscount.setSelection(txtDiscount.getText().length());
                            } else if (Integer.parseInt(txtQuantity.getText().toString()) > 0) {
                                double totalAmount;
                                if (!checkFree.isChecked()) {
                                    discountPercent = Double.parseDouble(txtDiscount.getText().toString());
                                    totalAmount = (priceBefore - (discountPercent / 100) * priceBefore);
                                    txtTotalPrice.setText(Double.toString(totalAmount));

                                }
                            }
                        } catch (Exception ex) {
                            if (txtDiscount.getText().toString().equals("")) {
                                txtTotalPrice.setText(Double.toString(priceBefore));
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtDiscount.setOnFocusChangeListener((view, b) -> {
            if(title.equals("Menu Items")) {
                if (!b) {
                    if (txtDiscount.getText().toString().isEmpty()) {
                        txtDiscount.setText("0");
                    }
                }
            }
        });

        txtTotalPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title.equals("Menu Items")) {
                    double price = Double.parseDouble(lblPrice.getText().toString().replace("₱",""));
//                    double getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                    double quantity = (txtQuantity.getText().toString().isEmpty()) ? 0.00  : Double.parseDouble(txtQuantity.getText().toString());
                    double priceBefore = quantity * price;
                    if (txtTotalPrice.hasFocus()) {
                        try {
                            if (!checkFree.isChecked()) {
                                if(txtTotalPrice.getText().toString().isEmpty()){
                                    txtDiscount.setText("100.0");
                                }
                                if (Double.parseDouble(txtTotalPrice.getText().toString()) > priceBefore) {
                                    double doubleTotalPrice = Double.parseDouble(txtTotalPrice.getText().toString());
                                    double calculatedDiscount = ((priceBefore - doubleTotalPrice) / priceBefore) * 100;
                                    txtDiscount.setText(Double.toString(calculatedDiscount));
                                    txtTotalPrice.setText(Double.toString(priceBefore));
                                    txtTotalPrice.setSelection(txtTotalPrice.getText().length());
                                } else {
                                    double doubleTotalPrice = Double.parseDouble(txtTotalPrice.getText().toString());
                                    double calculatedDiscount = ((priceBefore - doubleTotalPrice) / priceBefore) * 100;
                                    txtDiscount.setText(Double.toString(calculatedDiscount));
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtTotalPrice.setOnFocusChangeListener((view, b) -> {
            if(title.equals("Menu Items")) {
                if (!b) {
                    if (txtTotalPrice.getText().toString().isEmpty()) {
                        double price = Double.parseDouble(lblPrice.getText().toString().replace("₱",""));
//                            double getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                        double priceBefore = Double.parseDouble(txtQuantity.getText().toString()) * price;
                        txtTotalPrice.setText(Double.toString(priceBefore));
                        txtDiscount.setText("0.00");
                    }
                }
            }
        });

        checkFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txtDiscount.setText("0");
                    txtTotalPrice.setText("0.00");
                    txtDiscount.setEnabled(false);
                    txtTotalPrice.setEnabled(false);
                } else {
                    double price = Double.parseDouble(lblPrice.getText().toString().replace("₱",""));
//                        double getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                    double priceBefore = price * Double.parseDouble(txtQuantity.getText().toString());
                    txtTotalPrice.setText(Double.toString(priceBefore));
                    txtDiscount.setEnabled(true);
                    txtTotalPrice.setEnabled(true);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        isSubmit = false;

//        if(this.getWindow().getDecorView().getRootView().isShown()){
//
//        }

        int totalCart = myDb.countItems();
        MenuItem nav_ShoppingCart = menu.findItem(R.id.nav_shoppingCart);
        nav_ShoppingCart.setTitle("Shopping Cart (" + totalCart + ")");

        if (hidden_title.equals("API Received Item") || hidden_title.equals("API Received from SAP") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count") || hidden_title.equals("API System Transfer Item")) {
            checkFree.setVisibility(View.GONE);
            lblPrice.setVisibility(View.GONE);
            lblPercent.setVisibility(View.GONE);
            txtDiscount.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
            if (hidden_title.equals("API Received from SAP") || hidden_title.equals("API System Transfer Item")) {
                lblReceivedQuantity.setVisibility(View.VISIBLE);
                lblVariance.setVisibility(View.VISIBLE);
                lblReceivedQuantity.setText("Delivered Qty.: " + deliveredQuantity);
            }else if(hidden_title.equals("API Inventory Count")){
                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String isManager = sharedPreferences2.getString("isManager", "");
                if(Integer.parseInt(isManager) > 0){
                    lblReceivedQuantity.setVisibility(View.VISIBLE);
                    lblVariance.setVisibility(View.GONE);
                    lblReceivedQuantity.setText("Sales: " + getIntent().getIntExtra("store_quantity",0) + "\n" + "Auditor: " + getIntent().getIntExtra("auditor_quantity",0));
//                    lblVariance.setText("Variance: " + getIntent().getIntExtra("variance_quantity",0));
                }else{
                    lblReceivedQuantity.setVisibility(View.GONE);
                    lblVariance.setVisibility(View.GONE);
                }
            }else if(hidden_title.equals("API Pull Out Count")){
                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String isManager = sharedPreferences2.getString("isManager", "");
                if(Integer.parseInt(isManager) > 0){
                    lblReceivedQuantity.setVisibility(View.VISIBLE);
                    lblVariance.setVisibility(View.GONE);
                    lblReceivedQuantity.setText("Sales: " + getIntent().getIntExtra("store_quantity",0) + "\n" + "Auditor: " + getIntent().getIntExtra("auditor_quantity",0));
//                    lblVariance.setText("Variance: " + getIntent().getIntExtra("variance_quantity",0));
                }else{
                    lblReceivedQuantity.setVisibility(View.GONE);
                    lblVariance.setVisibility(View.GONE);
                }
            }
            else {
                lblReceivedQuantity.setVisibility(View.GONE);
                lblVariance.setVisibility(View.GONE);
                lblReceivedQuantity.setText("Received Qty.: " + deliveredQuantity);
            }
        } else {
            checkFree.setVisibility(View.GONE);
            lblPrice.setVisibility(View.VISIBLE);
            lblPrice.setText("₱" + getIntent().getDoubleExtra("price",0.00));
            lblReceivedQuantity.setVisibility(View.GONE);
            lblVariance.setVisibility(View.GONE);
            lblPercent.setVisibility(View.VISIBLE);
            txtDiscount.setVisibility(View.VISIBLE);
            txtTotalPrice.setVisibility(View.VISIBLE);
        }
        txtQuantity.requestFocus();
        showKeyboard();
    }



    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @SuppressLint("SetTextI18n")
    public String minusPlus(String operator) {
        int quantity;
        if(txtDiscount.getText().toString().isEmpty() && !checkFree.isChecked()){
            txtDiscount.setText("0");
        }
        if(txtTotalPrice.getText().toString().isEmpty() && !checkFree.isChecked()){
            txtTotalPrice.setText("0.00");
        }
        if (txtQuantity.getText().toString().isEmpty()) {
            quantity = 0;
        } else {
            quantity = Integer.parseInt(txtQuantity.getText().toString());
        }
        if (operator.equals("+")) {
            quantity += 1;
        } else {
            if (quantity > 0) {
                quantity -= 1;
            }
        }
        if (hidden_title.equals("API Received from SAP")) {
            double received_quantity = Double.parseDouble(lblReceivedQuantity.getText().toString().replace("Delivered Qty.: ", ""));
            double variance = quantity - received_quantity;
            lblVariance.setText("Variance: " + variance);
        }
        return Integer.toString(quantity);
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(API_ItemInfo.this);
                        pc.removeToken(API_ItemInfo.this);
                        startActivity(uic.goTo(API_ItemInfo.this, MainActivity.class));
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