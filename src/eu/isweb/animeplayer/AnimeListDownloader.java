package eu.isweb.animeplayer;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
	
	class AnimeListDownloader extends AsyncTask<String, Void, ArrayList<Anime>> {
	
		protected ConnectionManager cm;
		private static String URL = "http://www.anime-shinden.info/index.php?do=cat&category=online-glowna";
		
		@Override
		protected void onPreExecute() {
			cm = ConnectionManager.getInstance();
			cm.add(this.hashCode());
			super.onPreExecute();
		}
		
		protected ArrayList<Anime> doInBackground(String... attributes) {
        	Document doc = null;
        	ArrayList<Anime> result = new ArrayList<Anime>();
        	try {
				doc = Jsoup.connect(URL).get();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Elements elements = doc.select("a");
			boolean dump = false;
			for (Element element : elements) {
				if(element.text().equals(".hack//Roots"))
					dump = true;
				if(dump) {
					result.add(new Anime(element.text(), element.attr("href")));
    			if(element.text().equals("Zombie Loan"))
    				break;
				}
			}
			
			return result;
        }
		
		@Override
		protected void onCancelled() {
			cm.remove(this.hashCode());
			super.onCancelled();
		}
        
}