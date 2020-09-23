package com.example.atlanticbakery;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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

public class SalesInventory_AvailableItems extends AppCompatActivity {
    DecimalFormat df = new DecimalFormat("#,###");
    long mLastClickTime = 0;
    AutoCompleteTextView txtSearch;
    Button btnSearch;

    Connection con;
    DatabaseHelper5 myDb5;
    DatabaseHelper myDb;
    connection_class cc = new connection_class();
    user_class uc = new user_class();
    actualendbal_class ac = new actualendbal_class();
    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    receivedsap_class recsap = new receivedsap_class();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_inventory__available_items);

        final String title = getIntent().getStringExtra("title");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDb = new DatabaseHelper(this);
        myDb5 = new DatabaseHelper5(this);
        txtSearch = findViewById(R.id.txtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean isStoreExist = ac.isTypeExist(SalesInventory_AvailableItems.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(SalesInventory_AvailableItems.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(SalesInventory_AvailableItems.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(SalesInventory_AvailableItems.this, "PO Store Count");
                boolean isAuditorPullOutExist = ac.isTypeExist(SalesInventory_AvailableItems.this, "PO Auditor Count");
                boolean isFinalPullOutExist = ac.isTypeExist(SalesInventory_AvailableItems.this, "PO Final Count");
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
                        startActivity(uic.goTo(SalesInventory_AvailableItems.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(SalesInventory_AvailableItems.this, ShoppingCart.class));
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
                        }else if(!uc.returnWorkgroup(SalesInventory_AvailableItems.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(SalesInventory_AvailableItems.this).equals("Manager")){
                            Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                        }else {
                            result = true;
                            intent = new Intent(getBaseContext(), AvailableItems.class);
                            intent.putExtra("title", "AC Final Count List Items");
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_addsalesinventory:
                        result = true;
                        intent = new Intent(getBaseContext(), SalesInventory_AvailableItems.class);
                        intent.putExtra("title", "Add Sales Inventory");
                        startActivity(intent);
                        finish();
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
                    case R.id.nav_createUser:
                        result = true;
                        intent = new Intent(getBaseContext(), CreateUsers.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return result;
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String value = txtSearch.getText().toString();
                if(!value.matches("")){
                    loadData(value);
                }
            }
        });
        loadData("");
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(SalesInventory_AvailableItems.this);
                        startActivity(uic.goTo(SalesInventory_AvailableItems.this, MainActivity.class));
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
        int totalPendingSAP = recsap.returnPendingSAPNotif(SalesInventory_AvailableItems.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    public void loadData(final String value){
        String query = "SELECT a.itemname [item], a.endbal - x.quantity [quantity] FROM tblinvitems a INNER JOIN tblitems b ON a.itemname = b.itemname INNER JOIN tblcat c ON b.category = c.category OUTER APPLY(SELECT ISNULL(SUM(d.endbal),0) [quantity] FROM tblsalesinventory d WHERE  d.itemname=a.itemname AND d.invnum=a.invnum AND d.status=1)x WHERE invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND b.discontinued=0  AND c.status=1 AND a.itemname LIKE '%" + value + "%' GROUP BY a.itemname,a.endbal,x.quantity ORDER BY SUM(a.endbal - x.quantity) DESC, a.itemname ASC";
        final String title = Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString().trim();

        GridLayout gridLayout = findViewById(R.id.grid);
        gridLayout.removeAllViews();
        try{
            con = cc.connectionClass(SalesInventory_AvailableItems.this);
            if (con == null) {
                Toast.makeText(this, "loadData() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                List<String> items = new ArrayList<>();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()){
                    final String item = rs.getString("item");
                    final Double quantity = rs.getDouble("quantity");

                    CardView cardView = new CardView(getBaseContext());
                    LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(190, 200);
                    layoutParamsCv.setMargins(20, 10, 10, 10);
                    cardView.setLayoutParams(layoutParamsCv);
                    cardView.setRadius(12);
                    cardView.setCardElevation(5);

                    cardView.setVisibility(View.VISIBLE);
                    gridLayout.addView(cardView);
                    final LinearLayout linearLayout = new LinearLayout(getBaseContext());
                    LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
                    linearLayout.setLayoutParams(layoutParamsLinear);
                    linearLayout.setTag(item);

                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(quantity == 0){
                                Toast.makeText(getBaseContext(), "'" + item + "' is not available", Toast.LENGTH_SHORT).show();
                            }else if(myDb5.checkItem(item)){
                                Toast.makeText(getBaseContext(), "'" + item + "' is already exist in Selected Items", Toast.LENGTH_SHORT).show();
                            }else{
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

                    TextView txtItemName = new TextView(getBaseContext());
                    String cutWord = cutWord(item);
                    txtItemName.setText(cutWord);
                    txtItemName.setLayoutParams(layoutParams);
                    txtItemName.setTextSize(13);
                    txtItemName.setVisibility(View.VISIBLE);
                    linearLayout.addView(txtItemName);

                    TextView txtItemLeft = new TextView(getBaseContext());
                    txtItemLeft.setLayoutParams(layoutParamsItemLeft);
                    txtItemLeft.setTextSize(10);
                    if(quantity == 0.0){
                        txtItemLeft.setText(df.format(quantity) + " available");
                        txtItemLeft.setTextColor(Color.RED);
                    }else if(quantity <= 10){
                        txtItemLeft.setText(df.format(quantity) + " available!");
                        txtItemLeft.setTextColor(Color.rgb(247,154,22));
                    }else{
                        txtItemLeft.setText(df.format(quantity) + " available");
                        txtItemLeft.setTextColor(Color.rgb(30,203,6));
                    }
                    txtItemLeft.setVisibility(View.VISIBLE);
                    linearLayout.addView(txtItemLeft);

                    if(myDb5.checkItem(item)){
                        linearLayout.setBackgroundColor(Color.RED);
                        txtItemName.setTextColor(Color.WHITE);
                    }else{
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
                        if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        if(myDb5.countItems() <= 0){
                            Toast.makeText(getBaseContext(),"No selected item", Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent;
                            intent = new Intent(getBaseContext(), Received.class);
                            intent.putExtra("title", "Add Sales Inventory");
                            startActivity(intent);
                        }
                    }
                });

            }
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), "loadData() " +  ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public ArrayAdapter<String> fillItems(List<String> names){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
    }


    public String cutWord(String value){
        String result;
        int limit = 25;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }

}