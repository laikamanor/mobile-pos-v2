package com.example.atlanticbakery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReceivedSap extends AppCompatActivity {
    receivedsap_class rs = new receivedsap_class();
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    connection_class cc = new connection_class();
    private DrawerLayout drawerLayout;

    long mLastClickTime = 0;
    Connection con;
    DecimalFormat df = new DecimalFormat("#,###.00");

    SwipeRefreshLayout swipeRefreshLayout;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_sap);

        NavigationView navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

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
                        startActivity(uic.goTo(ReceivedSap.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(ReceivedSap.this, ShoppingCart.class));
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
                    case R.id.nav_receivedSap :
                        result = true;
                        intent = new Intent(getBaseContext(), ReceivedSap.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });
        final AutoCompleteTextView txtSearch = findViewById(R.id.txtSearch);
        txtSearch.setAdapter(fillSapNumber(returnSapNumber()));

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(txtSearch.getText().toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sapNumber = txtSearch.getText().toString();
                boolean isSAPExist = checkSapNumberExisted(Integer.parseInt(sapNumber));
                if(isSAPExist){
                    showMessage("Atlantic Bakery", "'" + sapNumber + "' not found or already exist in POS database");
                }else{
                    loadData(sapNumber);
                }
            }
        });
    }

    public ArrayAdapter<String> fillSapNumber(List<String> names){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<String> returnSapNumber(){
        final List<String>  result = new ArrayList<>();
        try {
            con = cc.connectionClass(ReceivedSap.this);
            if (con == null) {
                Toast.makeText(this, "returnSapNumber() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query2 = "SELECT DISTINCT docNum [sap_number] FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] WHERE CAST(DocDate AS date)=(select cast(getdate() as date)) ORDER BY docNum";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);
                while (rs2.next()){
                    con = cc.connectionClass(ReceivedSap.this);
                    String query = "SELECT transaction_id FROM tblproduction WHERE CAST(date AS date)=(select cast(getdate() as date)) AND sap_number='" + rs2.getString("sap_number") + "' AND sap_number !='To Follow' AND status='Completed';";
                    Statement stmt = con.createStatement();
                    final ResultSet result2 = stmt.executeQuery(query);
                    if(!result2.next()) {
                        result.add(rs2.getString("sap_number"));
                    }
                }
                con.close();
            }
        }catch (Exception ex){
            Toast.makeText(this, "returnSAPNumber() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadData(final String sapNumber){
        String fromBranch = returnBranchCode(Integer.parseInt(sapNumber));
        try{
            con = cc.connectionClass(ReceivedSap.this);
            if (con == null) {
                Toast.makeText(ReceivedSap.this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT Dscription [item_name],Quantity [quantity],FromWhsCod [fromWhat] FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] WHERE CAST(DocDate AS date)=(select cast(getdate() as date)) AND DocNum=" + sapNumber + " ORDER BY docNum";
                Statement stmt = con.createStatement();
                final ResultSet result = stmt.executeQuery(query);
                final LinearLayout layout = findViewById(R.id.parentLayout);
                layout.removeAllViews();

                LinearLayout.LayoutParams layoutParamsSapNumber = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParamsItems = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParamsActualQuantity = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParamsView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                layoutParamsSapNumber.setMargins(0,0,0,30);
                layoutParamsSapNumber.setMargins(0,0,0,20);
                layoutParamsActualQuantity.setMargins(0,0,0,20);
                TextView lblSapNumber = new TextView(this);
                lblSapNumber.setLayoutParams(layoutParamsSapNumber);
                lblSapNumber.setTextSize(30);
                lblSapNumber.setText("IT #: " + sapNumber + "\n" + "From Branch: " + fromBranch);
                layout.addView(lblSapNumber);

                while (result.next()){
                    con = cc.connectionClass(ReceivedSap.this);
                    String query2 = "SELECT transaction_id FROM tblproduction WHERE CAST(date AS date)=(select cast(getdate() as date)) AND sap_number='" + sapNumber + "' AND sap_number !='To Follow' AND status='Completed';";
                    Statement stmt2 = con.createStatement();
                    final ResultSet result2 = stmt2.executeQuery(query2);
                    if(!result2.next()){
                        TextView lblItemName = new TextView(this);
                        lblItemName.setLayoutParams(layoutParamsItems);
                        lblItemName.setText(result.getString("item_name"));
                        lblItemName.setTextSize(20);
                        layout.addView(lblItemName);

                        TextView lblQuantity = new TextView(this);
                        lblQuantity.setLayoutParams(layoutParamsItems);

                        final double quantity = result.getDouble("quantity");

                        lblQuantity.setText(df.format(quantity) + " pcs.");
                        lblQuantity.setLayoutParams(layoutParamsItems);
                        lblQuantity.setTextSize(20);
                        layout.addView(lblQuantity);

                        TextView lblActualQuantity = new TextView(this);
                        lblActualQuantity.setText("Actual Quantity:");
                        lblActualQuantity.setLayoutParams(layoutParamsItems);
                        lblActualQuantity.setTextSize(15);
                        layout.addView(lblActualQuantity);

                        final EditText txtActualQuantity = new EditText(this);
                        final TextView lblVariance = new TextView(this);
                        final double[] variance = {0};

                        txtActualQuantity.setText("0");
                        txtActualQuantity.setLayoutParams(layoutParamsActualQuantity);
                        txtActualQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtActualQuantity.setTextSize(20);

                        txtActualQuantity.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String value = txtActualQuantity.getText().toString();
                                if(value.matches("")){
                                    variance[0] = 0 - quantity;
                                }else{
                                    variance[0] =  Double.parseDouble(txtActualQuantity.getText().toString()) - quantity;
                                }
                                lblVariance.setText("Variance: " + df.format(variance[0]));
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        txtActualQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(!b){
                                    String value = txtActualQuantity.getText().toString();
                                    if(value.matches("")){
                                        txtActualQuantity.setText("0");
                                    }
                                }
                            }
                        });

                        layout.addView(txtActualQuantity);

                        double v = 0 - quantity;
                        lblVariance.setText("Variance: " + df.format(v));
                        lblVariance.setLayoutParams(layoutParamsItems);
                        lblVariance.setTextSize(15);
                        layout.addView(lblVariance);

                        View view = new View(this);
                        view.setLayoutParams(layoutParamsView);
                        view.setBackgroundColor(Color.BLACK);
                        layout.addView(view);
                    }
                }
                con.close();

                Button btnProceed = new Button(this);
                btnProceed.setLayoutParams(layoutParamsSapNumber);
                btnProceed.setText("Proceed");
                btnProceed.setTextSize(20);
                btnProceed.setBackgroundResource(R.color.colorPrimary);
                btnProceed.setTextColor(Color.WHITE);
                btnProceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        final AlertDialog.Builder myDialog = new AlertDialog.Builder(ReceivedSap.this);
                        myDialog.setCancelable(false);
                        LinearLayout layout = new LinearLayout(ReceivedSap.this);
                        layout.setPadding(40, 40, 40, 40);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        myDialog.setView(layout);

                        TextView lblRemarks = new TextView(ReceivedSap.this);
                        lblRemarks.setText("Remarks:");
                        lblRemarks.setTextSize(15);
                        lblRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        layout.addView(lblRemarks);

                        final EditText txtRemarks = new EditText(ReceivedSap.this);
                        txtRemarks.setTextSize(15);
                        txtRemarks.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        layout.addView(txtRemarks);

                        myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String remarks = txtRemarks.getText().toString().trim();
                                if(remarks.matches("")){
                                    Toast.makeText(ReceivedSap.this, "Remarks field is empty", Toast.LENGTH_SHORT).show();
                                }else{
                                    String branch = returnBranchCode(Integer.parseInt(sapNumber)).trim();
                                    String columnName;
                                    if(branch.equals("A1 P-FG")){
                                        columnName = "productionin";
                                    }else{
                                        columnName = "itemin";
                                    }
                                    saveData("+",columnName,sapNumber,remarks,branch);
                                    final LinearLayout layout = findViewById(R.id.parentLayout);
                                    layout.removeAllViews();
                                }
                            }
                        });

                        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        myDialog.show();
                    }
                });
                layout.addView(btnProceed);
            }
        }catch (Exception ex){
            Toast.makeText(ReceivedSap.this, "loadSAP() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveData(String operator, String columnName, String sapNumber, String remarks,String selectedBranch) {
        try {
            String transactionNumber = returnTransactionNumber(columnName);
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String title = (columnName.equals("itemin")) ? "Received from Other Branch" : "Received from Production";
                String query = "SELECT Dscription [item_name],Quantity [quantity] FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] WHERE CAST(DocDate AS date)=(select cast(getdate() as date)) AND DocNum=" + sapNumber + " ORDER BY docNum";
                Statement statement = con.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    double quantity = rs.getDouble("quantity");
                    String itemName = rs.getString("item_name");
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
                }
                con.close();
                showMessage("Atlantic Bakery", "REFERENCE #: " + "\n" + transactionNumber);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "saveData() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String returnTransactionNumber(String columnName){
        String result = "";
        String type2 = (columnName.equals("itemin")) ? "Received from Other Branch" : "Received from Production";
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

    public boolean checkSapNumberExisted(Integer sapNumber){
        boolean result = false;
        try{
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT Dscription [item_name],Quantity [quantity],FromWhsCod [fromWhat] FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] WHERE CAST(DocDate AS date)=(select cast(getdate() as date)) AND DocNum=" + sapNumber + " ORDER BY docNum";
                Statement stmt = con.createStatement();
                final ResultSet rs = stmt.executeQuery(query);
                if (rs.next()){
                    con = cc.connectionClass(ReceivedSap.this);
                    String query2 = "SELECT transaction_id FROM tblproduction WHERE CAST(date AS date)=(select cast(getdate() as date)) AND sap_number='" + sapNumber + "' AND sap_number !='To Follow' AND status='Completed';";
                    Statement stmt2 = con.createStatement();
                    final ResultSet rs2 = stmt2.executeQuery(query2);
                    if(rs2.next()){
                        System.out.println("Stock here at 2");
                        result = true;
                    }
                }else{
                    System.out.println("Stock here at 1");
                    result = true;
                }
                con.close();
            }

        }catch (Exception ex){
            Toast.makeText(ReceivedSap.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String returnBranchCode(Integer sapNumber){
        String result = "";
        try{
            con = cc.connectionClass(ReceivedSap.this);
            if (con == null) {
                Toast.makeText(ReceivedSap.this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT FromWhsCod [result] FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] WHERE DocNum=" + sapNumber + " AND CAST(DocDate AS date)=(select cast(getdate() as date));";
                Statement stmt = con.createStatement();
                final ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = rs.getString("result");
                }
            }
        }catch (Exception ex){
            Toast.makeText(ReceivedSap.this, "returnBranchCode() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(ReceivedSap.this);
                        startActivity(uic.goTo(ReceivedSap.this, MainActivity.class));
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