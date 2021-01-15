package com.example.atlanticbakery;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Users extends AppCompatActivity {
    public static final String URL_WS = "http://192.168.11.8:5000/api/user/get_all_users";
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mQueue = Volley.newRequestQueue(this);
        loadUsers();
    }

    public void loadUsers() {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_WS, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    TableRow tableColumn = new TableRow(getBaseContext());
                    final TableLayout tableLayout = findViewById(R.id.table_main);
                    tableLayout.removeAllViews();
                    String[] columns = {"Username", "Full Name", "Branch", "Action"};

                    for (String s : columns) {
                        TextView lblColumn1 = new TextView(getBaseContext());
                        lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn1.setText(s);
                        lblColumn1.setPadding(10, 0, 10, 0);
                        tableColumn.addView(lblColumn1);
                    }
                    tableLayout.addView(tableColumn);

                    JSONArray getArray = response.getJSONArray("data");
                    for (int i = 0; i < getArray.length(); i++) {
                        JSONObject objects = getArray.getJSONObject(i);
                        for (int a = 0; a < objects.names().length(); a++) {
                            final TableRow tableRow = new TableRow(getBaseContext());
//                                    TextView textView = new TextView(getBaseContext());
//                                    textView.setTextSize(15);
//                                    textView.setText("KEY: " + objects.names().getString(a) + "\n VALUE: " + objects.get(objects.names().getString(a)) + "\n");
//                            System.out.println("KEY: " + objects.names().getString(a) + "\n VALUE: " + objects.get(objects.names().getString(a)) + "\n");
//                            String userName = "";
//                            String column = objects.names().getString(a) + "\n";
//                            if (column.equals("username" + "\n")) {
//                                userName = objects.get(objects.names().getString(a)).toString();
//                                TextView lblColumn1 = new TextView(getBaseContext());
//                                lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                                lblColumn1.setText(objects.get(objects.names().getString(a)).toString());
//                                lblColumn1.setPadding(10, 0, 10, 0);
//                                tableRow.addView(lblColumn1);
//                                tableLayout.addView(tableRow);
//                            } else if (objects.names().getString(a) == "fullname" + "\n")  {
//                                TextView lblColumn2 = new TextView(getBaseContext());
//                                lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                                lblColumn2.setText(objects.get(objects.names().getString(a)).toString());
//                                lblColumn2.setPadding(10, 0, 10, 0);
//                                tableRow.addView(lblColumn2);
//                            } else if (objects.names().getString(a) == "branch" + "\n") {
//                                TextView lblColumn3 = new TextView(getBaseContext());
//                                lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                                lblColumn3.setText(objects.get(objects.names().getString(a)).toString());
//                                lblColumn3.setPadding(10, 0, 10, 0);
//                                tableRow.addView(lblColumn3);
//                            } else {
//                                TextView lblColumn4 = new TextView(getBaseContext());
//                                lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                                lblColumn4.setTag(userName);
//                                lblColumn4.setText("View");
//                                lblColumn4.setPadding(10, 10, 10, 10);
//                                lblColumn4.setTextColor(Color.GREEN);
//                                tableRow.addView(lblColumn4);
//                                tableLayout.addView(tableRow);
//                                continue;
//                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiIsImlhdCI6MTYwMjExODAzNiwiZXhwIjoxNjAyMTM5NjM2fQ.eyJ1c2VyX2lkIjoyfQ.63AAKa_GCpY1u41zP0NZof-nHVX46HeU_1dPx4f8qV43Oo4CDamwpfOe4bR83ZcHmaZaJlwDbGB5yTjQdow51g");
                return params;
            }
        };
        mQueue.add(request);
    }
}