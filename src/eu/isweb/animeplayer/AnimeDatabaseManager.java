package eu.isweb.animeplayer;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class AnimeDatabaseManager extends SQLiteOpenHelper {

	private static final String DB_FILE_NAME = "database.db";
	private String historyTableName = "history";
	private String[] historyColumns = { "url", "name", "epizode", "lastURL" };
	private String favoritesTableName = "favorites";
	private String[] favoritesColumns = { "url", "name" };
	private String jumpTableName = "jump";
	private String[] jumpTableColumns = { "url", "time" };

	public AnimeDatabaseManager(Context context) {
		super(context, DB_FILE_NAME, null, 8);
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
	
	public int getJumpTime(String url) {
		Cursor c = getReadableDatabase().query(jumpTableName, null, "url = \""+url+"\"",
				null, null, null, null);
		if (!c.moveToFirst()) 
			return 0;
		
		return c.getInt(2);
	}
	
	public long setJumpTime(String url, int time) {
		if(isInHistory(url)) {
			getWritableDatabase().delete(jumpTableName, "url = \""+url+"\"", null);
		}
		ContentValues v = new ContentValues();
		v.put(jumpTableColumns[0], url);
		v.put(jumpTableColumns[1], time);
		return getWritableDatabase().insert(jumpTableName, null, v);
	}
	
	public long insertHistory(History h) {
		if(isInHistory(h.url)) {
			getWritableDatabase().delete(historyTableName, "url = \""+h.url+"\"", null);
		}
		ContentValues v = new ContentValues();
		v.put(historyColumns[0], h.url);
		v.put(historyColumns[1], h.name);
		v.put(historyColumns[2], h.epizode);
		v.put(historyColumns[3], h.lastURL);
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
	
	public void toogleFavorites(Anime a) {
		if(isFavorites(a.URL)) {
			getWritableDatabase().delete(favoritesTableName, "url = \""+a.URL+"\"", null);
		}else{
			ContentValues v = new ContentValues();
			v.put(favoritesColumns[0], a.URL);
			v.put(favoritesColumns[1], a.name);
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
		db.execSQL("DROP TABLE IF EXISTS "+jumpTableName);
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		createTable(db, historyTableName, historyColumns);
		createTable(db, favoritesTableName, favoritesColumns);
		createTable(db, jumpTableName, jumpTableColumns);
	}

	public void removeHistory(String url) {
		getWritableDatabase().delete(historyTableName, "url = \""+url+"\"", null);
	}

}
