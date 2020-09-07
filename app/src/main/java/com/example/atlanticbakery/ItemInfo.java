package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.navigation.NavigationView;
import java.util.Objects;

public class ItemInfo extends AppCompatActivity {
    TextView txtItemName;
    EditText txtQuantity;
    Button btnMinus, btnPlus, btnAddCart;
    String itemName;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    item_class itemc = new item_class();
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    DatabaseHelper myDb;
    DatabaseHelper2 myDb2;

    CheckBox checkFree;
    EditText txtDiscount;
    EditText txtTotalPrice;
    TextView lblPercent;
    TextView lblPrice;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);

        final String title = getIntent().getStringExtra("title");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        txtQuantity = findViewById(R.id.txtQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddCart = findViewById(R.id.btnAddCart);

        itemName = getIntent().getStringExtra("itemname");
        txtItemName = findViewById(R.id.itemName);
        txtItemName.setText(itemName);

        double price = itemc.returnItemNamePrice(ItemInfo.this, itemName);
        lblPrice = findViewById(R.id.lblPrice);
        lblPrice.setText("₱" + price);

        txtQuantity.setText("0");
        txtQuantity.setBackgroundResource(R.drawable.custom_edittext);

        NavigationView navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean result = false;
                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.nav_logOut :
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        onBtnLogout();
                        break;
                    case R.id.nav_scanItem :
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        startActivity(uic.goTo(ItemInfo.this, ScanQRCode.class));
                        finish();
                        break;
                    case R.id.nav_exploreItems :
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Menu Items");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_shoppingCart :
                        result = true;
                        drawerLayout.closeDrawer(Gravity.START, false);
                        startActivity(uic.goTo(ItemInfo.this, ShoppingCart.class));
                        finish();
                        break;
                    case R.id.nav_receivedProduction :
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Received from Production");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedProduction2 :
                        result = true;
                        intent = new Intent(getBaseContext(), Received.class);
                        intent.putExtra("title", "Received from Production");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedBranch :
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Received from Other Branch");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedBranch2 :
                        result = true;
                        intent = new Intent(getBaseContext(), Received.class);
                        intent.putExtra("title", "Received from Other Branch");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedSupplier :
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Received from Direct Supplier");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_receivedSupplier2 :
                        result = true;
                        intent = new Intent(getBaseContext(), Received.class);
                        intent.putExtra("title", "Received from Direct Supplier");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_transferOut :
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Transfer Out");
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_transferOut2 :
                        result = true;
                        intent = new Intent(getBaseContext(), Received.class);
                        intent.putExtra("title", "Transfer Out");
                        startActivity(intent);
                        finish();
                        break;
//                    case R.id.nav_adjusmentIn :
//                        result = true;
//                        intent = new Intent(getBaseContext(), Received.class);
//                        intent.putExtra("type", "Received from Adjustment");
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.nav_adjustmentOut :
//                        result = true;
//                        intent = new Intent(getBaseContext(), Received.class);
//                        intent.putExtra("type", "Adjustment Out");
//                        startActivity(intent);
//                        finish();
//                        break;
                    case R.id.nav_inventory :
                        result = true;
                        intent = new Intent(getBaseContext(), Inventory.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_cancelRecTrans :
                        result = true;
                        intent = new Intent(getBaseContext(), CancelRecTrans.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });

        checkFree = findViewById(R.id.checkFree);
        txtDiscount = findViewById(R.id.txtDisount);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        lblPercent = findViewById(R.id.lblPercent);
        if(title.equals("Menu Items")){
            checkFree.setVisibility(View.VISIBLE);
            txtDiscount.setVisibility(View.VISIBLE);
            txtTotalPrice.setVisibility(View.VISIBLE);
            lblPercent.setVisibility(View.VISIBLE);
        }else{
            checkFree.setVisibility(View.GONE);
            txtDiscount.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
            lblPercent.setVisibility(View.GONE);
        }

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
                        double getPrice = itemc.returnItemNamePrice(ItemInfo.this, txtItemName.getText().toString());
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
                if(Integer.parseInt(txtQuantity.getText().toString()) == 0){
                    txtQuantity.setText("1");
                }
                if(title.equals("Menu Items")) {
                    double getPrice = itemc.returnItemNamePrice(ItemInfo.this, txtItemName.getText().toString());
                    double priceBefore = getPrice * Double.parseDouble(txtQuantity.getText().toString());

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
                        double discountPercent, getPrice = itemc.returnItemNamePrice(ItemInfo.this, txtItemName.getText().toString());
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

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title.equals("Menu Items")) {
                    double getPrice = itemc.returnItemNamePrice(ItemInfo.this, txtItemName.getText().toString());
                    double priceBefore = Double.parseDouble(txtQuantity.getText().toString()) * getPrice;
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
            @Override
            public void onFocusChange(View view, boolean b) {
                if(title.equals("Menu Items")) {
                    if (!b) {
                        if (txtTotalPrice.getText().toString().isEmpty()) {
                            double getPrice = itemc.returnItemNamePrice(ItemInfo.this, txtItemName.getText().toString());
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
                boolean isItemNameExist = itemc.checkItemName(ItemInfo.this, itemName);
                boolean hasStock = itemc.checkItemNameStock(ItemInfo.this, itemName);
                int quantity;
                if(txtQuantity.getText().toString().isEmpty()){
                    quantity = 0;
                    txtQuantity.setText("0");
                }else{
                    quantity = Integer.parseInt(txtQuantity.getText().toString());
                }
                if (!isItemNameExist) {
                    Toast.makeText(ItemInfo.this, "item not found", Toast.LENGTH_SHORT).show();
                }else if(quantity <=0){
                    Toast.makeText(ItemInfo.this, "Add Quantity atleast 1", Toast.LENGTH_SHORT).show();
                }else if(!hasStock && title.equals("Menu Items")) {
                    final AlertDialog.Builder myDialog = new AlertDialog.Builder(ItemInfo.this);
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
                }else if(!hasStock && title.equals("Transfer Out")) {
                    final AlertDialog.Builder myDialog = new AlertDialog.Builder(ItemInfo.this);
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
                }else{
                    saveData();
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
        loadShoppingCartCount();
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    public void saveData() {
        checkItem checkItem = new checkItem();
        checkItem.execute("");
        Intent intent;
        String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
//        if(title.equals("Menu Items")){
//            intent = new Intent(getBaseContext(), ShoppingCart.class);
//            startActivity(intent);
//        }else{
//            intent = new Intent(getBaseContext(), Received.class);
//            intent.putExtra("title", title);
//            startActivity(intent);
//        }
        intent = new Intent(getBaseContext(), AvailableItems.class);
        intent.putExtra("title", title);
        startActivity(intent);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    public class checkItem extends AsyncTask<String, String, String> {
        String z = "";

        final LoadingDialog loadingDialog = new LoadingDialog(ItemInfo.this);

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            double discountPercent = Double.parseDouble(txtDiscount.getText().toString());
            double price = itemc.returnItemNamePrice(ItemInfo.this, itemName);
            int free;
            if(checkFree.isChecked()){
                free = 1;
            }else{
                free = 0;
            }
            int quantity = Integer.parseInt(txtQuantity.getText().toString());
            double quantity2 = Double.parseDouble(txtQuantity.getText().toString());
            double totalPrice = (free == 1 ? 0 : quantity * price);
            String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
            boolean isInserted;
            if(title.equals("Menu Items")) {
                isInserted = myDb.insertData(quantity2, discountPercent, price, free, totalPrice, itemName);
            }else {
                isInserted = myDb2.insertData(itemName, quantity, title);
            }

            if (isInserted) {
                z = "Item Added";
            } else {
                z = "Item Not Added";
            }
            return z;
        }

        @Override
        protected void onPostExecute(final String s) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ItemInfo.this, s, Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            };
            handler.postDelayed(r, 1000);
        }
    }

    public void loadShoppingCartCount(){
        NavigationView navigationView = findViewById(R.id.nav);
        Menu menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.nav_shoppingCart);
        int totalItems = myDb.countItems();
        nav_shoppingCart.setTitle("Shopping Cart (" + totalItems + ")");
    }
}