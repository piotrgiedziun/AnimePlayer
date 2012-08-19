package eu.isweb.animeplayer;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;

public abstract class AnimeDownloader<T>
	extends AsyncTask<String, Void, ArrayList<T>>	{
	protected ConnectionManager cm;
	protected static String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2; en-gb; GT-P1000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	
	@Override
	protected void onPreExecute() {
		cm = ConnectionManager.getInstance();
		cm.add(this.hashCode());
		super.onPreExecute();
	}
	
	abstract protected ArrayList<T> doInBackgroundAction(Document doc);
	abstract protected void onPostExecuteAction(ArrayList<T> result);
	
	@Override
	protected ArrayList<T> doInBackground(String... attributes) {
    	Document doc = null;
    	ArrayList<T> result = new ArrayList<T>();
    	try {
			doc = Jsoup.connect(attributes[0])
					.userAgent(USER_AGENT).get();

        	if(doc == null)
        		return result;
        	
			result = doInBackgroundAction(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		return result;
    }
	
	@Override
	protected void onPostExecute(ArrayList<T> result) {
		cm.remove(this.hashCode());
		onPostExecuteAction(result);
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		cm.remove(this.hashCode());
		super.onCancelled();
	}
}
