package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.sql.Array;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReceivedSap extends AppCompatActivity {
    connection_class cc = new connection_class();
    receivedsap_class recsap = new receivedsap_class();

    user_class uc = new user_class();
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();
    actualendbal_class ac = new actualendbal_class();

    Connection con;
    DatabaseHelper myDb;
    DatabaseHelper3 myDb3;
    DecimalFormat df = new DecimalFormat("#,###");

    long mLastClickTime = 0;

    TextView lblSapNumber,lblFromBranch,lblSelectedType;
    AutoCompleteTextView txtSearch;
    Button btnSearch,btnDone;
    Spinner spinner;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_sap);
        myDb = new DatabaseHelper(this);
        myDb3 = new DatabaseHelper3(this);

        lblSelectedType = findViewById(R.id.lblSelectedType);
        spinner = findViewById(R.id.spinner);

        List<String> items = Arrays.asList("IT", "PO");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sapNumber = txtSearch.getText().toString();
                if(myDb3.countItems().equals(0)){
                    loadSAPNumber(sapNumber);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.navDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>Received from SAP</font>"));
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean isStoreExist = ac.isTypeExist(ReceivedSap.this, "Store Count");
                boolean isAuditorExist = ac.isTypeExist(ReceivedSap.this, "Auditor Count");
                boolean isFinalExist = ac.isTypeExist(ReceivedSap.this, "Final Count");

                boolean isStorePullOutExist = ac.isTypeExist(ReceivedSap.this, "Store Count Pull Out");
                boolean isAuditorPullOutExist = ac.isTypeExist(ReceivedSap.this, "Auditor Count Pull Out");
                boolean isFinalPullOutExist = ac.isTypeExist(ReceivedSap.this, "Final Count Pull Out");
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
                        startActivity(uic.goTo(ReceivedSap.this, ScanQRCode.class));
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
                        startActivity(uic.goTo(ReceivedSap.this, ShoppingCart.class));
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
                        }else if(!uc.returnWorkgroup(ReceivedSap.this).equals("Manager")){
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
                        }else if(!uc.returnWorkgroup(ReceivedSap.this).equals("Manager")){
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

        lblSapNumber = findViewById(R.id.lblSapNumber);
        lblFromBranch = findViewById(R.id.lblFromBranch);
        txtSearch = findViewById(R.id.txtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnDone = findViewById(R.id.btnDone);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sapNumber = txtSearch.getText().toString();
                if(myDb3.countItems().equals(0)){
                    loadSAPNumber(sapNumber);
                }else{
                    loadItems();
                }
            }
        });

        ScrollView scrollView = findViewById(R.id.scroll);
        LinearLayout.LayoutParams layoutParamsScroll;
        if(myDb3.countItems().equals(0)){
            layoutParamsScroll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 700);
            loadSAPNumber("");

            lblSapNumber.setVisibility(View.GONE);
            lblFromBranch.setVisibility(View.GONE);
            txtSearch.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            btnDone.setVisibility(View.GONE);
            lblSelectedType.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        }else{
            layoutParamsScroll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
            lblSapNumber.setVisibility(View.VISIBLE);
            lblFromBranch.setVisibility(View.VISIBLE);
            txtSearch.setVisibility(View.GONE);
            btnSearch.setVisibility(View.GONE);
            btnDone.setVisibility(View.VISIBLE);
            lblSelectedType.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);


            loadItems();
        }
        scrollView.setLayoutParams(layoutParamsScroll);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                navigateDone();
                loadItems();
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
        int totalPendingSAP = recsap.returnPendingSAPNotif(ReceivedSap.this, "");
        nav_shoppingCart.setTitle("Shopping Cart (" + totalCart + ")");
        nav_ReceivedSAP.setTitle("List Items (" + totalPendingSAP + ")");
    }

    public void navigateDone(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReceivedSap.this);
        builder.setMessage("Are you sure want to proceed?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        intent = new Intent(getBaseContext(), ReceivedSap2.class);
                        startActivity(intent);

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

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void loadSAPNumber(final String sapNumber) {
        GridLayout gridLayout = findViewById(R.id.grid);
        gridLayout.removeAllViews();
        try {
            con = cc.connectionClass(ReceivedSap.this);
            if (con == null) {
                Toast.makeText(this, "loadData() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                List<String> results;
                boolean isSAPIT = spinner.getSelectedItemPosition() == 0;
                results = recsap.returnSAPNumber(ReceivedSap.this, sapNumber, isSAPIT);
                for (String result : results){
                    CardView cardView = new CardView(ReceivedSap.this);
                    LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(190, 200);
                    layoutParamsCv.setMargins(20, 10, 10, 10);
                    cardView.setLayoutParams(layoutParamsCv);
                    cardView.setRadius(12);
                    cardView.setCardElevation(5);

                    cardView.setVisibility(View.VISIBLE);
                    gridLayout.addView(cardView);
                    final LinearLayout linearLayout = new LinearLayout(ReceivedSap.this);
                    LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
                    linearLayout.setLayoutParams(layoutParamsLinear);
                    linearLayout.setTag(result);

                    final String finalItem = result;
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder myDialog = new AlertDialog.Builder(ReceivedSap.this);
                            myDialog.setCancelable(false);
                            myDialog.setTitle("Confirmation");
                            myDialog.setMessage("Are you sure you want to select '" +  finalItem + "'?");

                            myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    int countError = insertSAPItems(finalItem);
                                    if(countError <= 0){
                                        Toast.makeText(ReceivedSap.this, "'" + finalItem + "' added", Toast.LENGTH_SHORT).show();
                                        startActivity(getIntent());
                                        finish();
                                    }else{
                                        Toast.makeText(ReceivedSap.this, "'" + finalItem + "' not added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            myDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            myDialog.show();
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

                    TextView txtItemName = new TextView(ReceivedSap.this);
                    String cutWord = cutWord(result,25);
                    txtItemName.setText(cutWord);
                    txtItemName.setLayoutParams(layoutParams);
                    txtItemName.setTextSize(13);
                    txtItemName.setVisibility(View.VISIBLE);
                    linearLayout.addView(txtItemName);
                }
                txtSearch.setAdapter(fillItems(results));
            }
        }catch (Exception ex){
            Toast.makeText(ReceivedSap.this, "loadData() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayAdapter<String> fillItems(List<String> names){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, names);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    public void loadItems(){
        GridLayout gridLayout = findViewById(R.id.grid);
        gridLayout.removeAllViews();
        Cursor cursor = myDb3.getAllData();
        if(cursor != null){
            while (cursor.moveToNext()){
                final int id = cursor.getInt(0);
                final String sapNumber = cursor.getString(1);
                final String fromBranch = cursor.getString(2);
                final String itemName = cursor.getString(3);
                final Double quantity = cursor.getDouble(4);
                final boolean isSelected = (cursor.getInt(6) > 0);
                System.out.println("isSelected: " + isSelected);
                lblSapNumber.setText("IT#: " + sapNumber);
                lblFromBranch.setText("Branch: " + fromBranch);

                CardView cardView = new CardView(ReceivedSap.this);
                LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(190, 200);
                layoutParamsCv.setMargins(20, 10, 10, 10);
                cardView.setLayoutParams(layoutParamsCv);
                cardView.setRadius(12);
                cardView.setCardElevation(5);

                cardView.setVisibility(View.VISIBLE);
                gridLayout.addView(cardView);
                final LinearLayout linearLayout = new LinearLayout(ReceivedSap.this);
                LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
                linearLayout.setLayoutParams(layoutParamsLinear);
                linearLayout.setTag(id);

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isSelected){
                            Toast.makeText(ReceivedSap.this, "This item is selected",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent;
                            intent = new Intent(getBaseContext(), ItemInfo.class);
                            intent.putExtra("title", "Received from SAP");
                            intent.putExtra("itemname", itemName);
                            intent.putExtra("sapNumber", sapNumber);
                            intent.putExtra("quantity", Double.toString(quantity));
                            intent.putExtra("fromBranch", fromBranch);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            loadItems();
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

                TextView txtItemName = new TextView(ReceivedSap.this);
                String cutWord = cutWord(itemName, 25);
                txtItemName.setText(cutWord);
                txtItemName.setLayoutParams(layoutParams);
                txtItemName.setTextSize(13);
                txtItemName.setVisibility(View.VISIBLE);

                TextView txtItemLeft = new TextView(ReceivedSap.this);
                txtItemLeft.setLayoutParams(layoutParamsItemLeft);
                txtItemLeft.setTextSize(10);
                txtItemLeft.setVisibility(View.VISIBLE);
                txtItemLeft.setText(df.format(quantity) + " qty.");
                txtItemLeft.setTextColor(Color.parseColor("#34A853"));

                if(isSelected){
                    linearLayout.setBackgroundColor(Color.RED);
                    txtItemName.setTextColor(Color.WHITE);
                    txtItemLeft.setTextColor(Color.WHITE);
                }else{
                    linearLayout.setBackgroundColor(Color.WHITE);
                    txtItemName.setTextColor(Color.BLACK);
                    txtItemLeft.setTextColor(Color.parseColor("#34A853"));
                }
                cardView.addView(linearLayout);
                linearLayout.addView(txtItemName);
                linearLayout.addView(txtItemLeft);
            }
        }
    }

    public int insertSAPItems(String sapNumber){
        List<String> results;
        boolean isSAPIT = spinner.getSelectedItemPosition() == 0;
        results = recsap.returnData(ReceivedSap.this, sapNumber, isSAPIT);
        int countError = 0;
        for (String result : results){
            String sap_number = "";
            String fromBranch = "";
            String itemName = "";
            double quantity = 0.00;
            String[] words = result.split(",");
            for (int i  = 0; i < words.length; i ++){
                if(i == 0){
                    sap_number = words[i];
                }else if(i == 1) {
                    fromBranch = words[i];
                }else if(i == 2){
                    itemName = words[i];
                }else if(i == 3){
                    quantity = Double.parseDouble(words[i]);
                }
            }
            boolean isSuccess = myDb3.insertData(sap_number, fromBranch, itemName,quantity,0);
            if(!isSuccess){
                countError += 1;
            }
        }
       return countError;
    }

    public String cutWord(String value, int limit){
        String result;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}