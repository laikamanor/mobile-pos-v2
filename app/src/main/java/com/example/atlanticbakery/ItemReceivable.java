package com.example.atlanticbakery;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Objects;

public class ItemReceivable extends AppCompatActivity {
    DatabaseHelper2 myDb2;
    String title;
    long mLastClickTime = 0;
    Button btnProceed;
    DecimalFormat df = new DecimalFormat("#,###");
    SharedPreferences sharedPreferences;

    Connection con;
    connection_class cc = new connection_class();
    String transactionNumber;
    TextView lblTransactionNumber,lblStatus;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDb2 = new DatabaseHelper2(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_receivable);
        btnProceed = findViewById(R.id.btnProceed);
        lblStatus = findViewById(R.id.lblStatus);
        lblTransactionNumber = findViewById(R.id.lblTransactionNumber);
        sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        title = getIntent().getStringExtra("title");
        transactionNumber = getIntent().getStringExtra("transaction_number");
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));
        loadItems(transactionNumber);
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadItems(String transNumber) {
        TableRow tableColumn = new TableRow(getBaseContext());
        final TableLayout tableLayout = findViewById(R.id.table_main);
        tableLayout.removeAllViews();
        String[] columns = {"Item", "Qty."};

        for (String s : columns) {
            TextView lblColumn1 = new TextView(getBaseContext());
            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn1.setText(s);
            lblColumn1.setPadding(10, 0, 10, 0);
            tableColumn.addView(lblColumn1);
        }
        tableLayout.addView(tableColumn);
        try {
            con = cc.connectionClass(this);
            if (con == null) {
                Toast.makeText(this, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                int userID = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("userid", "")));
                String query = "SELECT a.transaction_number,a.status, a.item_name [item], SUM(a.quantity)[quantity] FROM tblproduction a WHERE inv_id=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND a.status='Pending' AND a.type2 IN ('Transfer to Sales','Transfer from Sales') AND a.transfer_from=(SELECT username FROM tblusers WHERE systemid=" + userID + ") AND a.transaction_number='" + transNumber + "' GROUP BY a.item_name,a.transaction_number,a.status";
                System.out.println(query);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    lblTransactionNumber.setText("Reference #: " + rs.getString("transaction_number"));
                    lblStatus.setText("Status: " + rs.getString("status"));
                    final TableRow tableRow = new TableRow(getBaseContext());
                    String itemName = rs.getString("item");
                    String v = cutWord(itemName);
                    double quantity = rs.getDouble("quantity");

                    TextView lblColumn1 = new TextView(getBaseContext());
                    lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn1.setText(v);
                    lblColumn1.setPadding(10, 0, 10, 0);
                    tableRow.addView(lblColumn1);

                    TextView lblColumn2 = new TextView(getBaseContext());
                    lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblColumn2.setText(df.format(quantity));
                    lblColumn2.setPadding(10, 10, 10, 10);
                    tableRow.addView(lblColumn2);

                    tableLayout.addView(tableRow);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String cutWord(String value){
        String result;
        int limit = 15;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}