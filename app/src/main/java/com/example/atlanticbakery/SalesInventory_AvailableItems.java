package com.example.atlanticbakery;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static java.lang.Thread.sleep;


public class SalesInventory_AvailableItems extends AppCompatActivity {
    Button btn;
    TextView txt;
    ProgressBar progressBar;
    Connection con;
    connection_class cc = new connection_class();
    int i;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_inventory__available_items);
        btn = findViewById(R.id.btnsa);
        txt = findViewById(R.id.txt);
        progressBar = findViewById(R.id.prog);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            con = cc.connectionClass(SalesInventory_AvailableItems.this);
                            if (con == null) {
                                System.out.println("No internet Access");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                String query = "SELECT DocNum FROM vSAP_IT";
                                Statement stmt2 = con.createStatement();
                                ResultSet rs2 = stmt2.executeQuery(query);
                                while (rs2.next()) {
                                    try {
                                        System.out.println(rs2.getString("DocNum"));
                                        sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        }catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                });
                thread.start();
            }
        });

    }
}