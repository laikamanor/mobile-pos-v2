package com.example.atlanticbakery;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper3 extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "jkl.db";
    public static final String TABLE_NAME = "jkl";
    public  static  final String COL_1 = "id";
    public  static  final String COL_2 = "sap_number";
    public  static  final String COL_3 = "fromBranch";
    public  static  final String COL_4 = "item_name";
    public  static  final String COL_5 = "quantity";
    public  static  final String COL_6 = "actual_quantity";
    public  static  final String COL_7 = "isSelected";
    public DatabaseHelper3(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,sap_number INTEGER, fromBranch TEXT,item_name TEXT,quantity INTEGER,actual_quantity INTEGER,isSelected INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String sapNumber, String fromBranch,  String itemName,Double quantity,Integer actual_quantity){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, sapNumber);
        contentValues.put(COL_3, fromBranch);
        contentValues.put(COL_4, itemName);
        contentValues.put(COL_5, quantity);
        contentValues.put(COL_6, actual_quantity);
        contentValues.put(COL_7, 0);
        long resultQuery = db.insert(TABLE_NAME,null,contentValues);
        boolean result;
        result = resultQuery != -1;
        return result;
    }

    public Cursor getAllData(){
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);
        return  cursor;
    }

    public  Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ?", new  String[] {id});
    }

    public boolean updateSelected(String id,Integer isSelected, Double actual_quantity){
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7, isSelected);
        contentValues.put(COL_6, actual_quantity);
        db.update(TABLE_NAME, contentValues, "id= ?", new String[]{id});
        return true;
    }

    public  Integer deleteItem(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ?", new  String[] {id});
    }

    public void truncateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("VACUUM");
    }

    public boolean checkItemExist(String itemName,Integer sapNumber){
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE item_name='" + itemName + "' AND sap_number=" + sapNumber + ";",null);
        if(cursor.moveToFirst()){
            do{
                result = true;
            }
            while (cursor.moveToNext());
        }
        return result;
    }

    public Integer countItems(){
        int resultPrice = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor result = db.rawQuery("SELECT COUNT(id) FROM " + TABLE_NAME + ";", null);
        if(result.moveToFirst()){
            do{
                resultPrice = Integer.parseInt(result.getString(0));
            }
            while (result.moveToNext());
        }
        return resultPrice;
    }
}
