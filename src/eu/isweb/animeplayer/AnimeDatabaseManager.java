package eu.isweb.animeplayer;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;

public class AnimeDatabaseManager extends SQLiteOpenHelper {

	private static final String DB_FILE_NAME = "database.db";
	private String historyTableName = "history";
	private String[] historyColumns = { "url", "name", "epizode", "lastURL" };
	private String favoritesTableName = "favorites";
	private String[] favoritesColumns = { "url", "name" };

	public AnimeDatabaseManager(Context context) {
		super(context, DB_FILE_NAME, null, 7);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	private void createTable(SQLiteDatabase db, String tableName, String[] tableColumns) {
		String sql = "create table " + tableName + " (" + BaseColumns._ID
				+ " integer primary key autoincrement ";

		for (int i = 0; i < tableColumns.length; i++) {
			sql += ", " + tableColumns[i];
		}

		sql += " ) ";

		db.execSQL(sql);
	}

	public boolean isInHistory(String url) {
		Cursor c = getReadableDatabase().query(historyTableName, null, "url = \""+url+"\"",
				null, null, null, null);
		return c.moveToFirst();
	}
	
	public long insertHistory(String url, String name, String epizode, String lastURL) {
		if(isInHistory(url)) {
			getWritableDatabase().delete(historyTableName, "url = \""+url+"\"", null);
		}
		ContentValues v = new ContentValues();
		v.put(historyColumns[0], url);
		v.put(historyColumns[1], name);
		v.put(historyColumns[2], epizode);
		v.put(historyColumns[3], lastURL);
		return getWritableDatabase().insert(historyTableName, null, v);
	}
	
	public String getLastEpizodeURL(String url) {
		Cursor c = getReadableDatabase().query(historyTableName, null, "url = \""+url+"\"",
				null, null, null, null);
		if (!c.moveToFirst()) 
			return "";
		
		return c.getString(4);
	}

	public ArrayList<History> getHistory() {
		Cursor c = getReadableDatabase().query(historyTableName, null, null,
				null, null, null, "_id DESC");
		ArrayList<History> result = new ArrayList<History>();
		if (c.moveToFirst()) {
			do {
				result.add(new History(c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
			} while (c.moveToNext());
		}
		
		return result;
	}
	
	public boolean isFavorites(String url) {
		Cursor c = getReadableDatabase().query(favoritesTableName, null, "url = \""+url+"\"",
				null, null, null, null);
		return c.moveToFirst();
	}
	
	public void toogleFavorites(String url, String name) {
		if(isFavorites(url)) {
			getWritableDatabase().delete(favoritesTableName, "url = \""+url+"\"", null);
		}else{
			ContentValues v = new ContentValues();
			v.put(favoritesColumns[0], url);
			v.put(favoritesColumns[1], name);
			getWritableDatabase().insert(favoritesTableName, null, v);
		}
	}
	
	public ArrayList<Favorites> getFavorites() {
		Cursor c = getReadableDatabase().query(favoritesTableName, null, null,
				null, null, null, "_id DESC");
		ArrayList<Favorites> result = new ArrayList<Favorites>();
		if (c.moveToFirst()) {
			do {
				result.add(new Favorites(c.getString(1), c.getString(2)));
			} while (c.moveToNext());
		}
		
		return result;
	}
	
	public void clearHistory() {
		getWritableDatabase().execSQL("DELETE FROM "+historyTableName);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS "+historyTableName);
		db.execSQL("DROP TABLE IF EXISTS "+favoritesTableName);
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		createTable(db, historyTableName, historyColumns);
		createTable(db, favoritesTableName, favoritesColumns);
	}

}
