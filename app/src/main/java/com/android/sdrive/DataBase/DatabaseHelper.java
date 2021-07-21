package com.android.sdrive.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.sdrive.Component.Utils;
import com.android.sdrive.DataBase.Tables.FileStorage;
import org.json.JSONArray;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "trooopleWorld_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FileStorage.CREATE_TABLE);


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FileStorage.TABLE_NAME);


        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public int Insert(String TABLE_NAME, HashMap<String, String> Insert_Value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (HashMap.Entry<String, String> entry : Insert_Value.entrySet()) {
            values.put(entry.getKey(), (String) entry.getValue());
        }
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return (int) id;
    }

    public JSONArray getRow(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return Utils.jsonArray(db.rawQuery("select * from " + table_name + "", null));
    }

    public JSONArray getResult(String table_name, String Column[], String Value[]) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder strBuilder = new StringBuilder();
        String morewhere = "";
        for (int i = 0; i < Column.length; i++) {
            if (i == 1) {
                morewhere = " AND ";
            }
            strBuilder.append(morewhere + String.valueOf(Column[i]) + " = " + "'" + String.valueOf(Value[i]) + "'");
        }
        String where = strBuilder.toString();
        return Utils.jsonArray(db.rawQuery("select * from " + table_name + " WHERE " + where + " ORDER by id DESC ", null));
    }


    public boolean Update(String table_name, HashMap<String, String> myMap, String Column[], String Value[]) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (HashMap.Entry<String, String> entry : myMap.entrySet()) {
            contentValues.put(entry.getKey(), (String) entry.getValue());
        }

        StringBuilder strBuilder = new StringBuilder();
        String morewhere = "";
        for (int i = 0; i < Column.length; i++) {
            Log.w("key", ">>>>>>>>>>" + String.valueOf(i));
            if (i == 1) {
                morewhere = " AND ";
            }
            strBuilder.append(morewhere + String.valueOf(Column[i]) + " = " + "'" + String.valueOf(Value[i]) + "'");
        }
        String where = strBuilder.toString();

        db.update(table_name, contentValues, where, null);
        return true;
    }

    public boolean delete(String table_name, String Column[], String Value[]) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder strBuilder = new StringBuilder();
        String morewhere = "";
        for (int i = 0; i < Column.length; i++) {
            if (i == 1) {
                morewhere = " AND ";
            }
            strBuilder.append(morewhere + String.valueOf(Column[i]) + " = " + "'" + String.valueOf(Value[i]) + "'");
        }
        String where = strBuilder.toString();
        return db.delete(table_name, where, null) > 0;

    }

    public JSONArray getMessage(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return Utils.jsonArray(db.rawQuery("SELECT TimeStamp FROM " + table_name + " GROUP BY TimeStamp ORDER by id ASC ", null));
    }

    public void DeleteTable(String tablename){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ tablename); //delete all rows in a table
        db.close();
    }

}
