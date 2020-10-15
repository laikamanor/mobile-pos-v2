package com.example.atlanticbakery;

import android.app.Activity;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class receivedsap_class {
    Connection con;
    connection_class cc = new connection_class();

    public List<String> returnSAPNumber(Activity activity, String sapNumber, boolean isSAPIT){
        List<String> results = new ArrayList<>();
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String vName = (isSAPIT) ? "vSAP_IT" : "vSAP_PO";
                String resultQuery = "SELECT docNum [result],Dscription,Quantity FROM " + vName + " WHERE CAST(DocDate AS date)>='10/11/2020' AND docNum LIKE '%" + sapNumber + "%' ORDER BY docNum";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(resultQuery);
                while (rs.next()){
                    String resultQuery2 = "SELECT transaction_id FROM tblproduction WHERE CAST(date AS date)>='10/11/2020' AND sap_number='" + rs.getString("result") + "' AND item_name='" + rs.getString("Dscription") + "' AND sap_number !='To Follow' AND status='Completed'";
                    Statement stmt2 = con.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(resultQuery2);
                    if(!rs2.next()){
                        String selectedSAP = rs.getString("result");
                        if(!results.contains(selectedSAP)){
                            results.add(selectedSAP);
                        }
                    }
                }
                con.close();
            }
        }catch (Exception ex){
            Toast.makeText(activity, "returnSAPNumber() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return results;
    }

    public List<String> returnData(Activity activity, String sapNumber, boolean isSAPIT){
        List<String> results = new ArrayList<>();
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String vName = (isSAPIT) ? "vSAP_IT" : "vSAP_PO";
                String fromWhat = (isSAPIT) ? "FromWhsCod" : "CardName";
                String resultQuery = "SELECT DISTINCT DocNum [sap_number]," + fromWhat + " [fromWhat],Dscription [item_name], Quantity [quantity]  FROM " + vName + " WHERE CAST(DocDate AS date)>='10/11/2020' AND docNum LIKE '%" + sapNumber + "%'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(resultQuery);
                while (rs.next()){
                    String resultQuery2 = "SELECT transaction_id FROM tblproduction WHERE CAST(date AS date)>='10/11/2020' AND sap_number='" + sapNumber + "' AND item_name='" + rs.getString("item_name") + "' AND sap_number !='To Follow' AND status='Completed'";
                    Statement stmt2 = con.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(resultQuery2);
                    if(!rs2.next()){
                        String selectedSAP = rs.getString("sap_number") + "," + rs.getString("fromWhat") + "," + rs.getString("item_name") + "," + rs.getDouble("quantity");
                        results.add(selectedSAP);
                    }
                }
                con.close();
            }
        }catch (Exception ex){
            Toast.makeText(activity, "returnData() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return results;
    }

//    public boolean isItemToFollow(Activity activity, String sapNumber){
//        boolean result = false;
//        int count_fromSAP = 0;
//        int count_fromPOS = 0;
//        try{
//            con = cc.connectionClass(activity);
//            if (con == null) {
//                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
//
//            } else {
//                String resultQuery = "SELECT DISTINCT DocNum [sap_number],FromWhsCod [fromWhat],Dscription [item_name], Quantity [quantity]  FROM [192.168.30.6].[AKPOS].[dbo].[vSAP_IT] WHERE CAST(DocDate AS date)=(select cast(getdate() as date)) AND docNum LIKE '%" + sapNumber + "%'";
//                Statement stmt = con.createStatement();
//                ResultSet rs = stmt.executeQuery(resultQuery);
//                while (rs.next()){
//                    count_fromSAP += 1;
//                    String resultQuery2 = "SELECT transaction_id FROM tblproduction WHERE CAST(date AS date)=(select cast(getdate() as date)) AND sap_number='" + sapNumber + "' AND item_name='" + rs.getString("item_name") + "' AND sap_number !='To Follow' AND status='Completed'";
//                    Statement stmt2 = con.createStatement();
//                    ResultSet rs2 = stmt2.executeQuery(resultQuery2);
//                    if(!rs2.next()){
//                        count_fromPOS += 1;
//                    }
//                }
//                con.close();
//            }
//        }catch (Exception ex){
//            Toast.makeText(activity, "returnData() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        result = (count_fromSAP == count_fromPOS) ? false : true;
//        return result;
//    }

    public Integer returnPendingSAPNotif(Activity activity, String sapNumber){
        int result = 0;
        List<String> results = new ArrayList<>();
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String resultQuery = "SELECT docNum [result],Dscription,Quantity FROM vSAP_IT WHERE CAST(DocDate AS date)>='10/11/2020' AND docNum LIKE '%" + sapNumber + "%' ORDER BY docNum";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(resultQuery);
                while (rs.next()){
                    String resultQuery2 = "SELECT transaction_id FROM tblproduction WHERE CAST(date AS date)>='10/11/2020' AND sap_number='" + rs.getString("result") + "' AND item_name='" + rs.getString("Dscription") + "' AND sap_number !='To Follow' AND status='Completed'";
                    Statement stmt2 = con.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(resultQuery2);
                    if(!rs2.next()){
                        String selectedSAP = rs.getString("result");
                        if(!results.contains(selectedSAP)){
                            results.add(selectedSAP);
                            result += 1;
                        }
                    }
                }
                con.close();
            }
        }catch (Exception ex){
            Toast.makeText(activity, "returnSAPNumber() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
