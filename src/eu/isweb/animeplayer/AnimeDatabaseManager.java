package eu.isweb.animeplayer;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class AnimeDatabaseManager extends SQLiteOpenHelper {

	private static final String DB_FILE_NAME = "database.db";
	private String historyTableName = "history";
	private String[] historyColumns = { "url", "name", "date" };
	private String favoritesTableName = "favorites";
	private String[] favoritesColumns = { "url" };

	public AnimeDatabaseManager(Context context) {
		super(context, DB_FILE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db, historyTableName, historyColumns);
		createTable(db, favoritesTableName, favoritesColumns);
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

	public long inserHistory(String url, String name) {
		ContentValues v = new ContentValues();
		v.put(historyColumns[0], url);
		v.put(historyColumns[1], name);
		return getWritableDatabase().insert(historyTableName, null, v);
	}

	public ArrayList<String> getHistory() {
		Cursor c = getReadableDatabase().query(historyTableName, null, null,
				null, null, null, null);
		ArrayList<String> result = new ArrayList<String>();
		if (c.moveToFirst()) {
			do {
				result.add(c.getString(0));
			} while (c.moveToNext());
		}
		
		return result;
	}
	
	public boolean isFavorites(String url) {
		Cursor c = getReadableDatabase().query(favoritesTableName, null, "url = \""+url+"\"",
				null, null, null, null);
		return c.moveToFirst();
	}
	
	public void toogleFavorites(String url) {
		if(isFavorites(url)) {
			getWritableDatabase().delete(favoritesTableName, "url = \""+url+"\"", null);
		}else{
			ContentValues v = new ContentValues();
			v.put(favoritesColumns[0], url);
			getWritableDatabase().insert(favoritesTableName, null, v);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
