package com.example.atlanticbakery;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
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
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class AvailableItems extends AppCompatActivity {

    ui_class uic = new ui_class();
    user_class uc = new user_class();
    inventory_class ic = new inventory_class();
    connection_class cc = new connection_class();
    prefs_class pc = new prefs_class();
    received_class rc = new received_class();
    item_class itemc = new item_class();
    receivedsap_class recsap = new receivedsap_class();
    actualendbal_class ac = new actualendbal_class();
    DecimalFormat df = new DecimalFormat("#,###");
    SharedPreferences sharedPreferences;
    long mLastClickTime = 0;
    AutoCompleteTextView txtSearch;

    Connection con;
    DatabaseHelper myDb;
    DatabaseHelper2 myDb2;
    DatabaseHelper4 myDb4;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_items);

        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);
        myDb4 = new DatabaseHelper4(this);

        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);

        final String title = getIntent().getStringExtra("title");
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
                boolean isStoreExist = ac.isTypeExist(AvailableItems.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(AvailableItems.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(AvailableItems.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(AvailableItems.this, "PO Store Count");
                boolean isAuditorPullOutExist = ac.isTypeExist(AvailableItems.this, "PO Auditor Count");
                boolean isFinalPullOutExist = ac.isTypeExist(AvailableItems.this, "PO Final Count");
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
                        startActivity(uic.goTo(AvailableItems.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(AvailableItems.this, ShoppingCart.class));
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
                        intent.putExtra("title", "Manual Transfer Out ");
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
                        }else if(!uc.returnWorkgroup(AvailableItems.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(AvailableItems.this).equals("Manager")){
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

        Button btnSearch = findViewById(R.id.btnSearch);
        txtSearch = findViewById(R.id.txtSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String search = txtSearch.getText().toString();
                if(title.equals("AC Store Count List Items") || title.equals("AC Auditor Count List Items") || title.equals("AC Final Count List Items") || title.equals("PO Store Count List Items") || title.equals("PO Auditor Count List Items") || title.equals("PO Final Count List Items")){
                    loadActualEndingBalance(search);
                }else{
                    loadData(search);
                }
            }
        });

        if(title.equals("AC Store Count List Items") || title.equals("AC Auditor Count List Items") || title.equals("AC Final Count List Items")|| title.equals("PO Store Count List Items") || title.equals("PO Auditor Count List Items") || title.equals("PO Final Count List Items")){
            loadActualEndingBalance("");
        }else{
            loadData("");
        }
    }
    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(AvailableItems.this);
                        startActivity(uic.goTo(AvailableItems.this, MainActivity.class));
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
        int totalPendingSAP = recsap.returnPendingSAPNotif(AvailableItems.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    public void loadData(final String value) {
        String query = "";
        final String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
        String latestInventoryDate = ic.returnLatestInventoryDate(AvailableItems.this);
        int userid = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
        switch (title) {
            case "Received from SAP":
                query = "SELECT DISTINCT ISNULL(a.Dscription,0) [item],SUM(a.Quantity) [quantity] FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] a OUTER APPLY(SELECT DISTINCT ISNULL(b.sap_number,0) [sap_number] FROM tblproduction b WHERE b.sap_number != a.DocNum AND b.sap_number !='To Follow' AND b.status='Completed' AND CAST(b.date AS date)=a.DocDate) x WHERE CAST(a.DocDate AS date)=(select cast(getdate() as date)) AND x.sap_number IS NOT NULL AND a.DocNum=" + Integer.parseInt(value) + " GROUP BY a.Dscription";
//                query = "SELECT DISTINCT ISNULL(a.DocNum,0) [item],SUM(a.Quantity) [quantity] FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] a OUTER APPLY(SELECT DISTINCT ISNULL(b.sap_number,0) [sap_number] FROM tblproduction b WHERE b.sap_number != a.DocNum AND b.sap_number !='To Follow' AND b.status='Completed' AND CAST(b.date AS date)=a.DocDate) x WHERE CAST(a.DocDate AS date)=(select cast(getdate() as date)) AND x.sap_number IS NOT NULL " + (value.matches("") ? "" : " AND a.DocNum=" + Integer.parseInt(value)) + " GROUP BY a.DocNum";
                break;
            case "Manual Received from Production":
            case "Manual Received from Other Branch":
            case "Manual Received from Direct Supplier":
                query = "SELECT a.itemname [item],c.endbal [quantity] FROM funcLoadInventoryItems('" + latestInventoryDate + "','" + value + "','All') a INNER JOIN tblitems b  ON a.itemname = b.itemname INNER JOIN tblinvitems c ON c.itemname = a.itemname WHERE c.invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) ORDER BY c.endbal DESC,a.itemname ASC";
                break;
            case "Manual Transfer Out":
                query = "SELECT a.itemname [item],c.endbal [quantity] FROM funcLoadStockItems('" + latestInventoryDate + "','" + value + "','All') a INNER JOIN tblitems b ON a.itemname = b.itemname INNER JOIN tblinvitems c ON c.itemname = a.itemname WHERE c.invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC);";
                break;
            case "Menu Items":
                query = "SELECT a.itemname [item],a.endbal [quantity] FROM tblinvitems a INNER JOIN tblitems b ON a.itemname = b.itemname WHERE invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND b.status = 1 AND a.status = 1 AND a.itemname LIKE '%" + value + "%' ORDER BY 2 DESC, 1 ASC";
                break;
        }

        GridLayout gridLayout = findViewById(R.id.grid);
        gridLayout.removeAllViews();
        try {
            con = cc.connectionClass(AvailableItems.this);
            if (con == null) {
                Toast.makeText(this, "loadData() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                List<String> items = new ArrayList<>();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    final String item = rs.getString("item");
                    final Double quantity = rs.getDouble("quantity");

                    CardView cardView = new CardView(AvailableItems.this);
                    LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(190, 200);
                    layoutParamsCv.setMargins(20, 10, 10, 10);
                    cardView.setLayoutParams(layoutParamsCv);
                    cardView.setRadius(12);
                    cardView.setCardElevation(5);

                    cardView.setVisibility(View.VISIBLE);
                    gridLayout.addView(cardView);
                    final LinearLayout linearLayout = new LinearLayout(AvailableItems.this);
                    LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
                    linearLayout.setLayoutParams(layoutParamsLinear);
                    linearLayout.setTag(item);

                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int endBal = rc.checkStock(AvailableItems.this, item);
                            boolean checkStock = itemc.checkItemNameStock(AvailableItems.this, item,quantity);
                            String type = (title.equals("Auditor Count List Items")) ? "Auditor Count" : "Store Count";
                            if (!checkStock && title.equals("Menu Items")) {
                                Toast.makeText(AvailableItems.this, "'" + item + "' is not available", Toast.LENGTH_SHORT).show();
                            } else if (endBal <= 0 && title.equals("Menu Items")) {
                                Toast.makeText(AvailableItems.this, "'" + item + "' is not available", Toast.LENGTH_SHORT).show();
                            } else if (endBal <= 0 && title.equals("Manual Transfer Out")) {
                                Toast.makeText(AvailableItems.this, "'" + item + "' is not available", Toast.LENGTH_SHORT).show();
                            } else if (title.equals("Auditor Count List Items") && myDb4.checkItem(item, type)) {
                                Toast.makeText(AvailableItems.this, "'" + item + "' is already exist in Selected Items", Toast.LENGTH_SHORT).show();
                            } else if (title.equals("Store Count List Items") && myDb4.checkItem(item, type)) {
                                Toast.makeText(AvailableItems.this, "'" + item + "' is already exist in Selected Items", Toast.LENGTH_SHORT).show();
                            } else if (!title.equals("Menu Items") && myDb2.checkItem(item, title)) {
                                Toast.makeText(AvailableItems.this, "'" + item + "' is already exist in Selected Items", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent;
                                intent = new Intent(getBaseContext(), ItemInfo.class);
                                intent.putExtra("title", title);
                                intent.putExtra("itemname", item);
                                intent.putExtra("sapNumber", value);
                                intent.putExtra("quantity", Double.toString(quantity));
                                intent.putExtra("fromBranch", "");
                                startActivity(intent);
                            }
                        }
                    });

                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    linearLayout.setVisibility(View.VISIBLE);
                    cardView.addView(linearLayout);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(20, 0, 20, 0);
                    LinearLayout.LayoutParams layoutParamsItemLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsItemLeft.setMargins(20, -50, 0, 10);

                    TextView txtItemName = new TextView(AvailableItems.this);
                    String cutWord = cutWord(item);
                    txtItemName.setText(cutWord);
                    txtItemName.setLayoutParams(layoutParams);
                    txtItemName.setTextSize(13);
                    txtItemName.setVisibility(View.VISIBLE);
                    linearLayout.addView(txtItemName);

                    if (title.matches("Menu Items")) {
                        TextView txtItemLeft = new TextView(AvailableItems.this);
                        txtItemLeft.setLayoutParams(layoutParamsItemLeft);
                        txtItemLeft.setTextSize(10);
                        if (quantity == 0.0) {
                            txtItemLeft.setText(df.format(quantity) + " available");
                            txtItemLeft.setTextColor(Color.RED);
                        } else if (quantity <= 10) {
                            txtItemLeft.setText(df.format(quantity) + " available!");
                            txtItemLeft.setTextColor(Color.rgb(247, 154, 22));
                        } else {
                            txtItemLeft.setText(df.format(quantity) + " available");
                            txtItemLeft.setTextColor(Color.rgb(30, 203, 6));
                        }
                        txtItemLeft.setVisibility(View.VISIBLE);
                        linearLayout.addView(txtItemLeft);

                        if(myDb.checkItem(item)){
                            linearLayout.setBackgroundColor(Color.RED);
                            txtItemName.setTextColor(Color.WHITE);
                        }else{
                            linearLayout.setBackgroundColor(Color.WHITE);
                            txtItemName.setTextColor(Color.BLACK);
                        }

                    } else if (!title.equals("Menu Items") && myDb2.checkItem(item, title)) {
                        linearLayout.setBackgroundColor(Color.RED);
                        txtItemName.setTextColor(Color.WHITE);
                    } else {
                        linearLayout.setBackgroundColor(Color.WHITE);
                        txtItemName.setTextColor(Color.BLACK);
                    }
                    items.add(item);
                }

                txtSearch.setAdapter(fillItems(items));

                final Button btnDone = findViewById(R.id.btnDone);
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        int count;
                        if (title.equals("Menu Items")) {
                            count = myDb.countItems();
                        } else {
                            count = myDb2.countItems(title);
                        }
                        if (count <= 0) {
                            Toast.makeText(AvailableItems.this, "No selected item", Toast.LENGTH_SHORT).show();
                        } else {
                            navigateDone(title);
                        }
                    }
                });

            }
        } catch (Exception ex) {
            Toast.makeText(AvailableItems.this, "loadData() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadActualEndingBalance(String itemName) {
        final Button btnDone = findViewById(R.id.btnDone);
        final String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();
        String type = "";
        switch (title) {
            case "AC Auditor Count List Items":
                type = "Auditor Count";
                break;
            case "AC Store Count List Items":
                type = "Store Count";
                break;
            case "AC Final Count List Items":
                type = "Final Count";
                break;
            case "PO Auditor Count List Items":
                type = "PO Auditor Count";
                break;
            case "PO Store Count List Items":
                type = "PO Store Count";
                break;
            case "PO Final Count List Items":
                type = "PO Final Count";
                break;
        }
        boolean isTypeExist = ac.isTypeExist(AvailableItems.this,type);
        if (!isTypeExist) {
            List<String> results;
            results = ac.loadActualEndBal(AvailableItems.this, itemName,type);
            GridLayout gridLayout = findViewById(R.id.grid);
            gridLayout.removeAllViews();
            List<String> items = new ArrayList<>();
            for (String result : results) {
                String[] words = result.split(",");
                String item_name = "";
                double quantity = 0.0;
                for (int i = 0; i < words.length; i++) {
                    if (i == 0) {
                        item_name = words[i];
                    } else if (i == 1) {
                        quantity = Double.parseDouble(words[i]);
                    }
                }
                CardView cardView = new CardView(AvailableItems.this);
                LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(190, 200);
                layoutParamsCv.setMargins(20, 10, 10, 10);
                cardView.setLayoutParams(layoutParamsCv);
                cardView.setRadius(12);
                cardView.setCardElevation(5);

                cardView.setVisibility(View.VISIBLE);
                gridLayout.addView(cardView);

                final LinearLayout linearLayout = new LinearLayout(AvailableItems.this);
                TextView txtItemName = new TextView(AvailableItems.this);
                TextView txtItemLeft = new TextView(AvailableItems.this);

                LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
                linearLayout.setLayoutParams(layoutParamsLinear);
                linearLayout.setTag(item_name);

                final double finalQuantity = quantity;
                final String finalItem_name = item_name;
                final String finalItem_name1 = item_name;
                final String finalItem_name2 = item_name;
                final String finalType = type;
                final String finalType1 = type;
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        Cursor cursor = myDb4.getAllWhereItem(finalType, finalItem_name2);
                        if (cursor.moveToNext()) {
                            Toast.makeText(AvailableItems.this, "'" + finalItem_name + "' is already exist in Selected Items", Toast.LENGTH_SHORT).show();
                        }else if(title.equals("AC Auditor Count List Items") || title.equals("AC Store Count List Items") || title.equals("PO Auditor Count List Items") || title.equals("PO Store Count List Items")){
                            Intent intent;
                            intent = new Intent(getBaseContext(), ItemInfo.class);
                            intent.putExtra("title", title);
                            intent.putExtra("itemname", finalItem_name1);
                            intent.putExtra("sapNumber", "");
                            intent.putExtra("quantity", Double.toString(finalQuantity));
                            intent.putExtra("fromBranch", "");
                            startActivity(intent);
                        }else{
                            Intent intent;
                            intent = new Intent(getBaseContext(), ItemInfo2.class);
                            intent.putExtra("title", title);
                            intent.putExtra("itemname", finalItem_name1);
                            intent.putExtra("quantity", Double.toString(finalQuantity));
                            startActivity(intent);
                        }
                    }
                });

                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                linearLayout.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(20, 0, 20, 0);
                LinearLayout.LayoutParams layoutParamsItemLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParamsItemLeft.setMargins(20, -50, 0, 10);


                String cutWord = cutWord(item_name);
                txtItemName.setText(cutWord);
                txtItemName.setLayoutParams(layoutParams);
                txtItemName.setTextSize(13);
                txtItemName.setVisibility(View.VISIBLE);
                txtItemLeft.setLayoutParams(layoutParamsItemLeft);
                txtItemLeft.setTextSize(10);
                txtItemLeft.setText(df.format(quantity) + (type.equals("Final Count") || type.equals("PO Final Count") ? " variance" : " available"));
                if(title.equals("AC Auditor Count List Items") || title.equals("AC Store Count List Items") || title.equals("PO Auditor Count List Items") || title.equals("PO Store Count List Items")){
                    txtItemLeft.setVisibility(View.GONE);
                }else{
                    txtItemLeft.setVisibility(View.VISIBLE);
                }

                Cursor cursor = myDb4.getAllWhereItem(type, item_name);
                if (cursor.moveToNext() && quantity > 0) {
                    linearLayout.setBackgroundColor(Color.RED);
                    txtItemName.setTextColor(Color.WHITE);
                    txtItemLeft.setTextColor(Color.WHITE);
                }else if(!cursor.moveToNext() && quantity > 0){
                    linearLayout.setBackgroundResource(R.color.colorPrimary);
                    txtItemName.setTextColor(Color.WHITE);
                    txtItemLeft.setTextColor(Color.WHITE);
                }else {
                    linearLayout.setBackgroundColor(Color.WHITE);
                    txtItemName.setTextColor(Color.BLACK);
                    txtItemLeft.setTextColor(Color.BLACK);
                }

                if(quantity == 0){
                    linearLayout.setBackgroundColor(Color.parseColor("#545454"));
                    txtItemName.setTextColor(Color.WHITE);
                    txtItemLeft.setTextColor(Color.WHITE);
                }

                cardView.addView(linearLayout);
                linearLayout.addView(txtItemName);
                linearLayout.addView(txtItemLeft);

                items.add(item_name);

                txtSearch.setAdapter(fillItems(items));
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navigateDone(title);
                    }
                });
            }
        } else {
            btnDone.setVisibility(View.GONE);
        }
    }

    public ArrayAdapter<String> fillItems(List<String> names){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
    }

    public void navigateDone(final String title){
        if(title.equals("Menu Items")){
            Intent intent;
            intent = new Intent(getBaseContext(), ShoppingCart.class);
            startActivity(intent);
        }else if(title.equals("AC Auditor Count List Items") || title.equals("AC Store Count List Items") || title.equals("AC Final Count List Items") || title.equals("PO Auditor Count List Items") || title.equals("PO Store Count List Items") || title.equals("PO Final Count List Items")){
            String type = "",title2 = "";
            if(title.equals("AC Auditor Count List Items")){
                type = "Auditor Count";
                title2 = "Auditor Count Selected Items";
            }else if(title.equals("AC Store Count List Items")){
                type = "Store Count";
                title2 = "Store Count Selected Items";
            }else if(title.equals("AC Final Count List Items")){
                type = "Final Count";
                title2 = "Final Count Selected Items";
            }else if(title.equals("PO Auditor Count List Items")) {
                type = "PO Auditor Count";
                title2 = "Auditor Count Selected Items Pull Out";
            }else if(title.equals("PO Store Count List Items")){
                type = "PO Store Count";
                title2 = "Store Count Selected Items Pull Out";
            }else if(title.equals("PO Final Count List Items")) {
                type = "PO Final Count";
                title2 = "PO Final Count Selected Items";
            }
            Intent intent;
            intent = new Intent(getBaseContext(), StoreAuditorSelected.class);
            intent.putExtra("title",title2);
            intent.putExtra("type",type);
            startActivity(intent);
        }else {
            Intent intent;
            intent = new Intent(getBaseContext(), Received.class);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }

    public String cutWord(String value){
        String result;
        int limit = 25;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}
