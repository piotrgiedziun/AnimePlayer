package eu.isweb.animeplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras(); 
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_anime);
        
        instance = this;
        mText = (TextView) findViewById(android.R.id.empty);
    	mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter = new ArrayAdapter<Video>(this,
        	R.layout.listview_item,
        	videosList));
        mListView.setTextFilterEnabled(true);
        
        if (extras != null) {
        	downloadEpizodeList(extras.getString("url"));
        	this.setTitle(extras.getString("name"));
        }
    }
    
    public void downloadEpizodeList(String url) {
    	mText.setText("Retrieving data...");

    	this.setProgressBarIndeterminateVisibility(true);
    	new AnimeVideosDownloader(){
    		protected void onPostExecute(ArrayList<Video> results) {
    			cm.remove(this.hashCode());

    			videosList.clear();
    			for(Video result : results) {
    				videosList.add(result);
    			}
    			
    			mAdapter.notifyDataSetChanged();
    			if(cm.count() == 0)
    				instance.setProgressBarIndeterminateVisibility(false);
    			
    			if(videosList.isEmpty()) {
    				mText.setText("No results found!");
    			}
    		};
    	}.execute(url);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Log.d("JD", "onclick");
    	
    	Video selectedVideo = (Video)getListView().getItemAtPosition(position);
    	
    	Intent intent = new Intent(this, WebVideoActivity.class);
    	intent.putExtra("url", selectedVideo.URL);
    	intent.putExtra("type", selectedVideo.type);
    	startActivity(intent);
    	super.onListItemClick(l, v, position, id);
    }
}
