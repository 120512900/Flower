package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(@Nullable Context context) {

        super(context, "mydb.db", null, 1);

    }

    @Override

    public void onCreate(SQLiteDatabase db) {

        db.execSQL(

                "create table info(name varchar(20),url varchar(100) primary key,Baike varchar(100))"

        );

    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



    }
}
