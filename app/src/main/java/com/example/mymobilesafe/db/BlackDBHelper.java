package com.example.mymobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackDBHelper extends SQLiteOpenHelper {

	public BlackDBHelper(Context context) {
		super(context, BlackDB.NAME, null, BlackDB.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 建表的操作
		db.execSQL(BlackDB.TableBlack.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
