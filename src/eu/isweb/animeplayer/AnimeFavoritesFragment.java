package eu.isweb.animeplayer;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

    public class AnimeFavoritesFragment extends ListFragment
		implements Refreshable {
    	Activity parent;
    	TextView mText;
    	static AnimeDatabaseManager db;
    	static ListView mListView;
    	static ArrayAdapter<Anime> mAdapter; 
    	static ArrayList<Anime> animeList = new ArrayList<Anime>();
        
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
        	Anime selectedAnime=(Anime)getListView().getItemAtPosition(position);
        	
        	Intent intent = new Intent(parent, AnimeEpizodesActivity.class);
        	intent.putExtra("anime", selectedAnime);
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
            mListView.setAdapter(mAdapter = new ArrayAdapter<Anime>(c,
            	R.layout.listview_item, animeList));

           	refreshFavorites();
            
            return view;
        }

        public static void refreshFavorites() {
        	animeList.clear();
        	for(Favorites i : db.getFavorites()) {
        		animeList.add(new Anime(i.name, i.url));
        	}
        	mAdapter.notifyDataSetChanged();
		}

		@Override
		public void refresh() {
			refreshFavorites();
		}

    }