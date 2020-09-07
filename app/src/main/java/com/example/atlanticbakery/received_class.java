package com.example.atlanticbakery;

import android.app.Activity;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class received_class {
    connection_class cc = new connection_class();
    Connection con;

    public List<String> returnBranchSupplier(Activity activity, String type){
        List<String> discounts = new ArrayList<>();
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();

            } else {
                discounts.add("Select " + type);
                String supplierQuery = "SELECT name [result] FROM tblcustomers WHERE type='Supplier' AND status=1";
                String branchQuery = "SELECT branchcode [result] FROM tblbranch WHERE main = 0 AND status=1 ORDER BY branchcode";
                String resultQuery = (type.equals("Branch") ? branchQuery : supplierQuery);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(resultQuery);
                while (rs.next()){
                    discounts.add(rs.getString("result"));
                }
                con.close();
            }
        }catch (Exception ex){
            Toast.makeText(activity, "returnBranchSupplier() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return discounts;
    }
}
