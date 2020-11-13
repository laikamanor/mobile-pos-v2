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

public class actualendbal_class {
    Connection con;
    DatabaseHelper4 myDb4;
    connection_class cc = new connection_class();
    received_class rec = new received_class();

    public boolean hasFinalCount(Activity activity, String type) {
        boolean result;
        int result_int = 0;
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "loadActualEndBal() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String s = (type.equals("PO Final Count")) ? "PO " : "";
                String query = "SELECT itemname [sItemName],quantity [sQuantity] FROM tblactualendbal WHERE type='" + s + "Store Count' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1 ORDER BY itemname ASC;";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String query2 = "SELECT itemname [aItemName],quantity [aQuantity] FROM tblactualendbal WHERE type='" + s + "Auditor Count' AND itemname='" + rs.getString("sItemName") + "' AND status=1 AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) ORDER BY itemname ASC;";
                    Statement stmt2 = con.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(query2);
                    if (rs2.next()) {
                        double sQuantity = rs.getDouble("sQuantity");
                        double aQuantity = rs2.getDouble("aQuantity");
                        double variance = sQuantity - aQuantity;
                        if (sQuantity != aQuantity) {
                            result_int+= 1;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(activity, "hasFinalCount() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        result = result_int > 0;
        return result;
    }

    public List<String> loadActualEndBal(Activity activity, String itemname,String type) {
        List<String> results = new ArrayList<>();
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "loadActualEndBal() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                if (type.equals("Final Count") || type.equals("PO Final Count")) {
                    String s = (type.equals("PO Final Count")) ? "PO " : "";
                    String query = "SELECT itemname [sItemName],quantity [sQuantity] FROM tblactualendbal WHERE type='" + s + "Store Count' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND itemname LIKE '%" + itemname + "%' AND status=1 ORDER BY itemname ASC;";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        String query2 = "SELECT itemname [aItemName],quantity [aQuantity] FROM tblactualendbal WHERE type='" + s + "Auditor Count' AND itemname='" + rs.getString("sItemName") + "' AND status=1 AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) ORDER BY itemname ASC;";
                        Statement stmt2 = con.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(query2);
                        if (rs2.next()) {
                            double sQuantity = rs.getDouble("sQuantity");
                            double aQuantity = rs2.getDouble("aQuantity");
                            double variance = sQuantity - aQuantity;
                            if (sQuantity != aQuantity) {
                                String itemName = rs2.getString("aItemName");
                                String concatString = itemName + "," + variance + "," + 1;
                                results.add(concatString);
                            }
                        }
                    }
                } else if (type.equals("PO Auditor Count") || type.equals("PO Store Count")) {
                    String query = "SELECT itemname,endbal [quantity],totalav  FROM tblinvitems WHERE invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND status=1 AND itemname LIKE '%" + itemname + "%' ORDER BY endbal DESC, itemname ASC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        int nagalaw = 0;
                        if (rs.getDouble("totalav") > 0) {
                            nagalaw = 1;
                        }
                        String concatString = rs.getString("itemname") + "," + rs.getString("quantity") + "," + nagalaw;
                        results.add(concatString);
                    }
                } else {
                    String query = "SELECT a.itemname [result],CASE WHEN x.transaction_id IS NULL THEN a.endbal END [quantity], a.totalav FROM tblinvitems a INNER JOIN tblitems b ON a.itemname = b.itemname OUTER APPLY(SELECT c.transaction_id FROM tblproduction c WHERE c.inv_id=(Select TOP 1 invnum from tblinvsum ORDER BY invsumid DESC) AND c.type='Actual Ending Balance' AND c.item_name= a.itemname AND c.status='Completed')x WHERE a.invnum=(Select TOP 1 invnum from tblinvsum ORDER BY invsumid DESC) AND a.itemname LIKE '%" + itemname + "%' ORDER BY a.totalav DESC,a.itemname ASC;";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        if (rs.getString("quantity") != null) {
                            if (rs.getDouble("totalav") > 0) {
                                String concatString = rs.getString("result") + "," + rs.getString("quantity") + "," + 1;
                                results.add(concatString);
                            }
                        } else {
                            if (rs.getDouble("totalav") <= 0) {
                                String concatString = rs.getString("result") + "," + rs.getString("quantity") + "," + 0;
                                results.add(concatString);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(activity, "loadActualEndBal() " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return results;
    }

    public List<String> loadUpdateActualEndingBalance(Activity activity){
        List<String> results = new ArrayList<>();
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "loadActualEndBal() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT itemname [sItemName],quantity [sQuantity] FROM tblactualendbal WHERE type='Store Count' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1 ORDER BY itemname ASC;";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    String query2 = "SELECT itemname [aItemName],quantity [aQuantity] FROM tblactualendbal WHERE type='Auditor Count' AND itemname='" + rs.getString("sItemName") + "' AND status=1 AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) ORDER BY itemname ASC;";
                    Statement stmt2 = con.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(query2);
                    if (rs2.next()) {
                        double sQuantity = rs.getDouble("sQuantity");
                        double aQuantity = rs2.getDouble("aQuantity");
                        String itemName = rs.getString("sItemName");
                        if(sQuantity == aQuantity){
                            String concatResult = itemName + "," + aQuantity + "," + loadEndbal(activity, itemName) + "," + loadPullOut(activity, itemName) + "," + loadPrice(activity, itemName);
                            results.add(concatResult);
                        }else{
                            String query3 = "SELECT itemname [aItemName],quantity [fQuantity] FROM tblactualendbal WHERE type='Final Count' AND itemname='" + rs2.getString("aItemName") + "' AND status=1 AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) ORDER BY itemname ASC;";
                            Statement stmt3 = con.createStatement();
                            ResultSet rs3 = stmt3.executeQuery(query3);
                            if (rs3.next()) {
                                double fQuantity = rs3.getDouble("fQuantity");
                                String concatResult = itemName + "," + fQuantity + "," + loadEndbal(activity, itemName) + "," + loadPullOut(activity, itemName) + "," + loadPrice(activity, itemName);
                                results.add(concatResult);
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "loadUpdateActualEndingBalance() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  results;
    }

    public Double loadPullOut(Activity activity, String item){
        double result = 0.00;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                    Toast.makeText(activity, "loadActualEndBal() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT itemname [sItemName],quantity [sQuantity] FROM tblactualendbal WHERE type='PO Store Count' AND itemname='" + item + "' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1 ORDER BY itemname ASC;";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    String query2 = "SELECT itemname [aItemName],quantity [aQuantity] FROM tblactualendbal WHERE type='PO Auditor Count' AND itemname='" + rs.getString("sItemName") + "' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1 ORDER BY itemname ASC;";
                    Statement stmt2 = con.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(query2);
                    if (rs2.next()) {
                        double sQuantity = rs.getDouble("sQuantity");
                        double aQuantity = rs2.getDouble("aQuantity");
                        if(sQuantity == aQuantity){
                            result = aQuantity;
                        }else{
                            String query3 = "SELECT itemname [aItemName],quantity [fQuantity] FROM tblactualendbal WHERE type='PO Final Count' AND itemname='" + rs2.getString("aItemName") + "' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1 ORDER BY itemname ASC;";
                            Statement stmt3 = con.createStatement();
                            ResultSet rs3 = stmt3.executeQuery(query3);
                            if (rs3.next()) {
                                result = rs3.getDouble("fQuantity");
                            }
                        }
                    }else{
                        result = 0.00;
                    }
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "loadData() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    public Double loadPrice(Activity activity, String item){
        double result = 0.00;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "loadActualEndBal() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query2 = "SELECT price FROM tblitems WHERE itemname='" + item + "';";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);
                if (rs2.next()) {
                    result = rs2.getDouble("price");
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "loadPrice() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    public Double loadEndbal(Activity activity, String item){
        double result = 0.00;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "loadActualEndBal() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query2 ="SELECT endbal [quantity] FROM tblinvitems WHERE invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND endbal > 0 AND status=1 AND itemname='" + item + "';";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);
                if (rs2.next()) {
                    result = rs2.getDouble("quantity");
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "loadPrice() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean insertActualEnding(Activity activity, String type,int userid,String remarks){
        boolean result;
        int result_int = 0;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                myDb4 = new DatabaseHelper4(activity);
                Cursor cursor = myDb4.getAllData(type);
                if(cursor != null){
                    while (cursor.moveToNext()){
                        String query = "INSERT INTO tblactualendbal (itemname,quantity,type,createdby,datecreated,status,remarks) VALUES ('" + cursor.getString(1) + "'," + cursor.getDouble(2) + ",'" + type + "',(SELECT username FROM tblusers WHERE systemid=" + userid + "),(SELECT GETDATE()),1,'" + remarks + "')";
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);
                    }
                }else {
                    result_int += 1;
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "insertActualEnding() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            result_int += 1;
        }
        result = result_int <= 0;
        if(result){
            myDb4.deleteType(type);
        }
        return  result;
    }

    public boolean isTypeExist(Activity activity,String type){
        boolean result;
        int result_int = 0;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT COUNT(*) [count] FROM tblactualendbal WHERE type='" + type + "' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1;";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query);
                if(rs2.next()){
                    result_int = rs2.getInt("count");
                }
            }
        }catch (Exception ex){
            Toast.makeText( activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        result = result_int > 0;
        return  result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean updatePullOut(Activity activity, List<String> results,Integer userid, String selectedBranch){
        boolean answer;
        int answer_int = 0;
        String transNum = rec.returnTransactionNumber(activity, "Pull Out","pullout2");
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                for (String result : results) {
                    if(!result.equals("")){
                        String itemName = "";
                        double quantity = 0.00;
                        String[] words = result.split(",");
                        for (int i = 0; i < words.length; i++) {
                            if (i == 0) {
                                itemName = words[i];
                            } else if (i == 1) {
                                quantity = Double.parseDouble(words[i]);
                            }
                        }
                        if(quantity > 0){
                            String query = "UPDATE tblinvitems Set pullout2+=" + quantity + ", endbal-=" + quantity + ", variance+=" + quantity + " WHERE itemname='" + itemName + "' And invnum=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) And area='Sales';";
                            Statement stmt = con.createStatement();
                            stmt.executeUpdate(query);

                            String query2 = "INSERT INTO tblproduction (transaction_number,inv_id,item_code,item_name,category,quantity,reject,charge,sap_number,remarks,date,processed_by,type,area,status,transfer_from,transfer_to,typenum,type2) VALUES ('" + transNum + "',(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC),(SELECT itemcode FROM tblitems WHERE itemname='" + itemName + "'),'" + itemName + "',(SELECT category FROM tblitems WHERE itemname='" + itemName + "')," + quantity + ",0,0,'To Follow','',(SELECT GETDATE()),(SELECT username FROM tblusers WHERE systemid=" + userid + "),'Pull Out','Sales','Completed',(SELECT TOP 1 branchcode FROM tblbranch WHERE main=1),'" + selectedBranch + "','ITR','Pull Out');";
                            Statement stmt2 = con.createStatement();
                            stmt2.executeUpdate(query2);
                        }
                    }
                }
            }
        }catch (Exception ex){
            answer_int += 1;
            Toast.makeText(activity, "updatePullOut() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        answer = answer_int <= 0;
        return answer;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean updateActualEndingBalance(Activity activity, List<String> results,Integer userid){
        boolean answer;
        int answer_int = 0;
        String transNum = rec.returnTransactionNumber(activity, "Actual Ending Balance","aeb");
        String ar_num = getArNum(activity, "AR Charge", "CH");
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                double totalAmount = 0.00;
                for (String result : results) {
                    if (!result.equals("")) {
                        String itemName = "";
                        double quantity = 0.00;
                        double endBal = 0.00;
                        double pullOut = 0.00;
                        double charge = 0.00;
                        String[] words = result.split(",");
                        for (int i = 0; i < words.length; i++) {
                            if (i == 0) {
                                itemName = words[i];
                            } else if (i == 1) {
                                quantity = Double.parseDouble(words[i]);
                            } else if (i == 2) {
                                endBal = Double.parseDouble(words[i]);
                            }
                            else if (i == 3) {
                                pullOut = Double.parseDouble(words[i]);
                            }
                        }
                        double as = endBal - pullOut;
                        charge = quantity - as;
                        String query = "Update tblinvitems set endbal-=" + Math.abs(charge) + ", actualendbal=" + quantity + ", variance-=" + Math.abs(charge) + " where itemcode=(SELECT itemcode FROM tblitems WHERE itemname='" + itemName + "') and invnum=(Select TOP 1 invnum from tblinvsum WHERE area='Sales' order by invsumid DESC)";
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);
                        String query2 = "INSERT INTO tblproduction (transaction_number,inv_id,item_code,item_name,category,quantity,charge,date,processed_by,type,area,status,remarks) VALUES ('" + transNum + "',(Select TOP 1 invnum from tblinvsum WHERE area='Sales' order by invsumid DESC),(SELECT itemcode FROM tblitems WHERE itemname='" + itemName + "'),'" + itemName + "',(SELECT category FROM tblitems WHERE itemname='" + itemName + "')," + quantity + "," + Math.abs(charge) + ",(SELECT GETDATE()),(SELECT TOP 1 username FROM tblusers WHERE systemid=" + userid + "),'Actual Ending Balance', 'Sales', 'Completed','');";
                        Statement stmt2 = con.createStatement();
                        stmt2.executeUpdate(query2);
                        if(charge < 0) {
                            double c = Math.abs(charge);
                            totalAmount += chargeFunctionItems(activity,ar_num,itemName,c);
                        }
                        if(totalAmount > 0){
                            boolean isSuccess = chargeFunctionTransaction(activity,totalAmount, ar_num,userid,"Main Inventory");
                            if(isSuccess){
                                answer_int -= 1;
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            answer_int += 1;
            Toast.makeText(activity, "updateActualEndingBalance() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        answer = answer_int <= 0;
        return answer;
    }

    public boolean chargeFunctionTransaction(Activity activity,double totalAmount, String ar_num, int userID, String inventoryType) {
        boolean result = false;
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                if (totalAmount > 0) {
                    String query6 = "Insert into tbltransaction (ornum, transnum, transdate, cashier, tendertype, servicetype, delcharge, subtotal, disctype, less, vatsales, vat, amtdue, gctotal, tenderamt, change, refund, comment, remarks, customer, tinnum, tablenum, pax, datecreated, datemodified, status, area, invnum, partialamt,typenum,sap_number,sap_remarks,typez,discamt,salesname,auth_systemid,inventory_type) values ('0', '" + ar_num + "', (SELECT CAST(GETDATE() AS date)),(SELECT username FROM tblusers WHERE systemid=" + userID + "),'A.R Charge','N/A','0'," + totalAmount + ", 'N/A', '0', '0', '0', " + totalAmount + ", '0', '0', '0', '0', '', '','Short' , 'N/A', '0','1', (SELECT GETDATE()), (SELECT GETDATE()), '1','Sales',(Select TOP 1 invnum from tblinvsum WHERE area='Sales' order by invsumid DESC),'0','AR','To Follow','','Retail',0,(SELECT username FROM tblusers WHERE systemid=" + userID + ")," + userID + ",'" + inventoryType + "')";
                    Statement stmt6 = con.createStatement();
                    stmt6.executeUpdate(query6);

                    String query7 = "Insert into tbltransaction2 (ornum, ordernum, transdate, cashier, tendertype, servicetype, delcharge, subtotal, disctype, less, vatsales, vat, amtdue, gctotal, tenderamt, change, refund, comment, remarks, customer, tinnum, tablenum, pax, datecreated, datemodified, status, area,transnum,status2,createdby,typez,inventory_type) values ('0',(Select ISNULL(MAX(ordernum),0) + 1 from tbltransaction2 WHERE area='Sales' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date))) , (SELECT CAST(GETDATE() AS date)),(SELECT username FROM tblusers WHERE systemid=" + userID + "),'A.R Charge','N/A','0', " + totalAmount + ", 'N/A', '0', '0', '0', " + totalAmount + ", '0', '0', '0', '0', '', '','Short', 'N/A', '0','1', (SELECT GETDATE()), (SELECT GETDATE()), '1','Sales','" + ar_num + "','Paid',(SELECT username FROM tblusers WHERE systemid=" + userID + "),'Retail','" + inventoryType + "')";
                    Statement stmt7 = con.createStatement();
                    stmt7.executeUpdate(query7);

                    String query8 = "INSERT INTO tblars1 (arnum,transnum,amountdue,name,status,date_created,created_by,area,invnum,type,typenum,sap_no,remarks,_from) VALUES ('" + ar_num + "','" + ar_num + "', " + totalAmount + ", 'Short', 'Paid',(SELECT GETDATE()),(SELECT username FROM tblusers WHERE systemid=" + userID + "),'Sales',(Select TOP 1 invnum from tblinvsum WHERE area='Sales' order by invsumid DESC),'AR Charge','AR','To Follow','','Transfer to Main')";
                    Statement stmt8 = con.createStatement();
                    stmt8.executeUpdate(query8);
                    result = true;
                }
            }
        } catch (Exception ex) {
            Toast.makeText(activity, "chargeFunctionTransaction() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public double chargeFunctionItems(Activity activity, String ar_num, String itemName, Double charge){
        double totalAmount = 0.00;
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "chargeFunctionItems() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "Update tblinvitems set archarge+=" + charge + " where itemcode=(SELECT itemcode FROM tblitems WHERE itemname='" + itemName + "') and invnum=(Select TOP 1 invnum from tblinvsum WHERE area='Sales' order by invsumid DESC)";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);


                String query3 = "Insert into tblorder (transnum, category, itemname, qty, price, totalprice, dscnt, free, request, status, discprice, disctrans,area,invnum,type)values('" + ar_num + "', (SELECT category FROM tblitems WHERE itemname='" + itemName + "'),'" + itemName + "'," + charge + ",(SELECT TOP 1 price FROM tblitems WHERE itemname='" + itemName + "'),(SELECT SUM(price * " + charge + ") FROM tblitems WHERE itemname='" + itemName + "'),0,0,0,1,0,0,'Sales',(Select TOP 1 invnum from tblinvsum WHERE area='Sales' order by invsumid DESC),'AR Charge')";
                Statement stmt3 = con.createStatement();
                stmt3.executeUpdate(query3);

                String query4 = "Insert into tblorder2 (ordernum, category, itemname, qty, price, totalprice, dscnt, free, request, status, discprice, disctrans,area,datecreated)values((Select ISNULL(MAX(ordernum),0) + 1 from tbltransaction2 WHERE area='Sales' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date))),(SELECT category FROM tblitems WHERE itemname='" + itemName + "'),'" + itemName + "'," + charge + ",(SELECT TOP 1 price FROM tblitems WHERE itemname='" + itemName + "'),(SELECT SUM(price * " + charge + ") from tblitems WHERE itemname='" + itemName + "'),'0','0','0','1','0','0','Sales',(SELECT GETDATE()))";
                Statement stmt4 = con.createStatement();
                stmt4.executeUpdate(query4);

                String query5 = "INSERT INTO tblars2 (transnum,description,quantity,price,amount,area,name) VALUES ('" + ar_num + "', '" + itemName + "'," + charge + ",(SELECT TOP 1 price FROM tblitems WHERE itemname='" + itemName + "'),(SELECT SUM(price * " + charge + ") from tblitems WHERE itemname='" + itemName + "'),'Sales','Short')";
                Statement stmt5 = con.createStatement();
                stmt5.executeUpdate(query5);
                double v = charge * loadPrice(activity, itemName);
                totalAmount += v;
            }
        }catch (Exception ex) {
            Toast.makeText(activity, "chargeFunctionItems() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return totalAmount;
    }

    public String returnSAQuantity(Activity activity, String isPullOut, String itemName){
        String result = "";
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                double s = 0.00;
                double a = 0.00;
                double v = 0.00;
                String query = "SELECT quantity FROM tblactualendbal WHERE type='" + (isPullOut.equals("") ? "" : "PO ") + "Store Count' AND itemname='" + itemName + "' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1;";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    s = rs.getDouble("quantity");
                    result = "Store Count: " + rs.getDouble("quantity") + "\n";
                }

                String query2 = "SELECT quantity FROM tblactualendbal WHERE type='" + (isPullOut.equals("") ? "" : "PO ") + "Auditor Count' AND itemname='" + itemName + "' AND CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date)) AND status=1;";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);
                if(rs2.next()){
                    a = rs2.getDouble("quantity");
                    result += "Auditor Count: " + rs2.getDouble("quantity") + "\n";
                }
                v = s - a;
                result += "Variance: " + v;
            }
        }catch (Exception ex){
            Toast.makeText(activity, "returnSAQuantity() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    public boolean updateTblActualEndingBalanceStatus(Activity activity){
        boolean answer;
        int answer_int = 0;
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query2 = "UPDATE tblactualendbal SET status=0 WHERE CAST(datecreated AS date)=(SELECT CAST(GETDATE() AS date))";
                Statement stmt2 = con.createStatement();
                stmt2.executeUpdate(query2);
            }
        }catch (Exception ex){
            answer_int += 1;
            Toast.makeText(activity, "updateTblActualEndingBalanceStatus() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        answer = answer_int <= 0;
        return answer;
    }

    public String getArNum(Activity activity, String type, String formatsu){
        String result = "";
        try {
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "insertActualEnding() Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                int prodCount = 0;
                StringBuilder totalZero = new StringBuilder();
                String branchCode = "";

                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT ISNULL(MAX(CAST(REPLACE(transnum,SUBSTRING(transnum,1,20),'') AS INT)),0)+1 [result] FROM tblars1 WHERE name ='Short' AND type='" + type + "';");
                if(resultSet.next()){
                    prodCount = resultSet.getInt("result");
                }

                Statement statement2 = con.createStatement();
                ResultSet resultSet2 = statement2.executeQuery("SELECT branchcode FROM tblbranch WHERE main='1'");
                if(resultSet2.next()){
                    branchCode = resultSet2.getString("branchcode");
                }

                if(prodCount < 1000000){
                    String cselectcount_result = Integer.toString(prodCount);
                    int cselectcount_resultLength = 7 - cselectcount_result.length();
                    while (0 < cselectcount_resultLength){
                        totalZero.append("0");
                        cselectcount_resultLength-= 1;
                    }
                }
                result = "SALAR" + formatsu + " - " + branchCode + " - " + totalZero + prodCount;
            }
        }catch (Exception ex){
            Toast.makeText(activity, "getArNum() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
