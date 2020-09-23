package com.example.atlanticbakery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class ReceivedSap2 extends AppCompatActivity {
    Connection con;
    connection_class cc = new connection_class();
    received_class rc = new received_class();
    actualendbal_class ac = new actualendbal_class();
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    receivedsap_class recsap = new receivedsap_class();
    user_class uc = new user_class();

    DatabaseHelper myDb;
    DatabaseHelper3 myDb3;
    Button btnProeed;

    long mLastClickTime = 0;
    DecimalFormat df = new DecimalFormat("#,###");

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_sap2);
        myDb = new DatabaseHelper(this);
        myDb3 = new DatabaseHelper3(this);

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>Received from SAP</font>"));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean isStoreExist = ac.isTypeExist(ReceivedSap2.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(ReceivedSap2.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(ReceivedSap2.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(ReceivedSap2.this, "Store Count Pull Out");
                boolean isAuditorPullOutExist = ac.isTypeExist(ReceivedSap2.this, "Auditor Count Pull Out");
                boolean isFinalPullOutExist = ac.isTypeExist(ReceivedSap2.this, "Final Count Pull Out");
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
                        startActivity(uic.goTo(ReceivedSap2.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(ReceivedSap2.this, ShoppingCart.class));
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
                        intent = new Intent(getBaseContext(), Received.class);
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
                        }else if(!uc.returnWorkgroup(ReceivedSap2.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(ReceivedSap2.this).equals("Manager")){
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
                        intent = new Intent(getBaseContext(), SalesInventory_AvailableItems.class);
                        intent.putExtra("title", "Add Sales Inventory");
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });

        btnProeed = findViewById(R.id.btnProceed);
        loadData();

        btnProeed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder myDialog = new AlertDialog.Builder(ReceivedSap2.this);
                myDialog.setCancelable(false);
                LinearLayout layout = new LinearLayout(ReceivedSap2.this);
                layout.setPadding(40,40,40,40);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView lblRemarks = new TextView(ReceivedSap2.this);
                lblRemarks.setText("Remarks:");
                lblRemarks.setTextSize(15);
                lblRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblRemarks);

                final EditText txtRemarks = new EditText(ReceivedSap2.this);
                txtRemarks.setTextSize(15);
                txtRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(txtRemarks);

//                String sapNumber = "";
//                Cursor cursor = myDb3.getAllData();
//                if(cursor.moveToNext()){
//                    sapNumber = cursor.getString(1);
//                }
//                final boolean isShowSAP = recsap.isItemToFollow(ReceivedSap2.this, sapNumber);
//                TextView lblSapNumber = new TextView(ReceivedSap2.this);
//                final EditText txtSapNumber = new EditText(ReceivedSap2.this);
//                if(isShowSAP){
//                    lblSapNumber.setText("IT #:");
//                    lblSapNumber.setTextSize(15);
//                    lblSapNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                    layout.addView(lblSapNumber);
//
//                    txtSapNumber.setTextSize(15);
//                    txtSapNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                    layout.addView(txtSapNumber);
//                }

                myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String remarks = txtRemarks.getText().toString();
                        if(remarks.equals("")){
                            Toast.makeText(ReceivedSap2.this, "Remarks field is empty", Toast.LENGTH_SHORT).show();
                        }
//                        else if(isShowSAP && txtSapNumber.getText().toString().equals("")){
//                            Toast.makeText(ReceivedSap2.this, "IT# field is empty", Toast.LENGTH_SHORT).show();
//                        }
                        else{
                            Cursor cursor = myDb3.getAllData();
                            if(cursor != null){
                                if(cursor.moveToNext()){
//                                    String sapNumber = (isShowSAP) ? txtSapNumber.getText().toString() : cursor.getString(1);
                                    String sapNumber = cursor.getString(1);
                                    String fromBranch = cursor.getString(2);
                                    boolean isSelected = cursor.getInt(6) == 1;
                                    String columnName = "";
                                    if(fromBranch.equals("A1 P-FG")){
                                        columnName = "productionin";
                                    }else{
                                        columnName = "itemin";
                                    }
                                    if(isSelected){
                                        saveDataRec("+",columnName, sapNumber, remarks, fromBranch);
                                    }
                                }
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
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(ReceivedSap2.this);
                        startActivity(uic.goTo(ReceivedSap2.this, MainActivity.class));
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
        int totalCart =  myDb.countItems();
        int totalPendingSAP = recsap.returnPendingSAPNotif(ReceivedSap2.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveDataRec(String operator, String columnName, String sapNumber, String remarks,String selectedBranch) {
        try {
            String title = (columnName.equals("productionin")) ? "Received from Production" : "Received from Other Branch";
            String transactionNumber = rc.returnTransactionNumber(ReceivedSap2.this,title,columnName);
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                Cursor cursor = myDb3.getAllData();
                if(cursor != null){
                    while (cursor.moveToNext()) {
                        double quantity;
                        String itemName;
                        quantity = cursor.getDouble(5);
                        itemName = cursor.getString(3);
                        boolean isSelected = cursor.getInt(6) == 1;
                        if (isSelected) {
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

                            String query2 = "INSERT INTO tblproduction (transaction_number,inv_id,item_code,item_name,category,quantity,reject,charge,sap_number,remarks,date,processed_by,type,area,status,transfer_from,transfer_to,typenum,type2) VALUES ('" + transactionNumber + "',(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC),(SELECT itemcode FROM tblitems WHERE itemname='" + itemName + "'),'" + itemName + "',(SELECT category FROM tblitems WHERE itemname='" + itemName + "')," + quantity + ",0,0,'" + sapNumber + "','" + remarks + "',(SELECT GETDATE()),'" + procby + "','" + type + "','Sales','Completed'," + (fromBranch.equals("") ? "(SELECT branchcode + ' (SLS)' FROM tblbranch WHERE main='1')" : fromBranch) + ",(SELECT branchcode + '" + (columnName.equals("pullout") ? " (PRD)" : " (SLS)") + "' FROM tblbranch WHERE main='1'),'" + sapdoc + "','" + title + "');";
                            Statement stmt2 = con.createStatement();
                            stmt2.executeUpdate(query2);
                        }
                    }

                    showMessage("Atlantic Bakery", "REFERENCE #: " + "\n" + transactionNumber);
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
                myDb3.truncateTable();
                startActivity(getIntent());
                finish();
            }
        });

        builder.show();
    }

    public void loadData() {
        LinearLayout linearLayout = findViewById(R.id.layoutNoItems);
        linearLayout.setVisibility(View.GONE);
        btnProeed.setVisibility(View.VISIBLE);

        TableLayout tableLayout = findViewById(R.id.table_main);
        tableLayout.removeAllViews();
        Cursor cursor = myDb3.getAllData();
        if (cursor != null) {
            btnProeed.setVisibility(View.VISIBLE);
            TableRow tableColumn = new TableRow(ReceivedSap2.this);
            String[] columns = {"Item", "Del. Qty.", "Act. Qty.", "Var.", "Action"};

            for (String s : columns) {
                TextView lblColumn1 = new TextView(ReceivedSap2.this);
                lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblColumn1.setText(s);
                lblColumn1.setPadding(10, 0, 10, 0);
                tableColumn.addView(lblColumn1);
            }
            tableLayout.addView(tableColumn);

            cursor = myDb3.getAllData();
            while (cursor.moveToNext()) {
                final TableRow tableRow = new TableRow(ReceivedSap2.this);
                String itemName = cursor.getString(3);
                String v = cutWord(itemName);
                double del_quantity = cursor.getDouble(4);
                double act_quantity = cursor.getDouble(5);
                final int id = cursor.getInt(0);
                final boolean isSelected = (cursor.getInt(6) != 0);

                if (isSelected) {
                    TextView lblColumn1 = new TextView(ReceivedSap2.this);
                    lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn1.setText(v);
                    lblColumn1.setPadding(10, 0, 10, 0);
                    tableRow.addView(lblColumn1);

                    TextView lblColumn2 = new TextView(ReceivedSap2.this);
                    lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn2.setText(df.format(del_quantity));
                    lblColumn2.setPadding(10, 10, 10, 10);
                    tableRow.addView(lblColumn2);

                    TextView lblColumn3 = new TextView(ReceivedSap2.this);
                    lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn3.setText(df.format(act_quantity));
                    lblColumn3.setPadding(10, 10, 10, 10);
                    tableRow.addView(lblColumn3);

                    TextView lblColumn4 = new TextView(ReceivedSap2.this);
                    lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    double variance = act_quantity - del_quantity;
                    lblColumn4.setText(df.format(variance));
                    lblColumn4.setPadding(10, 10, 10, 10);
                    tableRow.addView(lblColumn4);

                    TextView lblColumn5 = new TextView(ReceivedSap2.this);
                    lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn5.setTag(id);
                    lblColumn5.setText("Remove");
                    lblColumn5.setPadding(10, 10, 10, 10);
                    lblColumn5.setTextColor(Color.RED);

                    lblColumn5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int deletedItem = myDb3.deleteData(Integer.toString(id));
                            if (deletedItem <= 0) {
                                Toast.makeText(ReceivedSap2.this, "Item not remove", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReceivedSap2.this, "Item removed", Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        }
                    });
                    tableRow.addView(lblColumn5);
                    tableLayout.addView(tableRow);
                }
            }
        }
    }

    public String cutWord(String value){
        String result;
        int limit = 14;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }

    public void gotoFunction(){
        Button btnGoto = findViewById(R.id.btnGoto);
        btnGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent;
                intent = new Intent(getBaseContext(), ReceivedSap.class);
                startActivity(intent);
            }
        });
    }
}