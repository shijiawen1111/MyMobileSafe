package com.example.mymobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by JW.S on 2020/9/22 8:19 PM.
 */
public class AntivirusDao {
    public AntivirusDao(){

    }
    /**
     * 判断是否是病毒文件
     * @param context
     * @param MD5
     * @return
     */
    public static boolean isVirus(Context context, String MD5) {
        String path = new File(context.getFilesDir(), "antivirus.db").getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select count(_id) from datable where MD5=?";
        Cursor cursor = db.rawQuery(sql, new String[]{MD5});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count > 0;
    }

}
