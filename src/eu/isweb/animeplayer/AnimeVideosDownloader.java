package eu.isweb.animeplayer;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;
	
	class AnimeVideosDownloader extends AsyncTask<String, Void, ArrayList<Video>> {
	
		protected ConnectionManager cm;
		
		@Override
		protected void onPreExecute() {
			cm = ConnectionManager.getInstance();
			cm.add(this.hashCode());
			super.onPreExecute();
		}
		
		protected ArrayList<Video> doInBackground(String... attributes) {
        	Document doc = null;
        	ArrayList<Video> result = new ArrayList<Video>();
        	try {
				doc = Jsoup.connect(attributes[0]).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	Elements elements;

        	//vk.com
			elements = doc.select("iframe[src^=http://vk.com/]");
			for (Element element : elements) {
				int count = 1;
				for(Video video : result) {
					if(video.type.equals("vk.com"))
						count++;
				}
			    result.add(new Video( "vk.com #"+count , element.attr("src"), "vk.com" ));
			}
			
			//sibnet.ru
			elements = doc.select("embed[src^=http://video.sibnet.ru/]");
			for (Element element : elements) {
				int count = 1;
				for(Video video : result) {
					if(video.type.equals("sibnet.ru"))
						count++;
				}
			    result.add(new Video( "sibnet.ru #"+count , element.attr("src"), "sibnet.ru" ));
			}
			
        	//dailymotion.com
			elements = doc.select("iframe[src^=http://www.dailymotion.com/]");
			for (Element element : elements) {
				int count = 1;
				for(Video video : result) {
					if(video.type.equals("dailymotion.com"))
						count++;
				}
			    result.add(new Video( "dailymotion.com #"+count , element.attr("src"), "dailymotion.com" ));
			}
			
			//anime-shinden.info
			elements = doc.select("embed[src^=http://anime-shinden.info/]");
			for (Element element : elements) {
				int count = 1;
				for(Video video : result) {
					if(video.type.equals("anime-shinden.info"))
						count++;
				}
				String url = element.attr("flashvars");
				int start = url.indexOf("http://anime-shinden.info/player/hd.php?link=");
				int end = (url.substring(start, url.length()-1-start)).indexOf("mp4");
				url = url.substring(start, end+start) + "mp4";
				url = "<html><body  style=\"padding:0px; margin:0px\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <video width=\"100%\" height=\"100%\" controls=\"controls\"><source src=\""+url+"\" type=\"video/mp4\" autoplay=\"autoplay\"/></video></body></html>";
			    result.add(new Video( "anime-shinden.info #"+count , url, "anime-shinden.info" ));
			}
			
			return result;
        }
		
		@Override
		protected void onCancelled() {
			cm.remove(this.hashCode());
			super.onCancelled();
		}
        
}