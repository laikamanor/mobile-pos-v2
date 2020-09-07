package com.example.atlanticbakery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class Received extends AppCompatActivity {
    Connection con;
    DatabaseHelper db = new DatabaseHelper(this);
    DatabaseHelper2 myDb;
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    received_class rc = new received_class();
    connection_class cc = new connection_class();

    DecimalFormat df = new DecimalFormat("#,###.00");
    long mLastClickTime = 0;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);
        myDb = new DatabaseHelper2(this);

        String title = getIntent().getStringExtra("title");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        NavigationView navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Menu menu = navigationView.getMenu();
        MenuItem nav_shoppingCart = menu.findItem(R.id.nav_shoppingCart);
        int totalItems = db.countItems();
        nav_shoppingCart.setTitle("Shopping Cart (" + totalItems + ")");
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
                        startActivity(uic.goTo(Received.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(Received.this, ShoppingCart.class));
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
        loadItems();
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
        final LinearLayout layout = findViewById(R.id.parentLayout);
        layout.removeAllViews();
        final String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
        final LinearLayout.LayoutParams layoutParamsNoItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParamsNoItems.setMargins(50, 20, 50, 0);
        LinearLayout.LayoutParams layoutParamsItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final Cursor result = myDb.getAllData(title);
        if (result.getCount() <= 0) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(layoutParamsNoItems);
            imageView.setImageResource(R.drawable.ic_sad_face);
            layout.addView(imageView);
            TextView lblNoItemFound = new TextView(this);
            lblNoItemFound.setLayoutParams(layoutParamsNoItems);
            lblNoItemFound.setText("Your Selected Items is currently empty. Tap the button below to shop for items.");
            lblNoItemFound.setBackgroundColor(Color.WHITE);
            lblNoItemFound.setTextSize(15);
            lblNoItemFound.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            layout.addView(lblNoItemFound);

            Button btnGotoShoppingCart = new Button(this);
            btnGotoShoppingCart.setText("Go List Items");
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
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
            });

            layout.addView(btnGotoShoppingCart);
        } else {
            LinearLayout.LayoutParams layoutParamsBtnRemoveItem = new LinearLayout.LayoutParams(70, 70);
            LinearLayout.LayoutParams layoutParamsView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            layoutParamsBtnRemoveItem.gravity = Gravity.RIGHT;
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
                        toastMsg("Item Removed", 0);
                        loadItems();
                    }
                });
                layout1.addView(btnRemoveItem);

                final TextView itemname = new TextView(this);
                itemname.setText(result.getString(1) + "     " + result.getDouble(2) + " pcs");
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
            LinearLayout.LayoutParams layoutParamsPay = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout layoutPay = new LinearLayout(this);
            layoutPay.setBackgroundColor(Color.WHITE);
            layoutPay.setLayoutParams(layoutParamsPay);

            final Button btnProceed = new Button(this);
            btnProceed.setLayoutParams(layoutParamsPay);
            btnProceed.setTextSize(20);
            btnProceed.setText("PROCEED");
            btnProceed.setBackgroundResource(R.color.colorPrimary);
            btnProceed.setTextColor(Color.WHITE);

            btnProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder myDialog = new AlertDialog.Builder(Received.this);
                    myDialog.setCancelable(false);
                    LinearLayout layout = new LinearLayout(Received.this);
                    layout.setPadding(40,40,40,40);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    String lblBranchText = "";
                    if(title.equals("Received from Direct Supplier")){
                        lblBranchText = "Supplier";
                    }else if(title.equals("Transfer Out") ||title.equals("Received from Other Branch")){
                        lblBranchText = "Branch";
                    }

                    TextView lblBranch = new TextView(Received.this);
                    final Spinner cmbDiscountType = new Spinner(Received.this);
                    if(title.equals("Transfer Out") || title.equals("Received from Direct Supplier")|| title.equals("Received from Other Branch")){
                        lblBranch.setText(lblBranchText + ":");
                        lblBranch.setTextSize(15);
                        lblBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        layout.addView(lblBranch);
                        layout.addView(cmbDiscountType);
                    }

                    List<String> discounts = rc.returnBranchSupplier(Received.this, lblBranchText);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Received.this, android.R.layout.simple_spinner_item, discounts);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cmbDiscountType.setAdapter(adapter);

                    TextView lblSAPNumber = new TextView(Received.this);
                    lblSAPNumber.setText("SAP #:");
                    lblSAPNumber.setTextSize(15);
                    lblSAPNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    layout.addView(lblSAPNumber);

                    final EditText txtSAPNumber = new EditText(Received.this);
                    final CheckBox toFollow = new CheckBox(Received.this);
                    final CheckBox chckAddSAP = new CheckBox(Received.this);
                    toFollow.setText("To Follow");

                    if(title.equals("Received from Adjustment") || title.equals("Adjustment Out")){
                        chckAddSAP.setText("Add SAP");
                        chckAddSAP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked){
                                    toFollow.setEnabled(true);
                                    txtSAPNumber.setEnabled(true);
                                }else{
                                    toFollow.setEnabled(false);
                                    txtSAPNumber.setEnabled(false);
                                }
                                toFollow.setChecked(false);
                                txtSAPNumber.setText("");
                            }
                        });
                        layout.addView(chckAddSAP);
                        toFollow.setEnabled(false);
                        txtSAPNumber.setEnabled(false);
                    }else{
                        toFollow.setEnabled(true);
                        txtSAPNumber.setEnabled(true);
                    }

                    toFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            txtSAPNumber.setEnabled((!isChecked));
                            txtSAPNumber.setText("");
                        }
                    });
                    layout.addView(toFollow);

                    txtSAPNumber.setTextSize(15);
                    txtSAPNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    txtSAPNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(6);
                    txtSAPNumber.setFilters(fArray);
                    layout.addView(txtSAPNumber);

                    TextView lblRemarks = new TextView(Received.this);
                    lblRemarks.setText("Remarks:");
                    lblRemarks.setTextSize(15);
                    lblRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    layout.addView(lblRemarks);

                    final EditText txtRemarks = new EditText(Received.this);
                    txtRemarks.setTextSize(15);
                    txtRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    layout.addView(txtRemarks);

                    final String finalLblBranchText = lblBranchText;
                    myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!toFollow.isChecked() && txtSAPNumber.getText().toString().equals("") && chckAddSAP.isChecked()){
                                Toast.makeText(Received.this, "SAP # field is empty", Toast.LENGTH_SHORT).show();
                            }else if(!toFollow.isChecked() && txtSAPNumber.getText().toString().length() < 6 && chckAddSAP.isChecked()){
                                Toast.makeText(Received.this, "SAP # should 6 numbers", Toast.LENGTH_SHORT).show();
                            }else if(cmbDiscountType.getSelectedItemPosition() == 0 && !finalLblBranchText.equals("")){
                                Toast.makeText(Received.this,  finalLblBranchText + " field is empty", Toast.LENGTH_SHORT).show();
                            }else if(txtRemarks.getText().toString().equals("")){
                                Toast.makeText(Received.this, "Remarks field is empty", Toast.LENGTH_SHORT).show();
                            }else {
                                insertData(cmbDiscountType.getSelectedItem().toString(), toFollow.isChecked(),txtSAPNumber.getText().toString(), txtRemarks.getText().toString());
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
            });

            layoutPay.addView(btnProceed);
            layoutPay.setOrientation(LinearLayout.VERTICAL);
            layoutPay.setPadding(20, 20, 20, 20);
            layout.addView(layoutPay);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void insertData(String branch, boolean toFollow, String txtSAPNumber, String remarks){
        String sapNumber;
        sapNumber = (toFollow ? "To Follow" : txtSAPNumber);
        String columnName = "";
        String operator = "";
        String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
        switch (title) {
            case "Received from Production":
                columnName = "productionin";
                operator = "+";
                break;
            case "Received from Other Branch":
                columnName = "itemin";
                operator = "+";
                break;
            case "Received from Direct Supplier":
                columnName = "supin";
                operator = "+";
                break;
            case "Received from Adjustment":
                columnName = "adjustmentin";
                operator = "+";
                break;
            case "Adjustment Out":
                columnName = "pullout";
                operator = "-";
                break;
            case "Transfer Out":
                columnName = "transfer";
                operator = "-";
                break;
        }
        saveData(operator, columnName,sapNumber, remarks,branch);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String returnTransactionNumber(String columnName){
        String result = "";
        String type2 = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
        String type;
        String template = "";
        int prodCount = 0;
        StringBuilder totalZero = new StringBuilder();
        String branchCode = "";

        switch (type2) {
            case "Received from Adjustment":
                type = "Adjustment Item";
                break;
            case "Transfer Out":
                type = "Transfer Item";
                break;
            case ("Adjustment Out"):
                type = "Adjustment Out Item";
                break;
            default:
                type = "Received Item";
                break;
        }

        switch (columnName) {
            case "productionin":
                template = "RECPROD - ";
                break;
            case "itemin":
                template = "RECBRA - ";
                break;
            case "supin":
                template = "RECSUPP - ";
                break;
            case "adjustmentin":
                template = "ADJIN - ";
                break;
            case "pullout":
                template = "ADJOUT - ";
                break;
            case "transfer":
                template = "TRA - ";
                break;
        }
        String queryProd = "Select ISNULL(MAX(transaction_id),0) +1 [counter] from tblproduction WHERE area='Sales' AND type='" + type + "' AND type2='" + type2 + "';";
        String queryBra = "SELECT branchcode FROM tblbranch WHERE main='1'";
        try{
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(queryProd);
                if(resultSet.next()){
                    prodCount = resultSet.getInt("counter");
                }

                Statement statement2 = con.createStatement();
                ResultSet resultSet2 = statement2.executeQuery(queryBra);
                if(resultSet2.next()){
                    branchCode = resultSet2.getString("branchcode") + " - ";
                }
                con.close();


                if(prodCount < 1000000){
                    String cselectcount_result = Integer.toString(prodCount);
                    int cselectcount_resultLength = 7 - cselectcount_result.length();
                    while (0 < cselectcount_resultLength){
                        totalZero.append("0");
                        cselectcount_resultLength-= 1;
                    }
                }
                result = template + branchCode + totalZero + prodCount;
            }
        }catch (Exception ex){
            Toast.makeText(this, "returnTransactionNumber() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveData(String operator, String columnName, String sapNumber, String remarks,String selectedBranch) {
        try {
            String transactionNumber = returnTransactionNumber(columnName);
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String title = Objects.requireNonNull(getSupportActionBar().getTitle()).toString().trim();
                Cursor cursor = myDb.getAllData(title);
                while (cursor.moveToNext()) {
                    double quantity = cursor.getDouble(2);
                    String itemName = cursor.getString(1);
                    String query1 = "UPDATE tblinvitems SET " + columnName + "+=" + quantity + (operator.equals("+") ? ",totalav+=" + quantity : "") + ",endbal" + operator + "=" + quantity + ",variance" + (operator.equals("+") ? "-" : "+") + "=" + quantity + " WHERE itemname='" + itemName + "' AND invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND area='Sales';";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query1);

                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                    String procby = Objects.requireNonNull(sharedPreferences.getString("username", ""));

                    String type;
                    switch (title) {
                        case "Received from Adjustment":
                            type = "Adjustment Item";
                            break;
                        case "Transfer Out":
                            type = "Transfer Item";
                            break;
                        case ("Adjustment Out"):
                            type = "Adjustment Out Item";
                            break;
                        default:
                            type = "Received Item";
                            break;
                    }
                    String fromBranch = (!selectedBranch.equals("") ? "'" + selectedBranch + "'" : "");
                    String sapdoc = (title.equals("Received from Direct Supplier") ? "GRPO" : "IT");

                    String query2 = "INSERT INTO tblproduction (transaction_number,inv_id,item_code,item_name,category,quantity,reject,charge,sap_number,remarks,date,processed_by,type,area,status,transfer_from,transfer_to,typenum,type2) VALUES ('" + transactionNumber + "',(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC),(SELECT itemcode FROM tblitems WHERE itemname='" + itemName + "'),'" + itemName + "',(SELECT category FROM tblitems WHERE itemname='" + itemName + "')," + quantity + ",0,0,'" + sapNumber + "','" + remarks + "',(SELECT GETDATE()),'" + procby + "','" + type + "','Sales','Completed'," + (fromBranch.equals("") ? "(SELECT branchcode + ' (SLS)' FROM tblbranch WHERE main='1')" : fromBranch ) + ",(SELECT branchcode + '" + (columnName.equals("pullout") ? " (PRD)" : " (SLS)") + "' FROM tblbranch WHERE main='1'),'" + sapdoc + "','" + title + "');";
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate(query2);
                    con.close();

                    showMessage("Atlantic Bakery", "REFERENCE #: " + "\n" + transactionNumber);
                    myDb.truncateTable();
                    loadItems();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, "saveData() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessage(String title, String message){
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

    @SuppressLint({"WrongConstant", "ShowToast"})
    public void deleteData(String id){
        Integer deletedItem = myDb.deleteData(id);
        if(deletedItem < 0){
            Toast.makeText(this,"Item not remove", 0).show();
        }
    }

    public void toastMsg(String value, Integer duration){
        Toast.makeText(this, value, duration).show();
    }

    public void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(Received.this);
                        startActivity(uic.goTo(Received.this, MainActivity.class));
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
}