package com.waiki.daftarbelanja.storage;

import java.io.File;
import java.util.ArrayList;

import com.waiki.daftarbelanja.common.Helper;
import com.waiki.daftarbelanja.obj.ItemObj;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
	public static final String DATABASE_PATH = "";
	public static final String DATABASE_NAME = "DaftarBelanjaDB.db";
	public static final int DATABASE_VERSION = 3;

	private static final String TABLE_ITEMS = "ITEMS";

	private static Database instance = null;

	private SQLiteDatabase db;
	private DatabaseHelper helper;
	private Context context;

	public static Database getInstance(Context context) {
		if (instance == null)
			instance = new Database(context);

		return instance;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_PATH + DATABASE_NAME, null,
					DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "
					+ TABLE_ITEMS
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, qty NUMBER, price NUMBER, item_date DATETIME)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String[] targetTables = new String[] { TABLE_ITEMS };
			for (int i = 0; i < targetTables.length; i++) {
				db.execSQL("DROP TABLE IF EXISTS " + targetTables[i]);
			}

			onCreate(db);
		}

	}

	public Database(Context c) {
		context = c;
	}

	public Database open() throws SQLException {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
		return this;
	}

	public void close() {
		helper.close();
	}

	public boolean checkDatabase() {
		File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
		return dbFile.exists();
	}

	public int insertItem(ItemObj itemObj) {
		db.execSQL("INSERT INTO "
				+ TABLE_ITEMS
				+ "(name, qty, price, item_date) "
				+ " VALUES(\'"
				+ replaceApostrophe(itemObj.getName()) + "\', "
				+ itemObj.getQty() + ", "
				+ itemObj.getPrice() + ", '"
				+ Helper.dateToString(itemObj.getItemDate(), "yyyy-MM-dd") + "'"
				+ ")");
		
		Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
		cursor.moveToFirst();
		int lastInsertId = cursor.getInt(0);
		cursor.close();
		return lastInsertId;
	}
	
	public void updateItem(ItemObj itemObj) {
		db.execSQL("UPDATE "
				+ TABLE_ITEMS
				+ " SET name='"+itemObj.getName()+"',"
				+ " qty="+itemObj.getQty()+","
				+ " price="+itemObj.getPrice()+","
				+ " item_date='"+itemObj.getItemDate()+"'"
				+ " WHERE id="+itemObj.getId()
				);
	}
	
	public void deleteItem(int id) {
		db.execSQL("DELETE FROM "
				+ TABLE_ITEMS
				+ " WHERE id="+id
				);
	}
	
	public ArrayList<ItemObj> getAllItems() {
		ArrayList<ItemObj> retVal = new ArrayList<ItemObj>();
		Cursor cursor = db.rawQuery(
				"SELECT id, name, qty, price, item_date FROM "
						+ TABLE_ITEMS, null);
		if (cursor.moveToFirst()) {
			do {
				retVal.add(new ItemObj(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), Helper.stringToDate(cursor.getString(4), "yyyy-MM-dd")));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return retVal;
	}
	
	public int getTotalPrice() {
		Cursor cursor = db.rawQuery(
				"SELECT SUM(price*qty) FROM "
						+ TABLE_ITEMS, null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}

	/**
	 * HELPER
	 */

	private String replaceApostrophe(String value) {
		return value.replace("'", "''");
	}

}
