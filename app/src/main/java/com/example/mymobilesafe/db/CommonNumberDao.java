package com.example.mymobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;

import com.example.mymobilesafe.bean.ChildBean;
import com.example.mymobilesafe.bean.GroupBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/8/10 11:43 PM.
 */
public class CommonNumberDao {
    public static List<GroupBean> getGroupDatas(Context context) {
        List<GroupBean> list = new ArrayList<>();
        //通过上下文拿到文件的路径
        File file = new File(context.getFilesDir(), "commonnum.db");
        //获取绝对路径
        
        String path = file.getAbsolutePath();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);

        //打开数据库,仅仅只有让读的权限
        //sql查询
        String sql = "select name,idx from classlist";
        Cursor groupCursor = database.rawQuery(sql, null);
        if (groupCursor != null) {
            while (groupCursor.moveToNext()) {
                String title = groupCursor.getString(0);
                int idx = groupCursor.getInt(1);
                GroupBean groupBean = new GroupBean();
                groupBean.title = title;
                groupBean.childrenDatas = new ArrayList<ChildBean>();
                String childSql = "select number,name from table" + idx;
                Cursor childCursor = database.rawQuery(childSql, null);
                if (childCursor != null) {
                    while (childCursor.moveToNext()) {
                        String number = childCursor.getString(0);
                        String name = childCursor.getString(1);
                        ChildBean childBean = new ChildBean();
                        childBean.number = number;
                        childBean.name = name;
                        groupBean.childrenDatas.add(childBean);
                    }
                    childCursor.close();
                }
                list.add(groupBean);
            }
            groupCursor.close();
        }
        database.close();
        return list;
    }
}
