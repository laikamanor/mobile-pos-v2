package com.example.atlanticbakery;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.Objects;


public class ItemInfo extends AppCompatActivity {
    CheckBox checkFree;
    TextView lblItem, lblPrice,lblPercent,lblVariance,lblReceivedQuantity;
    EditText txtQuantity,txtDiscount,txtTotalPrice;
    Button btnMinus,btnPlus,btnAddCart;

    String itemName,received_quantity;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    item_class itemc = new item_class();
    user_class uc = new user_class();
    received_class rc = new received_class();
    actualendbal_class ac = new actualendbal_class();
    receivedsap_class recsap = new receivedsap_class();

    DatabaseHelper myDb;
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DatabaseHelper4 myDb4;
    DatabaseHelper5 myDb5;

    DecimalFormat df = new DecimalFormat("#,###.##");
    String inv_type,trans_type;
    @SuppressLint({"CutPasteId", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);
        myDb3 = new DatabaseHelper3(this);
        myDb4 = new DatabaseHelper4(this);
        myDb5 = new DatabaseHelper5(this);

        checkFree = findViewById(R.id.checkFree);
        lblItem = findViewById(R.id.itemName);
        lblPrice = findViewById(R.id.lblPrice);
        lblReceivedQuantity = findViewById(R.id.lblReceivedQuantity);
        txtQuantity = findViewById(R.id.txtQuantity);
        lblPercent = findViewById(R.id.lblPercent);
        lblVariance = findViewById(R.id.lblVariance);
        txtQuantity =  findViewById(R.id.txtQuantity);
        txtDiscount = findViewById(R.id.txtDisount);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddCart = findViewById(R.id.btnAddCart);

        final String title = getIntent().getStringExtra("title");
        itemName = getIntent().getStringExtra("itemname");
        received_quantity = getIntent().getStringExtra("quantity");
        inv_type = getIntent().getStringExtra("inventory_type");
        trans_type = getIntent().getStringExtra("transfer_type");
        System.out.println("Inventory Type: " + inv_type);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean isStoreExist = ac.isTypeExist(ItemInfo.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(ItemInfo.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(ItemInfo.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(ItemInfo.this, "PO Store Count");
                boolean isAuditorPullOutExist = ac.isTypeExist(ItemInfo.this, "PO Auditor Count");
                boolean isFinalPullOutExist = ac.isTypeExist(ItemInfo.this, "PO Final Count");
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
                        startActivity(uic.goTo(ItemInfo.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(ItemInfo.this, ShoppingCart.class));
                        finish();
                        break;
                    case R.id.nav_receivedProduction:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Manual Received from Production");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedBranch:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Manual Received from Other Branch");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedSupplier:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Manual Received from Direct Supplier");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_transferOut2:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Manual Transfer Out");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_storeCountListPullOut:
                        if(isAuditorPullOutExist && isStorePullOutExist && isFinalPullOutExist){
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
                        if(isAuditorPullOutExist && isStorePullOutExist && isFinalPullOutExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (isAuditorPullOutExist) {
                            Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
                        }else{
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "PO Auditor Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_finalCountListPullOut:
                        if(isAuditorPullOutExist && isStorePullOutExist && isFinalPullOutExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (!isAuditorPullOutExist & !isStorePullOutExist) {
                            Toast.makeText(getBaseContext(), "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
                        }else if(!uc.returnWorkgroup(ItemInfo.this).equals("Manager")){
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
                        if(isAuditorExist && isStoreExist && isFinalExist){
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
                        if(isAuditorExist && isStoreExist && isFinalExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (isAuditorExist) {
                            Toast.makeText(getBaseContext(), "You have already Auditor Count", Toast.LENGTH_SHORT).show();
                        }else{
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "AC Auditor Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_finalCountList:
                        if(isAuditorExist && isStoreExist && isFinalExist){
                            Toast.makeText(getBaseContext(), "You have already Final Count", Toast.LENGTH_SHORT).show();
                        }else if (!isAuditorExist & !isStoreExist) {
                            Toast.makeText(getBaseContext(), "Finish Store and Audit First", Toast.LENGTH_SHORT).show();
                        }else if(!uc.returnWorkgroup(ItemInfo.this).equals("Manager")){
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

        assert title != null;
        if(title.matches("Menu Items")){
            checkFree.setVisibility(View.GONE);
            lblItem.setVisibility(View.VISIBLE);
            lblPrice.setVisibility(View.VISIBLE);
            lblReceivedQuantity.setVisibility(View.GONE);
            lblPercent.setVisibility(View.VISIBLE);
            lblVariance.setVisibility(View.GONE);
            txtQuantity.setVisibility(View.VISIBLE);
            txtDiscount.setVisibility(View.VISIBLE);
            txtTotalPrice.setVisibility(View.VISIBLE);
            btnMinus.setVisibility(View.VISIBLE);
            btnPlus.setVisibility(View.VISIBLE);
            btnAddCart.setVisibility(View.VISIBLE);
        }else if(title.matches("Received from SAP")){
            checkFree.setVisibility(View.GONE);
            lblItem.setVisibility(View.VISIBLE);
            lblPrice.setVisibility(View.GONE);
            lblReceivedQuantity.setVisibility(View.VISIBLE);
            lblPercent.setVisibility(View.GONE);
            lblVariance.setVisibility(View.VISIBLE);
            txtQuantity.setVisibility(View.VISIBLE);
            txtDiscount.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
            btnMinus.setVisibility(View.VISIBLE);
            btnPlus.setVisibility(View.VISIBLE);
            btnAddCart.setVisibility(View.VISIBLE);
        }else{
//            int visibility = (title.equals("Auditor Count List Items") || title.equals("Store Count List Items")) ? View.VISIBLE : View.GONE;
            lblReceivedQuantity.setVisibility(View.GONE);
            lblVariance.setVisibility(View.GONE);
            checkFree.setVisibility(View.GONE);
            lblItem.setVisibility(View.VISIBLE);
            lblPrice.setVisibility(View.GONE);
            lblPercent.setVisibility(View.GONE);
            txtQuantity.setVisibility(View.VISIBLE);
            txtDiscount.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
            btnMinus.setVisibility(View.VISIBLE);
            btnPlus.setVisibility(View.VISIBLE);
            btnAddCart.setVisibility(View.VISIBLE);
        }

        lblItem.setText(itemName);
        double price = itemc.returnItemNamePrice(this, itemName);
        lblPrice.setText("₱" + df.format(price));
//        lblReceivedQuantity.setText("Delivered Quantity: " + received_quantity);
        lblReceivedQuantity.setText((title.equals("AC Auditor Count List Items") || title.equals("AC Store Count List Items")) ? "Available Items: " + received_quantity : "Delivered Quantity: " + received_quantity);

        if(title.equals("Menu Items")) {
            checkFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        txtDiscount.setText("0");
                        txtTotalPrice.setText("0.00");
                        txtDiscount.setEnabled(false);
                        txtTotalPrice.setEnabled(false);
                    } else {
                        double getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                        double priceBefore = getPrice * Double.parseDouble(txtQuantity.getText().toString());
                        txtTotalPrice.setText(Double.toString(priceBefore));
                        txtDiscount.setEnabled(true);
                        txtTotalPrice.setEnabled(true);
                    }
                }
            });
        }

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtQuantity.setText(minusPlus("-"));
                txtQuantity.setSelection(txtQuantity.getText().length());
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtQuantity.setText(minusPlus("+"));
                txtQuantity.setSelection(txtQuantity.getText().length());
            }
        });

        txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title.equals("Menu Items")) {
                    double getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                    double quantity = (txtQuantity.getText().toString().isEmpty()) ? 0.00 : Double.parseDouble(txtQuantity.getText().toString());
                    double priceBefore = getPrice * quantity;

                    if(!checkFree.isChecked()){
                        if(Double.parseDouble(txtDiscount.getText().toString()) < 0){
                            txtTotalPrice.setText(Double.toString(priceBefore));
                        }else{
                            double discountedTotalPrice = (priceBefore - (Double.parseDouble(txtDiscount.getText().toString()) / 100) * priceBefore);
                            txtTotalPrice.setText(Double.toString(discountedTotalPrice));
                        }
                    }else {
                        txtTotalPrice.setText("0.00");
                        txtDiscount.setText("0");
                    }
                }else if(title.equals("Received from SAP") || title.equals("AC Auditor Count List Items") || title.equals("AC Store Count List Items")){
                    int quantity;
                    if(txtQuantity.getText().toString().isEmpty()){
                        quantity = 0;
                    }else{
                        quantity= Integer.parseInt(txtQuantity.getText().toString());
                    }
                    double received_quantity;
                    if(title.equals("AC Auditor Count List Items") || title.equals("AC Store Count List Items")){
                        received_quantity = Double.parseDouble(lblReceivedQuantity.getText().toString().replace("Available Items: ", ""));
                    }else{
                        received_quantity = Double.parseDouble(lblReceivedQuantity.getText().toString().replace("Delivered Quantity: ", ""));
                    }
                    double variance = quantity - received_quantity;
                    lblVariance.setText("Variance: " + variance);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(txtQuantity.getText().toString().isEmpty()){
                        txtQuantity.setText("0");
                    }
                }
            }
        });

        txtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title.equals("Menu Items")) {
                    if (txtDiscount.hasFocus()) {
                        double discountPercent, getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                        double priceBefore = Double.parseDouble(txtQuantity.getText().toString()) * getPrice;
                        try {
                            if (Double.parseDouble(txtDiscount.getText().toString()) > 25) {
                                txtDiscount.setText("25.00");
                                txtDiscount.setSelection(txtDiscount.getText().length() - 3);
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

        txtDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(title.equals("Menu Items")) {
                    if (!b) {
                        if (txtDiscount.getText().toString().isEmpty()) {
                            txtDiscount.setText("0");
                        }
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
                    double getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                    double quantity = (txtQuantity.getText().toString().isEmpty()) ? 0.00  : Double.parseDouble(txtQuantity.getText().toString());
                    double priceBefore = quantity * getPrice;
                    if (txtTotalPrice.hasFocus()) {
                        try {
                            if (!checkFree.isChecked()) {
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

        txtTotalPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFocusChange(View view, boolean b) {
                if(title.equals("Menu Items")) {
                    if (!b) {
                        if (txtTotalPrice.getText().toString().isEmpty()) {
                            double getPrice = itemc.returnItemNamePrice(ItemInfo.this, lblItem.getText().toString());
                            double priceBefore = Double.parseDouble(txtQuantity.getText().toString()) * getPrice;
                            txtTotalPrice.setText(Double.toString(priceBefore));
                            txtDiscount.setText("0.00");
                        }
                    }
                }
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog loadingDialog = new LoadingDialog(ItemInfo.this);
                loadingDialog.startLoadingDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isItemNameExist = itemc.checkItemName(ItemInfo.this, itemName);
                        int quantity;
                        if(txtQuantity.getText().toString().isEmpty()){
                            quantity = 0;
                            txtQuantity.setText("0");
                        }else{
                            quantity = Integer.parseInt(txtQuantity.getText().toString());
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        int userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
                        boolean hasStock = itemc.checkItemNameStock(ItemInfo.this, itemName, Double.parseDouble(String.valueOf(quantity)));
                        boolean hasHaving = (trans_type != null && trans_type.equals("Transfer from Sales"));
                        double OwnStockQuantity = rc.checkOwnInventoryStock(ItemInfo.this, "Transfer to Sales",userID, itemName,hasHaving);
                        if (!isItemNameExist) {
                            Toast.makeText(ItemInfo.this, "item not found", Toast.LENGTH_SHORT).show();
                        }else if(quantity <=0 && !title.equals("AC Store Count List Items") && !title.equals("AC Auditor Count List Items") && !title.equals("PO Store Count List Items") && !title.equals("PO Auditor Count List Items")){
                            Toast.makeText(ItemInfo.this, "Add Quantity atleast 1", Toast.LENGTH_SHORT).show();
                        }else if(!hasStock && title.equals("Menu Items") && inv_type != null && inv_type.equals("Main Inventory")) {
                            Toast.makeText(ItemInfo.this, "Insufficient Supply", Toast.LENGTH_SHORT).show();
                        }else if(OwnStockQuantity <= 0 && title.equals("Menu Items") && inv_type != null && inv_type.equals("Own Inventory")) {
                            Toast.makeText(ItemInfo.this, "Insufficient Supply", Toast.LENGTH_SHORT).show();
                        }else if(OwnStockQuantity < quantity && title.equals("Menu Items") && inv_type != null && inv_type.equals("Own Inventory")) {
                            Toast.makeText(ItemInfo.this, "Insufficient Supply", Toast.LENGTH_SHORT).show();
                        }else if(!hasStock && title.equals("Manual Transfer Out") && trans_type.equals("Transfer to Other Branch")) {
                            Toast.makeText(ItemInfo.this, "Insufficient Supply", Toast.LENGTH_SHORT).show();
                        }else if(OwnStockQuantity <= 0 && trans_type != null && trans_type.equals("Transfer from Sales")) {
                            Toast.makeText(ItemInfo.this, "Insufficient Supply", Toast.LENGTH_SHORT).show();
                        }else if(!hasStock && trans_type != null && trans_type.equals("Transfer to Sales")) {
                            Toast.makeText(ItemInfo.this, "Insufficient Supply", Toast.LENGTH_SHORT).show();
                        }else if(OwnStockQuantity < quantity && trans_type != null && trans_type.equals("Transfer from Sales")) {
                            Toast.makeText(ItemInfo.this, "Insufficient Supply", Toast.LENGTH_SHORT).show();
                        }else{
                            double discountPercent = Double.parseDouble(txtDiscount.getText().toString());
                            double price = itemc.returnItemNamePrice(ItemInfo.this, itemName);
                            int free;
                            if(checkFree.isChecked()){
                                free = 1;
                            }else{
                                free = 0;
                            }
                            int quantity3;
                            double quantityy;
                            if(txtQuantity.getText().toString().isEmpty()){
                                quantity3 = 0;
                                quantityy = 0.00;
                            }else{
                                quantity3 = Integer.parseInt(txtQuantity.getText().toString());
                                quantityy = Double.parseDouble(txtQuantity.getText().toString());
                            }
                            double discountAmount = (price * quantity3) * (discountPercent / 100);
                            double totalPrice = (free == 1 ? 0 : (price * quantity3) - discountAmount);
                            String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
                            boolean isInserted;
                            if(title.equals("Menu Items")) {
                                isInserted = myDb.insertData(quantityy, discountPercent, price, free, totalPrice, itemName,inv_type);
                            }else if(title.equals("Received from SAP")){
                                int id = getIntent().getIntExtra("id",0);
                                isInserted = myDb3.updateSelected(Integer.toString(id),1, quantityy);
                            }else if(title.equals("AC Auditor Count List Items") || title.equals("AC Store Count List Items") || title.equals("PO Auditor Count List Items") || title.equals("PO Store Count List Items")) {
                                String type = "";
                                if (title.equals("AC Auditor Count List Items")) {
                                    type = "Auditor Count";
                                } else if (title.equals("AC Store Count List Items")) {
                                    type = "Store Count";
                                } else if (title.equals("PO Auditor Count List Items")) {
                                    type = "PO Auditor Count";
                                } else if (title.equals("PO Store Count List Items")) {
                                    type = "PO Store Count";
                                }
                                isInserted = myDb4.insertData(itemName, Double.parseDouble(String.valueOf(quantity3)), type, 1);
                            }else {
                                isInserted = myDb2.insertData(itemName, quantity3, title);
                            }

                            if (isInserted) {
                                Toast.makeText(getBaseContext(),"Item Added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getBaseContext(),"Item Not Added", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent;
                            if(title.equals("Received from SAP")){
                                intent = new Intent(getBaseContext(), ReceivedSap.class);
                            }else{
                                intent = new Intent(getBaseContext(), AvailableItems.class);
                                intent.putExtra("transfer_type", trans_type);
                                intent.putExtra("inventory_type", inv_type);
                            }
                            finish();
                            intent.putExtra("title", title);
                            startActivity(intent);
                            finish();
                        }
                        loadingDialog.dismissDialog();
                    }
                },500);
            }
        });
    }


    public String minusPlus(String operator){
        int quantity;
        if(txtQuantity.getText().toString().isEmpty()){
            quantity = 0;
        }else{
            quantity = Integer.parseInt(txtQuantity.getText().toString());
        }
        if(operator.equals("+")){
            quantity += 1;
        }else {
            if(quantity > 0){
                quantity -= 1;
            }
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
                        pc.loggedOut(ItemInfo.this);
                        startActivity(uic.goTo(ItemInfo.this, MainActivity.class));
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
        loadCount();
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadCount(){
        Menu menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.nav_shoppingCart);
        MenuItem nav_ReceivedSAP = menu.findItem(R.id.nav_receivedSap);
        int totalCart = myDb.countItems();
        int totalPendingSAP = recsap.returnPendingSAPNotif(ItemInfo.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }

}