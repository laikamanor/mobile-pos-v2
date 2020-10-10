package com.example.atlanticbakery;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import net.sourceforge.jtds.jdbc.DateTime;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class access_class {
    Connection con;
    connection_class cc = new connection_class();

    public String getServerDate(Activity activity){
        String result = "";
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "loadData() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT CAST(GETDATE() AS date) [date]";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = rs.getString("date");
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity,"getServerDate() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public boolean checkCutOff(Activity activity){
        boolean result = false;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "checkCutOff() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String serverDate = getServerDate(activity);
                String dateFrom = "";
                String status = "";
                String query = "SELECT TOP 1 CAST(a.date AS date) [date],a.status FROM tblcutoff a INNER JOIN tblusers b ON a.userid = b.systemid WHERE CAST(a.date AS date)=(SELECT TOP 1 CAST(datecreated AS date) FROM tblinvsum WHERE verify=0 AND b.workgroup NOT IN ('Administrator','LC Accounting') ORDER BY invsumid ASC)";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    dateFrom = rs.getString("date");
                    status = rs.getString("status");
                }
                if(status.equals("In Active") && dateFrom.equals(serverDate)){
                    result = true;
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "checkCutOff() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean isUserAllowed(Activity activity, String type, int userID){
        boolean result = false;
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "isUserAllowed() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT COUNT(id) [count] FROM tblaccesss WHERE userid=" + userID + " AND moduleid=(SELECT id FROM tblmodules WHERE name='" + type + "') AND status=1;";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    if(rs.getInt("count") > 0){
                        result = true;
                    }
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "isUserAllowed() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
