package eu.isweb.animeplayer;
import java.util.ArrayList;

import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

/**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public class AnimeHistoryFragment extends ListFragment {
    	Activity parent;
    	static AnimeDatabaseManager db;
    	static ListView mListView;
    	static ArrayAdapter<History> mAdapter;
    	TextView mText; 
    	static ArrayList<History> animeList = new ArrayList<History>();
        
        @Override
        public void onAttach(Activity activity) {
        	parent = activity;
        	db = ((AnimeApp) parent.getApplication()).getDB();
        	super.onAttach(activity);
        }
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
        	super.onActivityCreated(savedInstanceState);
        }
        
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
        	
        	History selectedAnime=(History)getListView().getItemAtPosition(position);
        	
        	Intent intent = new Intent(parent, AnimeEpizodesActivity.class);
        	intent.putExtra("url", selectedAnime.url);
        	intent.putExtra("name", selectedAnime.name);
        	startActivity(intent);
        	super.onListItemClick(l, v, position, id);
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	Context c = getActivity().getApplicationContext();
        	View view = inflater.inflate(R.layout.listview, container, false);
        	
        	mText = (TextView) view.findViewById(android.R.id.empty);
        	mListView = (ListView) view.findViewById(android.R.id.list);
            mListView.setAdapter(mAdapter = new ArrayAdapter<History>(c,
            	R.layout.listview_item,
            	animeList));

            refreshHistory();
            
            return view;
        }

        public static void refreshHistory() {
        	animeList.clear();
        	for(History i : db.getHistory()) {
        		animeList.add(i);
        	}
        	mAdapter.notifyDataSetChanged();
		}
    }