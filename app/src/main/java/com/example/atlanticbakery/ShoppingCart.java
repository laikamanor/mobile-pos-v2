package com.example.atlanticbakery;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class ShoppingCart extends AppCompatActivity {
    Connection con;
    DatabaseHelper myDb;
    String discountID = "";
    String discountName = "";
    long mLastClickTime = 0;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    shoppingCart_class sc = new shoppingCart_class();
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    user_class uc = new user_class();
    connection_class cc = new connection_class();
    DecimalFormat df = new DecimalFormat("#,###.00");
    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        myDb = new DatabaseHelper(this);


        final NavigationView navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        loadShoppingCartCount();

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
                        startActivity(uic.goTo(ShoppingCart.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(ShoppingCart.this, ShoppingCart.class));
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

        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).setTitle(Html.fromHtml("<font color='#ffffff'>Shopping Cart</font>"));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        loadData();
    }

    public void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(ShoppingCart.this);
                        startActivity(uic.goTo(ShoppingCart.this, MainActivity.class));
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

    public void loadShoppingCartCount(){
        NavigationView navigationView = findViewById(R.id.nav);
        Menu menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.nav_shoppingCart);
        int totalItems = myDb.countItems();
        nav_shoppingCart.setTitle("Shopping Cart (" + totalItems + ")");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        loadShoppingCartCount();
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"SetTextI18n", "ResourceType", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadData() {
        final LinearLayout layout = findViewById(R.id.parentLayout);
        layout.removeAllViews();
        final LinearLayout.LayoutParams layoutParamsNoItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsNoItems.setMargins(50, 20, 50, 0);
        final LinearLayout.LayoutParams layoutParamsLblError = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int totalItems = myDb.countItems();
        if (totalItems <= 0) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(layoutParamsNoItems);
            imageView.setImageResource(R.drawable.ic_sad_face);
            layout.addView(imageView);
            TextView lblNoItemFound = new TextView(this);
            lblNoItemFound.setLayoutParams(layoutParamsNoItems);
            lblNoItemFound.setText("Your Shopping Cart is currently empty. Tap the button below to shop for items.");
            lblNoItemFound.setBackgroundColor(Color.WHITE);
            lblNoItemFound.setTextSize(15);
            lblNoItemFound.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            layout.addView(lblNoItemFound);

            Button btnGotoShoppingCart = new Button(this);
            btnGotoShoppingCart.setText("Go Menu Items");
            btnGotoShoppingCart.setBackgroundResource(R.color.colorPrimary);
            btnGotoShoppingCart.setLayoutParams(layoutParamsNoItems);
            btnGotoShoppingCart.setTextColor(Color.WHITE);

            btnGotoShoppingCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent;
                    intent = new Intent(getBaseContext(), AvailableItems.class);
                    intent.putExtra("title", "Menu Items");
                    startActivity(intent);
                }
            });

            layout.addView(btnGotoShoppingCart);
        } else {
            LinearLayout.LayoutParams layoutParamsPay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsBtnRemoveItem = new LinearLayout.LayoutParams(70, 70);
            LinearLayout.LayoutParams layoutParamsItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsBtnPay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParamsView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            LinearLayout.LayoutParams layoutParamsQuantity = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams layoutParamsDiscount = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lpDiscountType = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsBtnRemoveItem.gravity = Gravity.RIGHT;

            layoutParamsQuantity.setMargins(20, 0, 20, 20);
            lpDiscountType.setMargins(20, 20, 20, 20);
            layoutParamsDiscount.setMargins(20, 0, 20, 20);
            layout.setOrientation(LinearLayout.VERTICAL);


            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);

            final Cursor result = myDb.getAllData();
            while (result.moveToNext()) {
                final LinearLayout layout1 = new LinearLayout(this);
                layoutParamsItems.setMargins(10, 10, 10, 10);
                layout1.setLayoutParams(layoutParamsItems);
                layout1.setOrientation(LinearLayout.VERTICAL);
                layout1.setTag("layout" + result.getString(0));
                layout.addView(layout1);

                final Button btnRemoveItem = new Button(this);
                btnRemoveItem.setLayoutParams(layoutParamsBtnRemoveItem);
                btnRemoveItem.setText("X");
                btnRemoveItem.setTextSize(13);
                btnRemoveItem.setBackgroundColor(Color.RED);
                btnRemoveItem.setTextColor(Color.WHITE);
                btnRemoveItem.setTag(result.getString(0));
                final int generatedBtnRemoveID = View.generateViewId();
                btnRemoveItem.setId(generatedBtnRemoveID);

                btnRemoveItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        deleteData(btnRemoveItem.getTag().toString());
                        computeTotal();
                        layout.removeView(layout1);
                        toastMsg("Item Removed", 0);
                        System.out.println(layout.getChildCount());
                        if (layout.getChildCount() == 1) {
                            loadData();
                        }
                    }
                });
                layout1.addView(btnRemoveItem);

                final TextView itemname = new TextView(this);
                Double price = result.getDouble(3);
                String isFree = (result.getInt(6) > 0 ? "Free" : "");
                itemname.setText((isFree.equals("") ? "" : isFree + "\n") + result.getString(1) + "\n" + "₱" + df.format(price) + "\n" + result.getDouble(4) + "%" + "\n" + "₱" + result.getDouble(5));
                itemname.setPadding(20, 20, 20, 20);
                itemname.setTag(result.getString(0));

                itemname.setLayoutParams(layoutParamsItems);
                itemname.setTextColor(Color.BLACK);

                itemname.setTextSize(17);
                layout1.addView(itemname);

                View view = new View(this);
                view.setLayoutParams(layoutParamsView);
                view.setBackgroundColor(Color.BLACK);
                layout1.addView(view);
            }
            LinearLayout layoutPay = new LinearLayout(this);
            layoutPay.setBackgroundColor(Color.WHITE);
            layoutPay.setLayoutParams(layoutParamsPay);

            final Spinner cmbDiscountType = new Spinner(this);
            cmbDiscountType.setTag("cmbDiscountType");
            cmbDiscountType.setLayoutParams(lpDiscountType);
            List<String> discounts = sc.returnDiscounts(ShoppingCart.this);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, discounts);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cmbDiscountType.setAdapter(adapter);

            cmbDiscountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        AlertDialog.Builder myDialog = new AlertDialog.Builder(ShoppingCart.this);
                        myDialog.setCancelable(false);
                        myDialog.setTitle("Senior/PWD/Employee Discount");
                        LinearLayout layout = new LinearLayout(ShoppingCart.this);
                        layout.setPadding(40, 40, 40, 40);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText txtId = new EditText(ShoppingCart.this);
                        txtId.setHint("Enter ID");
                        layout.addView(txtId);

                        final EditText txtName = new EditText(ShoppingCart.this);
                        txtName.setHint("Enter Name");
                        layout.addView(txtName);
                        myDialog.setView(layout);

                        myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (txtId.getText().toString().equals("")) {
                                    toastMsg("ID field is required", 0);
                                    cmbDiscountType.setSelection(0);
                                } else if (txtName.getText().toString().equals("")) {
                                    toastMsg("Name field is required", 0);
                                    cmbDiscountType.setSelection(0);
                                } else {
                                    discountID = txtId.getText().toString();
                                    discountName = txtName.getText().toString();
                                    computeTotal();
                                }
                            }
                        });

                        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                discountID = "";
                                discountName = "";
                                cmbDiscountType.setSelection(0);
                                dialog.dismiss();
                            }
                        });

                        myDialog.show();
                    } else {
                        computeTotal();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            final Button btnPay = new Button(this);
            btnPay.setTag("Pay");
            btnPay.setLayoutParams(layoutParamsBtnPay);
            btnPay.setTextSize(20);
            btnPay.setText("PAY");
            btnPay.setBackgroundResource(R.color.colorPrimary);
            btnPay.setTextColor(Color.WHITE);

            btnPay.setOnClickListener(new View.OnClickListener() {
                String noStock = sc.checkEndingBalance(ShoppingCart.this);

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    layout.clearFocus();
                    if (myDb.countItems() <= 0) {
                        showMessage("Atlantic Bakery", "No Item found");
                    } else if (!noStock.equals("")) {
                        AlertDialog.Builder endbalDialog = new AlertDialog.Builder(ShoppingCart.this);
                        endbalDialog.setCancelable(false);
                        endbalDialog.setTitle("Confirmation");
                        endbalDialog.setMessage("Below Item is out of stock. Are you sure you want to proceed? \n\n" + noStock);
                        endbalDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog.Builder getPasswordDialog = new AlertDialog.Builder(ShoppingCart.this);
                                getPasswordDialog.setTitle("Enter Your Password");
                                LinearLayout layout = new LinearLayout(ShoppingCart.this);
                                layout.setPadding(40, 40, 40, 40);
                                layout.setOrientation(LinearLayout.VERTICAL);

                                final EditText txtPassword = new EditText(ShoppingCart.this);
                                txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                txtPassword.setHint("Enter Your Password");
                                layout.addView(txtPassword);
                                getPasswordDialog.setView(layout);

                                getPasswordDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                        int isPasswordCorrect = uc.checkManagerPassword(ShoppingCart.this, txtPassword.getText().toString());
                                        if (isPasswordCorrect < 0) {
                                            toastMsg("Wrong Password", 0);
                                        } else {
                                            confirmationDialog();
                                        }
                                    }
                                });

                                getPasswordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                getPasswordDialog.show();
                            }
                        });

                        endbalDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        endbalDialog.show();
                    } else {
                        confirmationDialog();
                    }
                }
            });

            final TextView lblSubTotal = new TextView(this);
            lblSubTotal.setText("0.00");

            lblSubTotal.setLayoutParams(layoutParamsLblError);
            lblSubTotal.setTextColor(Color.RED);
            lblSubTotal.setTag("lblSubTotal");
            lblSubTotal.setTextSize(40);

            layoutPay.addView(cmbDiscountType);
            layoutPay.addView(lblSubTotal);
            layoutPay.addView(btnPay);
            layoutPay.setOrientation(LinearLayout.VERTICAL);
            layoutPay.setPadding(20, 20, 20, 20);
            layout.addView(layoutPay);
            computeTotal();
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void confirmationDialog(){
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(ShoppingCart.this);
        LinearLayout layout = new LinearLayout(ShoppingCart.this);
        layout.setPadding(40,40,40,40);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView lblARTitle = new TextView(ShoppingCart.this);
        lblARTitle.setText("Choose Tender Type");
        lblARTitle.setTextSize(20);
        lblARTitle.setGravity(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(lblARTitle);

        final AutoCompleteTextView txtCustomer = new AutoCompleteTextView(ShoppingCart.this);
        txtCustomer.setHint("Customer Name");

        final EditText txttendered = new EditText(ShoppingCart.this);
        txttendered.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txttendered.setText("0.0");
        txttendered.setHint("Enter Amount");

        final RadioGroup rbGroup = new RadioGroup(ShoppingCart.this);
        rbGroup.setId(View.generateViewId());
        rbGroup.setTag("RBGroup");

        final RadioButton rbCash = new RadioButton(ShoppingCart.this);
        rbCash.setTag("ARCash");
        rbCash.setText("CASH");
        rbCash.setId(View.generateViewId());

        rbCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtCustomer.setText("CASH");
                    txtCustomer.setEnabled(false);
                    txttendered.setEnabled(true);
                    txtCustomer.setAdapter(null);
                }
            }
        });

        final RadioButton rbARSales = new RadioButton(ShoppingCart.this);
        rbARSales.setText("AR Sales");
        rbARSales.setTag("ARSales");
        rbARSales.setId(View.generateViewId());

        rbARSales.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtCustomer.setText("");
                    txtCustomer.setEnabled(true);
                    txtCustomer.requestFocus();
                    txttendered.setEnabled(false);
                    txttendered.setText("0.0");
                    txtCustomer.setAdapter(fillName(sc.returnCustomer(ShoppingCart.this, "Customer")));
                }
            }
        });

        final RadioButton rbARCharge = new RadioButton(ShoppingCart.this);
        rbARCharge.setText("AR Charge");
        rbARCharge.setTag("ARCharge");
        rbARCharge.setId(View.generateViewId());

        rbARCharge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtCustomer.setText("");
                    txtCustomer.setEnabled(true);
                    txtCustomer.requestFocus();
                    txttendered.setEnabled(false);
                    txttendered.setText("0.0");
                    txtCustomer.setAdapter(fillName(sc.returnCustomer(ShoppingCart.this,"Employee")));
                }
            }
        });

        layout.addView(rbGroup);
        rbGroup.addView(rbCash);
        rbGroup.addView(rbARSales);
        rbGroup.addView(rbARCharge);

        layout.addView(txtCustomer);
        layout.addView(txttendered);

        myDialog.setView(layout);
        myDialog.setCancelable(false);

        myDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String customerName = txtCustomer.getText().toString().trim();
                String tendertype = "";
                //TENDER TYPE
                if(rbCash.isChecked()){
                    tendertype = "Cash";
                }else if(rbARSales.isChecked()){
                    tendertype = "A.R Sales";
                }else if(rbARCharge.isChecked()){
                    tendertype = "A.R Charge";
                }
                if(!rbCash.isChecked() && !rbARCharge.isChecked() && !rbARSales.isChecked()){
                    toastMsg("Please select Service Type",0);
                }
                else if(customerName.isEmpty()){
                    toastMsg("Name field is required",0);
                }else if(!tendertype.equals("Cash") && !sc.checkCustomer(ShoppingCart.this,customerName, tendertype) ){
                    toastMsg("Name not found",0);
                }
                else{
                    AlertDialog.Builder dialogConfirmation = new AlertDialog.Builder(ShoppingCart.this);
                    dialogConfirmation.setTitle("Confirm Order?");
                    final String finalTendertype = tendertype;
                    dialogConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String disctype;
                            String tendered = txttendered.getText().toString();
                            double tenderamt;
//                                          DISCOUNT TYPE
                            View root = getWindow().getDecorView().getRootView();
                            Spinner currentDiscountType = root.findViewWithTag("cmbDiscountType");
                            if(currentDiscountType.getSelectedItemPosition() == 0){
                                disctype = "N/A";
                            }else{
                                disctype = currentDiscountType.getSelectedItem().toString();
                            }
//                                          TENDER AMOUNT
                            if(tendered.isEmpty()){
                                tenderamt = 0.00;
                            }else if(Double.parseDouble(tendered) < 0){
                                tenderamt = 0.00;
                            }else{
                                tenderamt = Double.parseDouble(tendered);
                            }
//                                          CUSTOMER
                            try {
                                insertTransaction(finalTendertype,disctype,tenderamt,customerName);
                                discountID = "";
                                discountName = "";

                            } catch (Exception e) {
                                showMessage("Error", e.getMessage());
                            }
                        }
                    });

                    dialogConfirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialogConfirmation.show();
                }
            }
        });
        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        myDialog.show();
    }

    public ArrayAdapter<String> fillName(List<String> names){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
    }

//    @SuppressLint({"WrongConstant", "ShowToast"})
//    public void updateData(String id, Double quantity, Double discountpercent, Double totalprice, Integer free){
//
//        boolean isUpdate = myDb.updateData(id,quantity,discountpercent,totalprice,free);
//        if(!isUpdate){
//            Toast.makeText(this,"Data not Updated", 0).show();
//        }
//    }

    @SuppressLint({"WrongConstant", "ShowToast"})
    public void deleteData(String id){
        Integer deletedItem = myDb.deleteData(id);
        if(deletedItem < 0){
            Toast.makeText(this,"Item not remove", 0).show();
        }
    }

    @SuppressLint("SetTextI18n")
    public void computeTotal(){
        View root = getWindow().getDecorView().getRootView();
        TextView lblsubtotal =root.findViewWithTag("lblSubTotal");
        Spinner cmbDiscount =root.findViewWithTag("cmbDiscountType");
        double discountType = 0.00;
        if(cmbDiscount.getSelectedItemPosition() != 0){
            String spinnerText = (String) cmbDiscount.getSelectedItem();
            discountType = sc.getDiscountPercent(ShoppingCart.this, spinnerText);
        }

        double getSubTotal = myDb.getSubTotal() - (myDb.getSubTotal() * (discountType / 100));
        if(getSubTotal > 0){
            lblsubtotal.setText("Total: " + df.format(getSubTotal));
        }else{
            lblsubtotal.setText("Total: 0.00");
        }
    }

    public void closeKeyboard(View v){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            v.clearFocus();
        }
    }

    public void toastMsg(String value, Integer duration){
        Toast.makeText(this, value, duration).show();
    }

    public  void showMessage(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void insertTransaction(final String tendertype, final String disctype, final Double tenderamt, final String customer) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
            int userid = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
            Integer ordernum = sc.getOrderNumber(ShoppingCart.this);
            Double less = sc.getDiscountPercent(ShoppingCart.this, disctype);
            double change;
            con = cc.connectionClass(ShoppingCart.this);
            if (con == null) {
                Toast.makeText(ShoppingCart.this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                double getSubTotal = myDb.getSubTotal() - (myDb.getSubTotal() * (less / 100));
                if (tenderamt > getSubTotal) {
                    change = tenderamt - getSubTotal;
                } else {
                    change = 0.00;
                }
                double discamt = myDb.getSubTotal() * (less / 100);
                double subtotalBefore = myDb.getSubTotalBefore();
                String query = "INSERT INTO tbltransaction2 (ornum, ordernum, transdate, cashier," +
                        " tendertype, servicetype, delcharge, subtotal, disctype, less, vatsales, vat," +
                        " amtdue, tenderamt, change, refund, comment, remarks, customer, tinnum, tablenum," +
                        " pax, createdby, datecreated, datemodified, status, status2, area, gctotal, typez," +
                        " discamt) VALUES ('000'," + ordernum + ",(select cast(getdate() as date)),(SELECT username FROM tblusers WHERE" +
                        " systemid=" + userid + "),'" + tendertype + "','Take Out'," +
                        "0," + subtotalBefore + ",'" + disctype + "'," + less + ",0,0," + getSubTotal + "," + tenderamt + "," + change + "," +
                        "0,'','','" + customer + "',0,0,0,(SELECT username FROM tblusers WHERE systemid=" + userid + ")," +
                        "(SELECT GETDATE()),(SELECT GETDATE()),1,'Unpaid','Sales',0,(SELECT postype FROM tblusers WHERE systemid=" +
                        "" + userid + ")," + discamt + ");";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                if (!disctype.equals("N/A")) {
                    Statement stmtSenior = con.createStatement();
                    String querySenior = "INSERT INTO tblsenior (transnum,idno,name,disctype,datedisc,status)" +
                            "VALUES ('" + ordernum + "','" + discountID + "','" + discountName + "','" + disctype + "'," +
                            "(SELECT GETDATE()),3)";
                    stmtSenior.executeUpdate(querySenior);
                }

                final Cursor result = myDb.getAllData();
                while (result.moveToNext()) {
                    String itemname = result.getString(1);
                    double quantity = Double.parseDouble(result.getString(2));
                    double totalprice = Double.parseDouble(result.getString(5));
                    double discount = Double.parseDouble(result.getString(4));
                    double discountPercent = discount / 100;
                    int free = Integer.parseInt(result.getString(6));
                    String query2 = "Insert into tblorder2 (ordernum, category, itemname, qty, price, totalprice, dscnt, free," +
                            "request, status, discprice, disctrans,area,gc,less,deliver,datecreated,pricebefore,discamt)" +
                            "VALUES(" + ordernum + ",(SELECT category FROM tblitems WHERE itemname='" + itemname + "'),'" + itemname + "'" +
                            "," + quantity + ",(SELECT price FROM tblitems WHERE itemname='" + itemname + "')," + totalprice + "," + discount + "," + free + "" +
                            ",'',1,(SELECT SUM(price - ((" + discount + "/ 100) *price)) FROM tblitems WHERE itemname='" + itemname + "'),0,'Sales',0,0,0,(SELECT GETDATE())," +
                            "(SELECT SUM(price * " + quantity + ") FROM tblitems WHERE itemname='" + itemname + "')," +
                            "(SELECT " + discountPercent + " * (price * " + quantity + ") FROM tblitems WHERE itemname='" + itemname + "'));";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(query2);
                }
                con.close();
                myDb.truncateTable();
                showMessage("Transaction Completed", "Order #: " + ordernum + "\n" + "Change: " + change);
                loadData();
            }
        } catch (SQLException ex) {
            showMessage("Error", ex.getMessage());
        }
    }
}