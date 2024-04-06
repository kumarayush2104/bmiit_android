package com.bmiit.practical1.modules;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SQLHandler extends SQLiteOpenHelper {

    public SQLHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Users(Employee Text NOT NULL, Password Text NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Leave(Employee Text NOT NULL, Leave String NOT NULL, Status var_char(100) NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void getAllLeaves(ArrayList<Leave> leaves) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM Leave Where Status == 'waiting'", null);
        cursor.moveToFirst();
        if(cursor.getCount() == 0) {
            return;
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                leaves.add(new Leave(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                cursor.moveToNext();
            }
        }
    }

    public void changeLeaveStatus(String employee, String date, String status) {
        getWritableDatabase().execSQL("UPDATE Leave set Status ='" + status + "' where Employee='" + employee + "' and Leave='" + date + "'");
    }

    public void applyLeave(String employee, String date) {
        getWritableDatabase().execSQL("INSERT INTO Leave VALUES('" + employee + "', '" + date + "', 'waiting')");
    }

    public void getLeaves(ArrayList<Leave> leaves, String employee) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM Leave where Employee = '" + employee + "'", null);
        cursor.moveToFirst();
        if(cursor.getCount() == 0) {
            return;
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                leaves.add(new Leave(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                cursor.moveToNext();
            }
        }
    }

    public String login(String employee, String password) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM Users where Employee = '" + employee + "' and Password = '" + password + "'", null);
        cursor.moveToFirst();
        if(cursor.getCount() == 0) {
            return "Login Failed";
        } else {
            return cursor.getString(0);
        }
    }
}
