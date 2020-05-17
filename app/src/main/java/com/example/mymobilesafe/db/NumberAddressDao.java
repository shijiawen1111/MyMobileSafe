package com.example.mymobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressDao {

	public static String findAddress(Context context, String number) {
		File file = new File(context.getFilesDir(), "address.db");
		String path = file.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		String address = "未知号码";
		// 13,14,15,17,18 ---> 11位
		// 1[34578]\d
		String reg = "^1[34578]\\d{9}$";
		boolean isPhone = number.matches(reg);
		if (isPhone) {
			String prefix = number.substring(0, 7);

			String sql = "select cardtype from info where mobileprefix=?";
			Cursor cursor = db.rawQuery(sql, new String[] { prefix });

			if (cursor != null) {
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}
				cursor.close();
			}
		} else {
			switch (number.length()) {
			case 3:
				// 110,119,120
				address = "警报电话";
				break;
			case 4:
				// 5556
				address = "模拟器&亲情号码";
				break;
			case 5:
				// 95555
				address = "银行号码";
				break;
			case 7:
			case 8:
				// 8123456
				// 81234567
				address = "本地号码";
				break;
			case 10:
			case 11:
			case 12:
				// 010-8123456外地
				// 010-81234567外地
				// 0755-8123456外地
				// 0755-81234567外地
				// 查询3位
				String prefix = number.substring(0, 3);

				String sql = "select city from info where area=?";
				Cursor cursor = db.rawQuery(sql, new String[] { prefix });

				if (cursor != null) {
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
				}

				if ("未知号码".equals(address)) {
					// 3位没有查出，查询4位

					prefix = number.substring(0, 4);
					sql = "select city from info where area=?";
					cursor = db.rawQuery(sql, new String[] { prefix });

					if (cursor != null) {
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}
				break;
			default:
				break;
			}
		}
		db.close();

		return address;
	}
}
