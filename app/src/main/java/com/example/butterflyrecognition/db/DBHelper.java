package com.example.butterflyrecognition.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dr.P on 2017/11/8.
 */

public class DBHelper {
    //数据库名
    public static final String DB_NAME = "cool_weather";
    //数据库版本
    public static final int VERSION = 1;

    private static DBHelper dbHelper;

    private SQLiteDatabase db;

    //构造方法私有化
    public DBHelper(Context context) {
        // TODO Auto-generated constructor stub
        OpenDBHelper dbHelper=new OpenDBHelper(context, DB_NAME, null, VERSION);
        db=dbHelper.getWritableDatabase();
    }

    //获取DBHelper的实例
    public synchronized static DBHelper getInstance(Context context) {
        if (dbHelper==null) {
            dbHelper=new DBHelper(context);
        }
        return dbHelper;
    }

    //将ButterflyInfo实例存到数据库
    public void saveButterfly(ButterflyInfo butterflyInfo) {
        if (butterflyInfo!=null) {
            ContentValues values=new ContentValues();
            values.put("butterfly_id",butterflyInfo.getId());
            values.put("butterfly_imageUrl",butterflyInfo.getImageUrl());
            values.put("butterfly_name", butterflyInfo.getName());
            values.put("butterfly_ename", butterflyInfo.getLatinName());
            values.put("butterfly_type",butterflyInfo.getType());
            values.put("butterfly_desc",butterflyInfo.getFeature());
            db.insert("butterflyinfo", null, values);
        }
    }

    //从数据库读取蝴蝶信息
    public List<ButterflyInfo> loadButterflyInfos() {
        List<ButterflyInfo> list=new ArrayList<ButterflyInfo>();
        Cursor cursor=db.query("ButterflyInfo", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ButterflyInfo butterflyInfo=new ButterflyInfo();
                butterflyInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
                butterflyInfo.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
                butterflyInfo.setName(cursor.getString(cursor.getColumnIndex("butterflyInfo_name")));
                butterflyInfo.setLatinName(cursor.getString(cursor.getColumnIndex("butterflyInfo_ename")));
                butterflyInfo.setType(cursor.getString(cursor.getColumnIndex("butterfly_type")));
                butterflyInfo.setFeature(cursor.getString(cursor.getColumnIndex("butterfly_desc")));
                list.add(butterflyInfo);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
