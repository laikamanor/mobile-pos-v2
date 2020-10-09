package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.sql.Connection;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class Received extends AppCompatActivity {
    Connection con;
    DatabaseHelper myDb;
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DatabaseHelper5 myDb5;
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    actualendbal_class ac = new actualendbal_class();
    connection_class cc = new connection_class();
    received_class rc = new received_class();
    receivedsap_class recsap = new receivedsap_class();
    user_class uc = new user_class();
    String transfer_type;

    DecimalFormat df = new DecimalFormat("#,###");
    long mLastClickTime = 0;

    Button btnProceed;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    String title;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);
        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);
        myDb3 = new DatabaseHelper3(this);
        myDb5 = new DatabaseHelper5(this);

        title = getIntent().getStringExtra("title");
        transfer_type = getIntent().getStringExtra("transfer_type");
        System.out.println(transfer_type);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean isStoreExist = ac.isTypeExist(Received.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(Received.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(Received.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(Received.this, "Store Count Pull Out");
                boolean isAuditorPullOutExist = ac.isTypeExist(Received.this, "Auditor Count Pull Out");
                boolean isFinalPullOutExist = ac.isTypeExist(Received.this, "Final Count Pull Out");
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
                        startActivity(uic.goTo(Received.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(Received.this, ShoppingCart.class));
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
                        }else if(!uc.returnWorkgroup(Received.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(Received.this).equals("Manager")){
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


        btnProceed = findViewById(R.id.btnProceed);

        loadItems();

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wholeInventory();
            }
        });
    }

    public void wholeInventory() {
        LoadingDialog loadingDialog = new LoadingDialog(Received.this);
        loadingDialog.startLoadingDialog();
        final String[] selectedSpinner = new String[1];
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(Received.this);
        myDialog.setCancelable(false);
        LinearLayout layout = new LinearLayout(Received.this);
        layout.setPadding(40, 40, 40, 40);
        layout.setOrientation(LinearLayout.VERTICAL);

        String lblBranchText = "";
        assert title != null;
        if (title.equals("Manual Received from Direct Supplier")) {
            lblBranchText = "Supplier";
        } else if (transfer_type != null && transfer_type.equals("Manual Transfer Out") || title.equals("Manual Received from Other Branch")) {
            lblBranchText = "Branch";
        } else if (transfer_type != null && transfer_type.equals("Transfer to Sales")) {
            lblBranchText = "Sales Agent";
        } else if (transfer_type != null && transfer_type.equals("Transfer from Sales")) {
            lblBranchText = "Main Branch";
        }


        TextView lblBranch = new TextView(Received.this);
        final Spinner cmbDiscountType = new Spinner(Received.this);
        if (transfer_type != null) {
            if (transfer_type.equals("Transfer Out") || title.equals("Manual Received from Direct Supplier") || title.equals("Manual Received from Other Branch") || transfer_type.equals("Transfer to Sales") || transfer_type.equals("Transfer from Sales")) {
                lblBranch.setText(lblBranchText + ":");
                lblBranch.setTextSize(15);
                lblBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblBranch);
                layout.addView(cmbDiscountType);

                List<String> discounts = rc.returnBranchSupplier(Received.this, lblBranchText);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Received.this, android.R.layout.simple_spinner_item, discounts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cmbDiscountType.setAdapter(adapter);
            }
        }

        cmbDiscountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinner[0] = cmbDiscountType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TextView lblSAPNumber1 = new TextView(Received.this);
        lblSAPNumber1.setText("SAP #:");
        lblSAPNumber1.setTextSize(15);
        lblSAPNumber1.setGravity(View.TEXT_ALIGNMENT_CENTER);

        if (transfer_type != null && !transfer_type.equals("Transfer to Sales")) {
            if (transfer_type != null && !transfer_type.equals("Transfer from Sales")) {
                layout.addView(lblSAPNumber1);
            }
        }

        final EditText txtSAPNumber = new EditText(Received.this);
        final CheckBox toFollow = new CheckBox(Received.this);
        final CheckBox chckAddSAP = new CheckBox(Received.this);
        toFollow.setText("To Follow");

        if (title.equals("Manual Received from Adjustment") || title.equals("Manual Adjustment Out")) {
            chckAddSAP.setText("Add SAP");
            chckAddSAP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        toFollow.setEnabled(true);
                        txtSAPNumber.setEnabled(true);
                    } else {
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
        } else {
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
        if (transfer_type != null && !transfer_type.equals("Transfer to Sales")) {
            if (transfer_type != null && !transfer_type.equals("Transfer from Sales")) {
                layout.addView(toFollow);
            }
        }

        txtSAPNumber.setTextSize(15);
        txtSAPNumber.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtSAPNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(6);
        txtSAPNumber.setFilters(fArray);
        if (transfer_type != null && !transfer_type.equals("Transfer to Sales")) {
            if (transfer_type != null && !transfer_type.equals("Transfer from Sales")) {
                layout.addView(txtSAPNumber);
            }
        }

        TextView lblRemarks = new TextView(Received.this);
        lblRemarks.setText("Remarks:");
        lblRemarks.setTextSize(15);
        lblRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(lblRemarks);

        final EditText txtRemarks = new EditText(Received.this);
        txtRemarks.setTextSize(15);
        txtRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(txtRemarks);
        myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadingDialog.startLoadingDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!toFollow.isChecked() && txtSAPNumber.getText().toString().equals("") && chckAddSAP.isChecked()) {
                            Toast.makeText(Received.this, "SAP # field is empty", Toast.LENGTH_SHORT).show();
                        } else if (!toFollow.isChecked() && txtSAPNumber.getText().toString().length() < 6 && chckAddSAP.isChecked()) {
                            Toast.makeText(Received.this, "SAP # should 6 numbers", Toast.LENGTH_SHORT).show();
                        } else if (cmbDiscountType.getSelectedItemPosition() == 0 && !selectedSpinner[0].equals("")) {
                            Toast.makeText(Received.this, selectedSpinner[0] + " field is empty", Toast.LENGTH_SHORT).show();
                        } else if (txtRemarks.getText().toString().equals("")) {
                            Toast.makeText(Received.this, "Remarks field is empty", Toast.LENGTH_SHORT).show();
                        } else {
                            String remarks = txtRemarks.getText().toString();
                            String columnName = "";
                            String operator = "";
                            String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
                            switch (title) {
                                case "Manual Received from Production":
                                    columnName = "productionin";
                                    operator = "+";
                                    break;
                                case "Manual Received from Other Branch":
                                    columnName = "itemin";
                                    operator = "+";
                                    break;
                                case "Manual Received from Direct Supplier":
                                    columnName = "supin";
                                    operator = "+";
                                    break;
                                case "Manual Received from Adjustment":
                                    columnName = "adjustmentin";
                                    operator = "+";
                                    break;
                                case "Manual Adjustment Out":
                                    columnName = "pullout";
                                    operator = "-";
                                    break;
                                case "Manual Transfer Out":
                                    columnName = "transfer";
                                    operator = "-";
                                    break;
                                case "Transfer to Sales":
                                    columnName = "salesout";
                                    operator = "-";
                                    break;
                            }
                            if (transfer_type != null && transfer_type.equals("Transfer to Sales")) {
                                columnName = "salesout";
                                operator = "-";
                            } else if (transfer_type != null && transfer_type.equals("Transfer from Sales")) {
                                columnName = "salesin";
                                operator = "+";
                            }
                            String sapNumber;
                            if (toFollow.isChecked()) {
                                sapNumber = "To Follow";
                            } else {
                                sapNumber = txtSAPNumber.getText().toString();
                            }
                            saveDataRec(operator, columnName, sapNumber, remarks, selectedSpinner[0]);
                        }
                        loadingDialog.dismissDialog();
                    }
                }, 500);
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

    public  void onBtnLogout(){
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
        int totalPendingSAP = recsap.returnPendingSAPNotif(Received.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveDataRec(String operator, String columnName, String sapNumber, String remarks,String selectedBranch) {
        try {
            String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
            String transnumTitle;
            if(transfer_type != null && transfer_type.equals("Transfer to Sales")){
                transnumTitle = "Transfer to Sales";
            }else if(transfer_type != null && transfer_type.equals("Transfer from Sales")){
                transnumTitle = "Transfer from Sales";
            }else{
                transnumTitle = title;
            }
            String transactionNumber = rc.returnTransactionNumber(Received.this,transnumTitle,columnName);
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                Cursor cursor = myDb2.getAllData(title);
                if(cursor != null){
                    double totalAmount = 0.00;
                    String ar_num = ac.getArNum(Received.this, "AR Charge", "CH");
                    while (cursor.moveToNext()) {
                        double quantity;
                        String itemName;

                        quantity = cursor.getDouble(2);
                        itemName = cursor.getString(1);

                       if(transfer_type != null && transfer_type.equals("Transfer to Sales")){
                           System.out.println("1");
                       }else if(transfer_type != null && transfer_type.equals("Transfer from Sales")){
                           System.out.println("2");
                       }else{
                           System.out.println("3");
                           String query1 = "UPDATE tblinvitems SET " + columnName + "+=" + quantity + (operator.equals("+") ? ",totalav+=" + quantity : "") + ",endbal" + operator + "=" + quantity + ",variance" + (operator.equals("+") ? "-" : "+") + "=" + quantity + " WHERE itemname='" + itemName + "' AND invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND area='Sales';";
                           Statement stmt = con.createStatement();
                           stmt.executeUpdate(query1);
                       }

                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        String procby = Objects.requireNonNull(sharedPreferences.getString("username", ""));

                        String type;
                        switch (title) {
                            case "Manual Received from Adjustment":
                                type = "Adjustment Item";
                                break;
                            case "Manual Transfer Out":
                            case "Transfer to Sales":
                                type = "Transfer Item";
                                break;
                            case ("Manual Adjustment Out"):
                                type = "Adjustment Out Item";
                                break;
                            default:
                                type = "Received Item";
                                break;
                        }
                        if(transfer_type != null &&  transfer_type.equals("Transfer to Sales")){
                            type = "Transfer Item";
                        }else if(transfer_type != null &&  transfer_type.equals("Transfer from Sales")){
                            type = "Received Item";
                        }
                        String fromBranch = (selectedBranch != null && !selectedBranch.equals("") ? "'" + selectedBranch + "'" : "");
                        String sapdoc = (title.equals("Manual Received from Direct Supplier") ? "GRPO" : "IT");
                        String type2;
                        if(transfer_type != null && transfer_type.equals("Transfer to Sales")){
                            type2 = "Transfer to Sales";
                        }else if(transfer_type != null && transfer_type.equals("Transfer from Sales")){
                            type2 = "Transfer from Sales";
                        }else{
                           type2 = title.replace("Manual ", "");
                        }
                        if(transfer_type != null &&  transfer_type.equals("Transfer from Sales")){
                            int userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
                            fromBranch = "(SELECT username FROM tblusers WHERE systemid=" + userID + ")";
                        }
                        String status2 = "Completed";
                        if(transfer_type != null && transfer_type.equals("Transfer to Sales")){
                            sapdoc = "";
                            status2 = "Pending";
                        }else if(transfer_type != null && transfer_type.equals("Transfer from Sales")){
                            sapdoc = "";
                            status2 = "Pending";
                        }
                        String query2 = "INSERT INTO tblproduction (transaction_number,inv_id,item_code,item_name,category,quantity,reject,charge,sap_number,remarks,date,processed_by,type,area,status,transfer_from,transfer_to,typenum,type2) VALUES ('" + transactionNumber + "',(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC),(SELECT itemcode FROM tblitems WHERE itemname='" + itemName + "'),'" + itemName + "',(SELECT category FROM tblitems WHERE itemname='" + itemName + "')," + quantity + ",0,0,'" + sapNumber + "','" + remarks + "',(SELECT GETDATE()),'" + procby + "','" + type + "','Sales','" + status2 + "'," + (fromBranch.equals("") ? "(SELECT branchcode + ' (SLS)' FROM tblbranch WHERE main='1')" : fromBranch ) + ",(SELECT branchcode + '" + (columnName.equals("pullout") ? " (PRD)" : " (SLS)") + "' FROM tblbranch WHERE main='1'),'" + sapdoc + "','" + type2  + "');";
                        Statement stmt2 = con.createStatement();
                        stmt2.executeUpdate(query2);
                        if(transfer_type != null && transfer_type.equals("Transfer from Sales")){
                            int userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
                            double salesCharge = rc.getSalesCharge(Received.this,itemName,quantity,userID);
                            salesCharge = salesCharge - quantity;
                            if(Math.abs(salesCharge) > 0){
                                totalAmount += ac.chargeFunctionItems(Received.this,ar_num,itemName,Math.abs(salesCharge));
                            }
                        }
                    }
                    con.close();
                    if(transfer_type != null && transfer_type.equals("Transfer from Sales") && totalAmount > 0){
                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        int userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
                        ac.chargeFunctionTransaction(Received.this,totalAmount, ar_num,userID,"Own Inventory");
                    }
                    myDb2.truncateTable();
                    showMessage("Atlantic Bakery", "REFERENCE #: " + "\n" + transactionNumber);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, "saveData() " + ex, Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }


    public void showMessage(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(getIntent());
                finish();
            }
        });

        builder.show();
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadItems() {
        LoadingDialog loadingDialog = new LoadingDialog(Received.this);
        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Cursor cursor;
                final String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();

                int count;
                count = myDb2.countItems(title);
                LinearLayout layout = findViewById(R.id.layoutNoItems);
                if (count == 0) {
                    layout.setVisibility(View.VISIBLE);
                    Button btnGoto = findViewById(R.id.btnGoto);
                    btnGoto.setOnClickListener(new View.OnClickListener() {
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
                    btnProceed.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.GONE);

                    btnProceed.setVisibility(View.VISIBLE);

                    TableRow tableColumn = new TableRow(Received.this);
                    final TableLayout tableLayout = findViewById(R.id.table_main);
                    tableLayout.removeAllViews();
                    String[] columns = {"Item", "Qty.", "Action"};

                    for (String s : columns) {
                        TextView lblColumn1 = new TextView(Received.this);
                        lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn1.setText(s);
                        lblColumn1.setPadding(10, 0, 10, 0);
                        tableColumn.addView(lblColumn1);
                    }
                    tableLayout.addView(tableColumn);
                    cursor = myDb2.getAllData(title);

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            final TableRow tableRow = new TableRow(Received.this);
                            String itemName = cursor.getString(1);
                            String v = cutWord(itemName);
                            System.out.println(v);
                            double quantity = cursor.getDouble(2);
                            final int id = cursor.getInt(0);

                            TextView lblColumn1 = new TextView(Received.this);
                            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn1.setText(v);
                            lblColumn1.setPadding(10, 0, 10, 0);
                            tableRow.addView(lblColumn1);

                            TextView lblColumn2 = new TextView(Received.this);
                            lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn2.setText(df.format(quantity));
                            lblColumn2.setPadding(10, 10, 10, 10);
                            tableRow.addView(lblColumn2);

                            TextView lblColumn3 = new TextView(Received.this);
                            lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn3.setTag(id);
                            lblColumn3.setText("Remove");
                            lblColumn3.setPadding(10, 10, 10, 10);
                            lblColumn3.setTextColor(Color.RED);

                            lblColumn3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int deletedItem;
                                    deletedItem = myDb2.deleteData(Integer.toString(id));
                                    if (deletedItem < 0) {
                                        Toast.makeText(Received.this, "Item not remove", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Received.this, "Item removed", Toast.LENGTH_SHORT).show();
                                        loadItems();
                                    }

                                    if (myDb2.countItems(title).equals(0)) {
                                        tableLayout.removeAllViews();
                                        btnProceed.setVisibility(View.GONE);
                                    }
                                }
                            });

                            tableRow.addView(lblColumn3);

                            tableLayout.addView(tableRow);
                        }
                    }
                }
                loadingDialog.dismissDialog();
            }
        }, 500);
    }

    public String cutWord(String value){
        String result;
        int limit = 15;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}