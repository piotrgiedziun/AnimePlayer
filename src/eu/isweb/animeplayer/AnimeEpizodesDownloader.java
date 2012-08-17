package eu.isweb.animeplayer;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
	
	class AnimeEpizodesDownloader extends AsyncTask<String, Void, ArrayList<Epizode>> {
	
		protected ConnectionManager cm;
		
		@Override
		protected void onPreExecute() {
			cm = ConnectionManager.getInstance();
			cm.add(this.hashCode());
			super.onPreExecute();
		}
		
		protected ArrayList<Epizode> doInBackground(String... attributes) {
        	Document doc = null;
        	ArrayList<Epizode> result = new ArrayList<Epizode>();
        	try {
				doc = Jsoup.connect(attributes[0]).get();
			} catch (IOException e) {
				e.printStackTrace();
			}

        	//vk.com
			Elements elements = doc.select("a");
			for (Element element : elements) {
				if(element.text().contains("Odcinek"))
					result.add(new Epizode( element.text(), element.attr("href") ));
			}
			
			
			return result;
        }
		
		@Override
		protected void onCancelled() {
			cm.remove(this.hashCode());
			super.onCancelled();
		}
        
}