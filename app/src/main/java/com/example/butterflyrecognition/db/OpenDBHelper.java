package com.example.butterflyrecognition.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dr.P on 2017/11/8.
 */

public class OpenDBHelper extends SQLiteOpenHelper{

    private static final String BUTTERFLY_TABLE = "butterflyinfo";

    /*
    Butterfly表创建语句
     */
    public static final String CREATE_BUTTERFLY="create table butterflyinfo"+
            "id integer primary key,"+"imageUrl varchar(70),"+
            "name varchar(40),"+"ename varchar(50)"+
            "type varchar(40),"+"desc varchar(500));";

    public OpenDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BUTTERFLY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " +BUTTERFLY_TABLE);
        onCreate(db);
    }
}
