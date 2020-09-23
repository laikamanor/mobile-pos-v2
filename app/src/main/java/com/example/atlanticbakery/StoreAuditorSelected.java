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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.Objects;

public class StoreAuditorSelected extends AppCompatActivity {
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    user_class uc = new user_class();
    actualendbal_class ac = new actualendbal_class();
    receivedsap_class recsap = new receivedsap_class();
    DatabaseHelper4 myDb4;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    DatabaseHelper myDb;
    DatabaseHelper3 myDb3;
    Button btnProeed;

    long mLastClickTime = 0;
    String type;
    String title;
    DecimalFormat df = new DecimalFormat("#,###");
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_auditor_selected);
        myDb = new DatabaseHelper(this);
        myDb4 = new DatabaseHelper4(this);
        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("title");
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
                boolean isStoreExist = ac.isTypeExist(StoreAuditorSelected.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(StoreAuditorSelected.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(StoreAuditorSelected.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(StoreAuditorSelected.this, "PO Store Count");
                boolean isAuditorPullOutExist = ac.isTypeExist(StoreAuditorSelected.this, "PO Auditor Count");
                boolean isFinalPullOutExist = ac.isTypeExist(StoreAuditorSelected.this, "PO Final Count");
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
                        startActivity(uic.goTo(StoreAuditorSelected.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(StoreAuditorSelected.this, ShoppingCart.class));
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
                        }else if(!uc.returnWorkgroup(StoreAuditorSelected.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(StoreAuditorSelected.this).equals("Manager")){
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
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogConfirmation = new AlertDialog.Builder(StoreAuditorSelected.this);
                dialogConfirmation.setTitle("Confirmation");
                dialogConfirmation.setMessage("Are you sure you want to proceed?");
                dialogConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        int userid = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
                        boolean isSuccess = ac.insertActualEnding(StoreAuditorSelected.this, type,userid);
                        if(isSuccess) {
                            Toast.makeText(StoreAuditorSelected.this, "Transaction Completed", Toast.LENGTH_SHORT).show();
                            startActivity(getIntent());
                            finish();
                        }else {
                            Toast.makeText(StoreAuditorSelected.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogConfirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialogConfirmation.show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void loadData() {
        if (myDb4.countItems(type) > 0) {
            btnProeed.setVisibility(View.VISIBLE);

            TableLayout tableLayout = findViewById(R.id.table_main);
            tableLayout.removeAllViews();
            Cursor cursor = myDb4.getAllData(type);
            if (cursor != null) {
                btnProeed.setVisibility(View.VISIBLE);
                TableRow tableColumn = new TableRow(StoreAuditorSelected.this);
                String[] columns = {"Item", "Qty.", "Action"};

                for (String s : columns) {
                    TextView lblColumn1 = new TextView(StoreAuditorSelected.this);
                    lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn1.setText(s);
                    lblColumn1.setPadding(10, 0, 10, 0);
                    tableColumn.addView(lblColumn1);
                }
                tableLayout.addView(tableColumn);

                cursor = myDb4.getAllData(type);
                while (cursor.moveToNext()) {
                    final TableRow tableRow = new TableRow(StoreAuditorSelected.this);
                    String itemName = cursor.getString(1);
                    String v = cutWord(itemName);
                    double quantity = cursor.getDouble(2);
                    final int id = cursor.getInt(0);

                    TextView lblColumn1 = new TextView(StoreAuditorSelected.this);
                    lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn1.setText(v);
                    lblColumn1.setPadding(10, 0, 10, 0);
                    tableRow.addView(lblColumn1);

                    TextView lblColumn2 = new TextView(StoreAuditorSelected.this);
                    lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn2.setText(df.format(quantity));
                    lblColumn2.setPadding(10, 10, 10, 10);
                    tableRow.addView(lblColumn2);

                    TextView lblColumn5 = new TextView(StoreAuditorSelected.this);
                    lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn5.setTag(id);
                    lblColumn5.setText("Remove");
                    lblColumn5.setPadding(10, 10, 10, 10);
                    lblColumn5.setTextColor(Color.RED);

                    lblColumn5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int deletedItem = myDb4.deleteData(Integer.toString(id));
                            if (deletedItem <= 0) {
                                Toast.makeText(StoreAuditorSelected.this, "Item not remove", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(StoreAuditorSelected.this, "Item removed", Toast.LENGTH_SHORT).show();
                                startActivity(getIntent());
                                finish();
                            }
                        }
                    });
                    tableRow.addView(lblColumn5);
                    tableLayout.addView(tableRow);
                }
            }
        }else{
            btnProeed.setVisibility(View.GONE);
        }
    }

    public String cutWord(String value){
        String result;
        int limit = 30;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(StoreAuditorSelected.this);
                        startActivity(uic.goTo(StoreAuditorSelected.this, MainActivity.class));
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
        int totalPendingSAP = recsap.returnPendingSAPNotif(StoreAuditorSelected.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }
}