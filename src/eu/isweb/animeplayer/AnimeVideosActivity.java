package eu.isweb.animeplayer;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class AnimeVideosActivity extends ListActivity {

	Activity instance;
	ListView mListView;
	ArrayAdapter<Video> mAdapter;
	TextView mText; 
	SearchView mSearch;
	ArrayList<Video> videosList = new ArrayList<Video>();
	Epizode epizode;
	Anime anime;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras(); 
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_anime);
        
        ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
        
        instance = this;
        mText = (TextView) findViewById(android.R.id.empty);
    	mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter = new ArrayAdapter<Video>(this,
        	R.layout.listview_item,
        	videosList));
        mListView.setTextFilterEnabled(true);
        
        if (extras != null) {
        	epizode = (Epizode) extras.getSerializable("epizode");
        	anime = (Anime) extras.getSerializable("anime");
        	downloadEpizodeList(epizode.URL);
        	this.setTitle(anime.name + " : " +epizode.name);
        }
    }
    
    public void downloadEpizodeList(String url) {
    	mText.setText(getString(R.string.downloading_data));

    	new AnimeDownloader<Video>(this){

			@Override
			protected ArrayList<Video> doInBackgroundAction(Document doc) 
				throws Exception {
				ArrayList<Video> result = new ArrayList<Video>();
				
				Elements elements = null;
	        	
				//anime-shinden.info
				elements = doc.select("embed[src^=http://anime-shinden.info/]");
				for (Element element : elements) {
					int count = 1;
					for(Video video : result) {
						if(video.type.equals(Video.TYPE_ANIME_SHIDEN))
							count++;
					}
					String url = element.attr("flashvars");
					int start = url.indexOf("http://anime-shinden.info/player/hd.php?link=");
					int end = (url.substring(start, url.length()-1-start)).indexOf("mp4");
					url = url.substring(start, end+start) + "mp4";
				    result.add(new Video( "anime-shinden.info #"+count , url, Video.TYPE_ANIME_SHIDEN, count ));
				}
				
	        	//vk.com
				elements = doc.select("iframe[src^=http://vk.com/]");
				for (Element element : elements) {
					int count = 1;
					for(Video video : result) {
						if(video.type.equals(Video.TYPE_VK))
							count++;
					}
				    result.add(new Video( "vk.com #"+count , element.attr("src"), Video.TYPE_VK, count ));
				}
				
	        	//dailymotion.com
				elements = doc.select("iframe[src^=http://www.dailymotion.com/]");
				for (Element element : elements) {
					int count = 1;
					for(Video video : result) {
						if(video.type.equals(Video.TYPE_DAILYMOTION))
							count++;
					}
				    result.add(new Video( "dailymotion.com #"+count , element.attr("src"), Video.TYPE_DAILYMOTION, count ));
				}
				
				//sibnet.ru
				elements = doc.select("embed[src^=http://video.sibnet.ru/]");
				for (Element element : elements) {
					int count = 1;
					for(Video video : result) {
						if(video.type.equals(Video.TYPE_SIBNET))
							count++;
					}
				    result.add(new Video( "sibnet.ru #"+count , element.attr("src"), Video.TYPE_SIBNET, count ));
				}
				
				//myspace.com
				elements = doc.select("embed[src^=http://mediaservices.myspace.com/]");
				for (Element element : elements) {
					int count = 1;
					for(Video video : result) {
						if(video.type.equals(Video.TYPE_MYSPACE))
							count++;
					}
				    result.add(new Video( "myspace.com #"+count , element.attr("src"), Video.TYPE_MYSPACE, count ));
				}

				if( result.size() == 0) {
					throw new Exception("try to retry");
				}
				
				return result;
			}

			@Override
			protected void onPostExecuteAction(ArrayList<Video> result) {
				videosList.clear();
				videosList.addAll(result);
				
    			mAdapter.notifyDataSetChanged();
    			
    			if(videosList.isEmpty()) {
    				mText.setText(getString(R.string.no_results_found));
    			}
			};
    	}.execute(url);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Video selectedVideo = (Video)getListView().getItemAtPosition(position);
    	Intent intent = null;
    	
    	if(selectedVideo.type.equals(Video.TYPE_ANIME_SHIDEN) 
    			|| selectedVideo.type.equals(Video.TYPE_VK) ) {
        	intent = new Intent(this, VideoActivity.class);
        	
        	ArrayList<Video> nextVideos = new ArrayList<Video>();
        	//find videos in same hosting
        	for (Video video : videosList) {
        		if( video.type.equals(selectedVideo.type) && video.position > selectedVideo.position) {
        			nextVideos.add(video);
        		}
        	}
        	intent.putExtra("next", nextVideos);
        	
    	}else{
        	intent = new Intent(this, WebVideoActivity.class);
    	}
    	intent.putExtra("video", selectedVideo);
    	intent.putExtra("anime", anime);
    	intent.putExtra("epizode", epizode);
    	startActivity(intent);
    	
    	super.onListItemClick(l, v, position, id);
    }
}
