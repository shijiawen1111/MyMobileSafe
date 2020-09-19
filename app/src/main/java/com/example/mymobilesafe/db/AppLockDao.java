package com.example.mymobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by JW.S on 2020/9/14 9:16 PM.
 * 实现数据库的增删改查
 */
public class AppLockDao {

    private AppLockDBHelper mHelper;

    public AppLockDao(Context context) {
        mHelper = new AppLockDBHelper(context);
    }

    /**
     * 添加数据
     * @param packageName
     * @return
     */
    public boolean add(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppLockDBHelper.AppLockDB.TableAppLock.COLUMN_PKG, packageName);
        long insert = database.insert(AppLockDBHelper.AppLockDB.TableAppLock.TABLE_NAME, null, values);
        database.close();
        return insert != -1;
    }

    /**
     * 删除数据
     * @param packageName
     * @return
     */
    public boolean delete(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = AppLockDBHelper.AppLockDB.TableAppLock.COLUMN_PKG + "=?";
        String[] whereArgs = new String[]{packageName};
        int delete = database.delete(AppLockDBHelper.AppLockDB.TableAppLock.TABLE_NAME, whereClause, whereArgs);
        database.close();
        return delete > 0;
    }

    /**
     * 查找数据
     * @param packageName
     * @return
     */
    public boolean findIsLock(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "select count(_id) from "
                + AppLockDBHelper.AppLockDB.TableAppLock.TABLE_NAME + " where "
                + AppLockDBHelper.AppLockDB.TableAppLock.COLUMN_PKG + "=?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();
        return count != 0;
    }
}
