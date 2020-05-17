package com.example.mymobilesafe.db;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mymobilesafe.bean.BlackBean;

public class BlackDao {
	private BlackDBHelper mHelper;

	public BlackDao(Context context) {
		mHelper = new BlackDBHelper(context);
	}

	public boolean add(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackDB.TableBlack.COLUMN_NUMBER, number);
		values.put(BlackDB.TableBlack.COLUMN_TYPE, type);

		long insert = db.insert(BlackDB.TableBlack.TABLE_NAME, null, values);

		db.close();
		return insert != -1;
	}

	public boolean update(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackDB.TableBlack.COLUMN_TYPE, type);

		String whereClause = BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };

		int update = db.update(BlackDB.TableBlack.TABLE_NAME, values,
				whereClause, whereArgs);

		db.close();

		return update != 0;
	}

	public boolean delete(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		String whereClause = BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };
		int delete = db.delete(BlackDB.TableBlack.TABLE_NAME, whereClause,
				whereArgs);
		db.close();
		return delete != 0;
	}

	public List<BlackBean> findAll() {
		List<BlackBean> list = new ArrayList<BlackBean>();

		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " + BlackDB.TableBlack.COLUMN_NUMBER + ","
				+ BlackDB.TableBlack.COLUMN_TYPE + " from "
				+ BlackDB.TableBlack.TABLE_NAME;

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {

			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;

				list.add(bean);
			}
			// 关闭
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<BlackBean> findPart(int pageSize, int index) {
		// select * from black limit 10 offset 20;

		// limit:查询的数量
		// offset:从第几条查询
		List<BlackBean> list = new ArrayList<BlackBean>();

		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " + BlackDB.TableBlack.COLUMN_NUMBER + ","
				+ BlackDB.TableBlack.COLUMN_TYPE + " from "
				+ BlackDB.TableBlack.TABLE_NAME + " limit " + pageSize
				+ " offset " + index;

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {

			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;

				list.add(bean);
			}
			// 关闭
			cursor.close();
		}

		db.close();

		return list;

	}

	public int findType(String number) {
		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " + BlackDB.TableBlack.COLUMN_TYPE + " from "
				+ BlackDB.TableBlack.TABLE_NAME + " where "
				+ BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { number });

		int type = -1;// -1没有这个号码
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
			}
		}
		db.close();

		return type;
	}

}
