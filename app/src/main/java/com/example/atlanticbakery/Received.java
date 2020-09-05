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
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.Objects;

public class Received extends AppCompatActivity {
    DatabaseHelper db = new DatabaseHelper(this);
    DatabaseHelper2 myDb;
    ui_class uic = new ui_class();
    prefs_class pc = new prefs_class();

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

    @SuppressLint("SetTextI18n")
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
                itemname.setText(result.getString(1) + "\n" + result.getDouble(2) + " pcs");
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
            btnProceed.setTag("Pay");
            btnProceed.setLayoutParams(layoutParamsPay);
            btnProceed.setTextSize(20);
            btnProceed.setText("PROCEED");
            btnProceed.setBackgroundResource(R.color.colorPrimary);
            btnProceed.setTextColor(Color.WHITE);

            btnProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toastMsg("HELLO WORLD", 0);
                }
            });

            layoutPay.addView(btnProceed);
            layoutPay.setOrientation(LinearLayout.VERTICAL);
            layoutPay.setPadding(20, 20, 20, 20);
            layout.addView(layoutPay);
        }
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