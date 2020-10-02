package com.example.atlanticbakery;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static android.content.Context.MODE_PRIVATE;
public class shoppingCart_class {
    Connection con;
    connection_class cc = new connection_class();

    public boolean checkCustomer(Activity activity, String name, String tenderType){
        boolean result = false;
        String type = "";
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                if(tenderType.equals("A.R Sales")){
                    type = "Customer";
                } else if (tenderType.equals("A.R Charge")) {
                    type = "Employee";
                }
                String query2 = "SELECT customer_id FROM tblcustomers WHERE name='" + name + "' AND type='" + type + "';";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);
                result = rs2.next();
                con.close();
            }
        }catch (Exception ex){
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public List<String> returnCustomer(Activity activity, String type){
        final List<String>  result = new ArrayList<>();
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query2 = "SELECT name FROM tblcustomers WHERE type='" + type + "';";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);
                while (rs2.next()){
                    result.add(rs2.getString("name"));
                }
                con.close();
            }
        }catch (Exception ignored){

        }
        return  result;
    }

    public List<String> returnDiscounts(Activity activity){
        List<String> discounts = new ArrayList<>();
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();

            } else {
                discounts.add("Select Discount Type");
                String query = "SELECT disname FROM tbldiscount WHERE status=1;";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()){
                    discounts.add(rs.getString("disname"));
                }
                con.close();
            }
        }catch (Exception ex){
            Toast.makeText(activity, "returnDiscounts() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return discounts;
    }
    public Integer getOrderNumber(Activity activity){
        int result = 0;
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "Select ISNULL(MAX(ordernum),0) + 1 [ordernum] from tbltransaction2 WHERE area='Sales' AND CAST(datecreated AS date)=(select cast(getdate() as date));";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = Integer.parseInt(rs.getString("ordernum"));
                }
                con.close();
            }
        }
        catch (Exception ex) {
            Toast.makeText(activity, "getOrderNumber() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public Double getDiscountPercent(Activity activity, String value){
        double result = 0.00;
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT amount FROM tbldiscount WHERE disname='" + value + "';";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = Double.parseDouble(rs.getString("amount"));
                }
                con.close();
            }
        }
        catch (Exception ex){
            Toast.makeText(activity, "getDiscountPercent() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
