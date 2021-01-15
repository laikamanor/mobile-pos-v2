package com.example.atlanticbakery;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    DatabaseHelper myDb = new DatabaseHelper(this);
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DatabaseHelper4 myDb4;
    DatabaseHelper5 myDb5;
    DatabaseHelper9 myDb9;
    prefs_class pc = new prefs_class();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        myDb2 = new DatabaseHelper2(this);
        myDb3 = new DatabaseHelper3(this);
        myDb4 = new DatabaseHelper4(this);
        myDb5 = new DatabaseHelper5(this);
        myDb9 = new DatabaseHelper9(this);
        Objects.requireNonNull(getSupportActionBar()).hide();

        TextView txtTitle = findViewById(R.id.textView9);
        txtTitle.setText(getString(R.string.app_name).toUpperCase());

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                goTO();
            }
        };
        handler.postDelayed(r, 3000);
    }

    public void goTO(){
        myDb.truncateTable();
        myDb2.truncateTable();
        myDb3.truncateTable();
        myDb4.truncateTable();
        myDb5.truncateTable();
        myDb9.truncateTable();
        pc.loggedOut(SplashScreen.this);
        Intent intent = new Intent(this, IPAddress.class);
        startActivity(intent);
        finish();
    }

}