package com.example.atlanticbakery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2  extends AppCompatActivity {
    Button btnLoginAnotherAccount, btnUser;

    DatabaseHelper myDb;
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DatabaseHelper4 myDb4;
    DatabaseHelper5 myDb5;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        btnLoginAnotherAccount = findViewById(R.id.btnLoginAnotherAccount);
        btnUser = findViewById(R.id.btnUser);

        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);
        myDb3 = new DatabaseHelper3(this);
        myDb4 = new DatabaseHelper4(this);
        myDb5 = new DatabaseHelper5(this);

        btnLoginAnotherAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "You logged in successfully!", Toast.LENGTH_SHORT).show();
                saveToken("eyJhbGciOiJIUzUxMiIsImlhdCI6MTYwNjAyNzIwOCwiZXhwIjoxNjA2MjAwMDA4fQ.eyJ1c2VyX2lkIjoyfQ.oXpK351HMW72Dc-cm321cv2Ipb72UDAz_BJEPmI4wsNlMwrKRC9W0llqp2bMHEIN6EFwERi0oLz3ITV1yWa7hg");
                saveLoggedIn();
                openAPIMainMenu();
                myDb.truncateTable();
                myDb2.truncateTable();
                myDb3.truncateTable();
                myDb4.truncateTable();
                myDb5.truncateTable();
            }
        });
    }


    public  void saveLoggedIn(){
        String susername = "gord";
        String spassword = "qwe123";
        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",susername).apply();
        editor.putString("password",spassword).apply();
        editor.putString("fullname","Gordon Macaraeg").apply();
        editor.putString("userid","2").apply();
        editor.putString("whse","A1 S-FG").apply();
        editor.putString("isManager","1").apply();
//        uc.insetLoginLogs(MainActivity.this, susername);
//        uc.checkCutOff(MainActivity.this, susername);
    }

    public void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token).apply();
    }

    public  void openAPIMainMenu(){
        Intent intent = new Intent(this, API_Nav.class);
        startActivity(intent);
        finish();
    }
}
