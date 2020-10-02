package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateActualEndingBalance extends AppCompatActivity {
    actualendbal_class ac = new actualendbal_class();
    received_class rec = new received_class();
    prefs_class pc = new prefs_class();
    user_class uc = new user_class();
    DatabaseHelper myDb;
    receivedsap_class recsap = new receivedsap_class();
    ui_class uic = new ui_class();
    DecimalFormat df = new DecimalFormat("#,###");
    TableLayout tableLayout;
    Button btnProceed;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_actual_ending_balance);
        myDb = new DatabaseHelper(this);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>Update Actual Ending Balance</font>"));
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean isStoreExist = ac.isTypeExist(UpdateActualEndingBalance.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(UpdateActualEndingBalance.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(UpdateActualEndingBalance.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(UpdateActualEndingBalance.this, "PO Store Count");
                boolean isAuditorPullOutExist = ac.isTypeExist(UpdateActualEndingBalance.this, "PO Auditor Count");
                boolean isFinalPullOutExist = ac.isTypeExist(UpdateActualEndingBalance.this, "PO Final Count");
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
                        startActivity(uic.goTo(UpdateActualEndingBalance.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(UpdateActualEndingBalance.this, ShoppingCart.class));
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
                        }else if(!uc.returnWorkgroup(UpdateActualEndingBalance.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(UpdateActualEndingBalance.this).equals("Manager")){
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
                    case R.id.nav_addsalesinventory:
                        result = true;
                        intent = new Intent(getBaseContext(), AvailableItems.class);
                        intent.putExtra("title", "Transfer to Sales");
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });

        tableLayout = findViewById(R.id.table_main);
        btnProceed = findViewById(R.id.btnProceed);
        btnProceed.setVisibility(View.VISIBLE);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder myDialog = new AlertDialog.Builder(UpdateActualEndingBalance.this);
                myDialog.setCancelable(false);
                LinearLayout layout = new LinearLayout(UpdateActualEndingBalance.this);
                layout.setPadding(40, 40, 40, 40);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView lblBranch = new TextView(UpdateActualEndingBalance.this);
                lblBranch.setText("Pull Out Destination:");
                lblBranch.setTextSize(15);
                lblBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblBranch);

                final Spinner cmbBranches = new Spinner(UpdateActualEndingBalance.this);
                List<String> discounts = rec.returnBranchSupplier(UpdateActualEndingBalance.this, "Branch");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateActualEndingBalance.this, android.R.layout.simple_spinner_item, discounts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cmbBranches.setAdapter(adapter);
                layout.addView(cmbBranches);

                myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (cmbBranches.getSelectedItemPosition() == 0) {
                            Toast.makeText(getBaseContext(), "Select Branch first", Toast.LENGTH_SHORT).show();
                        } else {
                            String selectedBranch = cmbBranches.getSelectedItem().toString();
                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                            int userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
                            List<String> pullouts, actualEndings;
                            actualEndings = getByColumnIndex(1);
                            pullouts = getByColumnIndex(3);
                            boolean isPullOutSuccess = ac.updatePullOut(UpdateActualEndingBalance.this, pullouts, userID, selectedBranch);
                            boolean isUpdateTblActualEndingBalanceStatus = ac.updateTblActualEndingBalanceStatus(UpdateActualEndingBalance.this);
                            boolean isActualEndingBalanceSuccess = ac.updateActualEndingBalance(UpdateActualEndingBalance.this, actualEndings, userID);
                            if (isPullOutSuccess && isUpdateTblActualEndingBalanceStatus && isActualEndingBalanceSuccess) {
                                Toast.makeText(getBaseContext(), "Transaction Completed", Toast.LENGTH_SHORT).show();
                                startActivity(getIntent());
                                finish();
                            } else {
                                Toast.makeText(getBaseContext(), "Transaction Not Completed", Toast.LENGTH_SHORT).show();
                            }
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

        loadData();
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(UpdateActualEndingBalance.this);
                        startActivity(uic.goTo(UpdateActualEndingBalance.this, MainActivity.class));
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
        int totalPendingSAP = recsap.returnPendingSAPNotif(UpdateActualEndingBalance.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }

    public List<String>  getByColumnIndex(int columnIndex){
//        column index (0) - itemname
//        column index (1) - actualendbal
//        column index (2) - endbal
//        column index (3) - pullout
//        column index (5) - price
        List<String> resultz = new ArrayList<>();
        List<String> results;
        results = ac.loadUpdateActualEndingBalance(this);
        for (String result : results) {
            String itemName = "";
            double quantity = 0.00;
            double endBal = 0.00;
            double pullOut = 0.00;
            String[] words = result.split(",");
            for (int i = 0; i < words.length; i++) {
                if (i == 0) {
                    itemName = words[i];
                } else if (i == columnIndex) {
                    quantity = Double.parseDouble(words[i]);
                }else if (i == 2) {
                    endBal = Double.parseDouble(words[i]);
                }else if (i == 3) {
                    pullOut = Double.parseDouble(words[i]);
                }
            }
            String concatString = itemName + "," + quantity + (columnIndex == 1 ? "," + endBal + "," + pullOut : "") ;
            resultz.add(concatString);
        }
        return resultz;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void loadData(){
        tableLayout.removeAllViews();

        TableRow tableColumn = new TableRow(getBaseContext());
        String[] columns = {"Item", "Act. End. Inv.", "Sys. Inv.", "P.O", "Var.", "Php", "Total Amt."};

        for (String s : columns) {
            TextView lblColumn1 = new TextView(getBaseContext());
            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn1.setText(s);
            lblColumn1.setPadding(10, 0, 10, 0);
            lblColumn1.setTextSize(11);
            tableColumn.addView(lblColumn1);
        }
        tableLayout.addView(tableColumn);

        List<String> results;
        results = ac.loadUpdateActualEndingBalance(this);
        for (String result : results){
            String itemName = "";
            double endBal = 0.00,actualEndBal = 0.00, pullOut = 0.00, price = 0.00,variance;
            String[] words = result.split(",");
            for (int i  = 0; i < words.length; i ++){
                if(i == 0){
                    itemName = words[i];
                }else if(i == 1) {
                    actualEndBal = Double.parseDouble(words[i]);
                }else if(i == 2){
                    endBal = Double.parseDouble(words[i]);
                }else if(i == 3){
                    pullOut = Double.parseDouble(words[i]);
                }else if(i == 4){
                    price = Double.parseDouble(words[i]);
                }
            }
            variance = actualEndBal - endBal;
            double varianceFinal = variance + pullOut;
            double totalAmount = varianceFinal * price;

            final TableRow tableRow = new TableRow(getBaseContext());

            TextView lblColumn1 = new TextView(getBaseContext());
            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
            String v = cutWord(itemName);
            lblColumn1.setText(v);
            lblColumn1.setPadding(10, 0, 10, 0);
            lblColumn1.setTextSize(11);

            final String finalItemName = itemName;
            lblColumn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), finalItemName, Toast.LENGTH_SHORT).show();
                }
            });

            tableRow.addView(lblColumn1);

            TextView lblColumn2 = new TextView(getBaseContext());
            lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn2.setText(df.format(actualEndBal));
            lblColumn2.setPadding(10, 10, 10, 10);
            lblColumn2.setTextSize(11);
            tableRow.addView(lblColumn2);

            TextView lblColumn3 = new TextView(getBaseContext());
            lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn3.setText(df.format(endBal));
            lblColumn3.setPadding(10, 0, 10, 0);
            lblColumn3.setTextSize(11);
            tableRow.addView(lblColumn3);

            TextView lblColumn4 = new TextView(getBaseContext());
            lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn4.setText(df.format(pullOut));
            lblColumn4.setPadding(10, 10, 10, 10);
            lblColumn4.setTextSize(11);
            tableRow.addView(lblColumn4);

            TextView lblColumn5 = new TextView(getBaseContext());
            lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn5.setText(df.format(varianceFinal));
            lblColumn5.setPadding(10, 10, 10, 10);
            lblColumn5.setTextSize(11);
            int colorVariance = 0;
            if(varianceFinal == 0){
                colorVariance = Color.BLACK;
            }else if(varianceFinal > 0){
                colorVariance = Color.BLACK;
            }else if(varianceFinal < 0){
                colorVariance = Color.RED;
            }
            lblColumn5.setTextColor(colorVariance);
            tableRow.addView(lblColumn5);

            TextView lblColumn6 = new TextView(getBaseContext());
            lblColumn6.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn6.setText(df.format(price));
            lblColumn6.setPadding(10, 10, 10, 10);
            lblColumn6.setTextSize(11);
            tableRow.addView(lblColumn6);

            TextView lblColumn7 = new TextView(getBaseContext());
            lblColumn7.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn7.setText(df.format(totalAmount));
            lblColumn7.setPadding(10, 10, 10, 10);
            lblColumn7.setTextSize(11);
            int colorTotalAmt = 0;
            if(totalAmount == 0){
                colorTotalAmt = Color.BLACK;
            }else if(totalAmount > 0){
                colorTotalAmt = Color.BLACK;
            }else if(totalAmount < 0){
                colorTotalAmt = Color.RED;
            }
            lblColumn7.setTextColor(colorTotalAmt);
            tableRow.addView(lblColumn7);
            tableLayout.addView(tableRow);
        }
        btnProceed.setVisibility((tableLayout.getChildCount() <= 1) ? View.GONE : View.VISIBLE);
    }

    public String cutWord(String value){
        String result;
        int limit = 10;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}