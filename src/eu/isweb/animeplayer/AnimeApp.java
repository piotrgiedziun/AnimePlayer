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
        super.onTerminate();
        db.close();
    }
	
	public AnimeDatabaseManager getDB() {
		return db;
	}

}
