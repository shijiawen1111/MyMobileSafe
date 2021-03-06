package com.example.mymobilesafe.db;

/**
 * 
 * 黑名单数据库的表结构类
 * 
 */
public interface BlackDB {

	String NAME = "black.db";
	int VERSION = 1;

	/**
	 * 表：black
	 * 
	 */
	public interface TableBlack {
		String TABLE_NAME = "black";

		// 列
		String COLUMN_ID = "_id";
		String COLUMN_NUMBER = "number";
		String COLUMN_TYPE = "type";// 电话-0，短信-1，全部-2

		// sql
		String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NUMBER
				+ " TEXT UNIQUE," + COLUMN_TYPE + " INTEGER" + ")";

	}

}
