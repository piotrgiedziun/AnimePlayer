package eu.isweb.animeplayer;

import android.app.Application;

public class AnimeApp extends Application {

	private AnimeDatabaseManager db;

	@Override
	public void onCreate() {
		super.onCreate();
		db = new AnimeDatabaseManager(this);
	}

	@Override
    public void onTerminate() {
        db.close();
        super.onTerminate();
    }
	
	public AnimeDatabaseManager getDB() {
		return db;
	}

}
