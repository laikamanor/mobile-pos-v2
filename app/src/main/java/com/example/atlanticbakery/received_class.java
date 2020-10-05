package com.example.atlanticbakery;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                String mainBranchQuery = "SELECT branchcode [result] FROM tblbranch WHERE main = 1 AND status=1 AND main=1 ORDER BY branchcode";
                String branchQuery = "SELECT branchcode [result] FROM tblbranch WHERE main = 0 AND status=1 ORDER BY branchcode";
                String userQuery = "SELECT username [result] FROM tblusers WHERE status=1 AND workgroup='Sales' ORDER BY username ASC;";
                String resultQuery = "";
                switch (type) {
                    case "Branch":
                        resultQuery = branchQuery;
                        break;
                    case "Supplier":
                        resultQuery = supplierQuery;
                        break;
                    case "Sales Agent":
                        resultQuery = userQuery;
                        break;
                    case "Main Branch":
                        resultQuery = mainBranchQuery;
                        break;
                }
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String returnTransactionNumber(Activity activity, String title, String columnName){
        String result = "";
        String t = title.replace("Manual ", "");
        String type2 = t;
        String type;
        String template = "";
        int prodCount = 0;
        StringBuilder totalZero = new StringBuilder();
        String branchCode = "";
        switch (type2) {
            case "Received from Adjustment":
                type = "Adjustment Item";
                break;
            case "Transfer Out":
            case "Transfer to Sales":
                type = "Transfer Item";
                break;
            case ("Adjustment Out"):
                type = "Adjustment Out Item";
                break;
            case ("Pull Out"):
                type = "Pull Out";
                break;
            case ("Actual Ending Balance"):
                type = "Actual Ending Balance";
                break;
            default:
                type = "Received Item";
                break;
        }

        switch (columnName) {
            case "productionin":
                template = "RECPROD - ";
                break;
            case "itemin":
                template = "RECBRA - ";
                break;
            case "supin":
                template = "RECSUPP - ";
                break;
            case "adjustmentin":
                template = "ADJIN - ";
                break;
            case "pullout":
                template = "ADJOUT - ";
                break;
            case "transfer":
                template = "TRA - ";
                break;
            case "aeb":
                template = "AEB - ";
                break;
            case "pullout2":
                template = "PO - ";
                break;
            case "salesout":
                template = "TRASLS - ";
                break;
            case "salesin":
                template = "RECSLS - ";
                break;
        }
        String queryProd = "Select ISNULL(MAX(transaction_id),0) +1 [counter] from tblproduction WHERE area='Sales' AND type='" + type + "' AND type2='" + type2 + "';";
        String queryBra = "SELECT branchcode FROM tblbranch WHERE main='1'";
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(queryProd);
                if(resultSet.next()){
                    prodCount = resultSet.getInt("counter");
                }

                Statement statement2 = con.createStatement();
                ResultSet resultSet2 = statement2.executeQuery(queryBra);
                if(resultSet2.next()){
                    branchCode = resultSet2.getString("branchcode") + " - ";
                }
                con.close();


                if(prodCount < 1000000){
                    String cselectcount_result = Integer.toString(prodCount);
                    int cselectcount_resultLength = 7 - cselectcount_result.length();
                    while (0 < cselectcount_resultLength){
                        totalZero.append("0");
                        cselectcount_resultLength-= 1;
                    }
                }
                result = template + branchCode + totalZero + prodCount;
            }
        }catch (Exception ex){
            Toast.makeText(activity, "returnTransactionNumber() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public int checkStock(Activity activity, String itemName){
        int result = 0;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT dbo.checkStock('" + itemName + "')";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = rs.getInt(1);
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "checkStock() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    public int checkOwnInventory(Activity activity, String type2,int userID){
        int result = 0;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT COUNT(*) [result] FROM tblproduction WHERE inv_id=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND type2='" + type2 + "' AND transfer_from=(SELECT username FROM tblusers WHERE systemid=" + userID + ")";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = rs.getInt("result");
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "checkOwnInventory() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    public double checkOwnInventoryStock(Activity activity, String type2,int userID, String itemName,boolean hasHaving){
        double result = 0.0;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT SUM(a.quantity) - (ISNULL(x.qty,0) + ISNULL(xx.transferFromSales,0)) [quantity] FROM tblproduction a OUTER APPLY(SELECT c.itemname,SUM(c.qty) [qty] FROM tbltransaction2 b INNER JOIN tblorder2 c ON c.ordernum = b.ordernum AND CAST(b.datecreated As date)=CAST(c.datecreated AS date) WHERE CAST(b.datecreated AS date)=(SELECT TOP 1 CAST(datecreated AS date) FROM tblinvsum ORDER BY invsumid DESC) AND a.item_name = c.itemname AND b.status2 IN ('Unpaid','Paid') AND b.inventory_type='Own Inventory' AND b.createdby=(SELECT username FROM tblusers WHERE systemid=" + userID + ") GROUP BY c.itemname)x OUTER APPLY(SELECT SUM(b.quantity)[transferFromSales] FROM tblproduction b WHERE b.item_name = a.item_name AND b.inv_id = a.inv_id AND b.type2='Transfer from Sales' AND b.transfer_from = a.transfer_from)xx WHERE a.inv_id=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND a.type2='" + type2 + "' AND a.transfer_from=(SELECT username FROM tblusers WHERE systemid=" + userID + ") AND a.item_name LIKE '%" + itemName + "%' GROUP BY x.qty,xx.transferFromSales " + (hasHaving ? "HAVING ISNULL(SUM(a.quantity),0) - ISNULL(x.qty,0) > 0" : "") + ";";
                System.out.println(query);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = rs.getDouble("quantity");
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "checkOwnInventoryStock() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    public double getSalesCharge(Activity activity,String itemName, double actualQuantity, int userID){
        double result = 0.00;
        try{
            con = cc.connectionClass(activity);
            if (con == null) {
                Toast.makeText(activity, "Check Your Internet Access", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT SUM(a.quantity) - (ISNULL(x.qty,0) + ISNULL(xx.transferFromSales,0)) [quantity] FROM tblproduction a OUTER APPLY(SELECT c.itemname,SUM(c.qty) [qty] FROM tbltransaction2 b INNER JOIN tblorder2 c ON c.ordernum = b.ordernum AND CAST(b.datecreated As date)=CAST(c.datecreated AS date) WHERE CAST(b.datecreated AS date)=(SELECT TOP 1 CAST(datecreated AS date) FROM tblinvsum ORDER BY invsumid DESC) AND a.item_name = c.itemname AND b.status2 IN ('Unpaid','Paid') AND b.inventory_type='Own Inventory' AND b.createdby=(SELECT username FROM tblusers WHERE systemid=" + userID + ") GROUP BY c.itemname)x OUTER APPLY(SELECT SUM(b.quantity)[transferFromSales] FROM tblproduction b WHERE b.item_name = a.item_name AND b.inv_id = a.inv_id AND b.type2='Transfer from Sales' AND b.transfer_from = a.transfer_from)xx WHERE a.inv_id=(SELECT TOP 1 invnum FROM tblinvsum ORDER BY invsumid DESC) AND a.type2='Transfer to Sales' AND a.transfer_from=(SELECT username FROM tblusers WHERE systemid=" + userID + ") AND a.item_name LIKE '%" + itemName + "%' GROUP BY a.item_name,x.qty,xx.transferFromSales ORDER BY 1 ASC";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    double endBal = rs.getDouble("quantity");
                    result = actualQuantity - endBal;
                }
            }
        }catch (Exception ex){
            Toast.makeText(activity, "getSalesCharge() " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }
}
