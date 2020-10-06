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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

 public class CancelRecTrans extends AppCompatActivity {
    long mLastClickTime = 0;
    inventory_class ic = new inventory_class();
    connection_class cc = new connection_class();
    receivedsap_class recsap = new receivedsap_class();
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    user_class uc = new user_class();
    actualendbal_class ac = new actualendbal_class();
    Connection con;
    AutoCompleteTextView txtSearch;


    DecimalFormat df = new DecimalFormat("#,###");

    DatabaseHelper myDb;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_rec_trans);

        myDb = new DatabaseHelper(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>Cancel Rec/Trans</font>"));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

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
                boolean isStoreExist = ac.isTypeExist(CancelRecTrans.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(CancelRecTrans.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(CancelRecTrans.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(CancelRecTrans.this, "PO Store Count");
                boolean isAuditorPullOutExist = ac.isTypeExist(CancelRecTrans.this, "PO Auditor Count");
                boolean isFinalPullOutExist = ac.isTypeExist(CancelRecTrans.this, "PO Final Count");
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
                        startActivity(uic.goTo(CancelRecTrans.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(CancelRecTrans.this, ShoppingCart.class));
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
                        }else if(!uc.returnWorkgroup(CancelRecTrans.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(CancelRecTrans.this).equals("Manager")){
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

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        txtSearch = findViewById(R.id.txtSearch);
        txtSearch.setAdapter(ic.fillAdapter(this, ic.returnTransactionNumbers(this,"")));

        loadTransactions("");
        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                loadTransactions(txtSearch.getText().toString());
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
                        pc.loggedOut(CancelRecTrans.this);
                        startActivity(uic.goTo(CancelRecTrans.this, MainActivity.class));
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
         int totalPendingSAP = recsap.returnPendingSAPNotif(CancelRecTrans.this, "");
         nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
         nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
     }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void loadTransactions(final String transactionNumber){
        final LinearLayout linearTransactions = findViewById(R.id.linearTransactions);
        final LinearLayout layoutItems = findViewById(R.id.layoutItems);
        linearTransactions.removeAllViews();
        layoutItems.removeAllViews();
        List<String> result;
        result = ic.returnTransactionNumbers(this,transactionNumber);
        int totalTransactions = 0;
        for (String temp : result) {
            totalTransactions += 1;
            final LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);
            layout.setOrientation(LinearLayout.VERTICAL);
            linearTransactions.addView(layout);

            TextView txtTransaction = new TextView(this);
            LinearLayout.LayoutParams txtTransactionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txtTransaction.setLayoutParams(txtTransactionParams);
            txtTransactionParams.setMargins(40,10,10,10);
            txtTransaction.setText(temp);
            txtTransaction.setTextSize(15);
            txtTransaction.setTextColor(Color.BLACK);
            final int generatedTxtTransactionID = View.generateViewId();
            txtTransaction.setId(generatedTxtTransactionID);
            txtTransaction.setClickable(true);

            final String currentTransactionNumber = temp;
            txtTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    loadItems(currentTransactionNumber);
                }
            });

            layout.addView(txtTransaction);

            Button btnCancel = new Button(this);
            LinearLayout.LayoutParams btnCancelParams = new LinearLayout.LayoutParams(200,80);
            btnCancelParams.setMargins(40,0,10,10);
            btnCancel.setBackgroundColor(Color.RED);
            btnCancel.setTextColor(Color.WHITE);
            btnCancel.setText("Cancel");
            final int generatedCancelID = View.generateViewId();
            btnCancel.setId(generatedCancelID);
            btnCancel.setLayoutParams(btnCancelParams);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    String errorMsg = "You can't cancel this transaction because the item(s) below of ending balance is less than the Received quantity: \n \n";
                    errorMsg += ic.checkStock(CancelRecTrans.this, currentTransactionNumber);
                    if(!errorMsg.equals("You can't cancel this transaction because the item(s) below of ending balance is less than the Received quantity: \n \n")){

                        AlertDialog.Builder myDialogError = new AlertDialog.Builder(CancelRecTrans.this);
                        myDialogError.setMessage(errorMsg);
                        myDialogError.setCancelable(false);
                        myDialogError.setTitle("Atlantic Bakery");
                        myDialogError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        myDialogError.show();

                    }else{
                        AlertDialog.Builder myDialog = new AlertDialog.Builder(CancelRecTrans.this);
                        myDialog.setMessage("Are you sure you want to cancel this transaction?");
                        myDialog.setCancelable(false);

                        myDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ic.cancelRecTrans(CancelRecTrans.this, currentTransactionNumber);
                                layoutItems.removeAllViews();
                                loadTransactions("");
                                TextView txtHeader = findViewById(R.id.txtHeader);
                                TextView txtHeaderItems = findViewById(R.id.txtHeaderItems);
                                txtHeader.setText("TRANSACTIONS (0)");
                                txtHeaderItems.setText("ITEMS (0)");
                            }
                        });

                        myDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        myDialog.show();
                    }

                }
            });

            layout.addView(btnCancel);

            View line = new View(this);
            LinearLayout.LayoutParams lineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            line.setLayoutParams(lineLayout);
            line.setBackgroundColor(Color.BLACK);
            layout.addView(line);
        }
        TextView txtHeader = findViewById(R.id.txtHeader);
        txtHeader.setText("TRANSACTIONS (" + totalTransactions + ")");
    }

    public void loadItems(String transactionNumber){
        try{
            LinearLayout layoutItems = findViewById(R.id.layoutItems);
            layoutItems.removeAllViews();
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT item_name,quantity FROM tblproduction WHERE transaction_number='" + transactionNumber + "';";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query);
                int totalItems = 0;
                while (rs2.next()){
                    totalItems += 1;
                    LinearLayout layout = new LinearLayout(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setLayoutParams(layoutParams);
                    layoutItems.addView(layout);

                    TextView txtItemname = new TextView(this);
                    LinearLayout.LayoutParams txtItemNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    txtItemNameParams.setMargins(40,10,10,0);
                    txtItemname.setTextSize(15);
                    txtItemname.setText(rs2.getString("item_name"));
                    txtItemname.setLayoutParams(txtItemNameParams);
                    layout.addView(txtItemname);

                    TextView txtQuantity = new TextView(this);
                    txtQuantity.setTextSize(15);

                    double quantity = rs2.getDouble("quantity");

                    txtQuantity.setText(df.format(quantity) + " pcs.");
                    txtQuantity.setLayoutParams(txtItemNameParams);
                    layout.addView(txtQuantity);

                    View line = new View(this);
                    LinearLayout.LayoutParams lineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    line.setLayoutParams(lineLayout);
                    line.setBackgroundColor(Color.BLACK);
                    layout.addView(line);

                }
                con.close();
                TextView txtHeaderItems = findViewById(R.id.txtHeaderItems);
                txtHeaderItems.setText("ITEMS (" + totalItems + ")");
            }

        }catch (Exception ex){
            Toast.makeText(this,"loadItems() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}