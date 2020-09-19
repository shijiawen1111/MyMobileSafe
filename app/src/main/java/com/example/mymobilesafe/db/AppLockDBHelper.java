package com.example.mymobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JW.S on 2020/9/14 8:55 PM.
 */
public class AppLockDBHelper extends SQLiteOpenHelper {
    public AppLockDBHelper(Context context) {
        super(context, AppLockDB.NAME, null, AppLockDB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建表 的时候回调这个方法
        db.execSQL(AppLockDB.TableAppLock.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //数据库版本更新的时候回调这个方法
    }

    public interface AppLockDB {
        String NAME = "applock.db";
        int VERSION = 1;

        interface TableAppLock {
            String TABLE_NAME = "t_applock";
            String COLUMN_ID = "_id";
            String COLUMN_PKG= "pkg";//用来记录上锁应用的包名
            String SQL_CREATE_TABLE = "create table " + TABLE_NAME + "("
                    + COLUMN_ID + " integer primary key autoincrement,"
                    + COLUMN_PKG + " text unique" + ")";
        }
    }
}
