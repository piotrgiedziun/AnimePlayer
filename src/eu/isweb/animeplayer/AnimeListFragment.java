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
    public class AnimeListFragment extends ListFragment
    	implements SearchView.OnQueryTextListener {
    	Activity parent;
    	ListView mListView;
    	ArrayAdapter<Anime> mAdapter;
    	TextView mText; 
    	SearchView mSearch;
    	ArrayList<Anime> animeList = new ArrayList<Anime>();
        
        @Override
        public void onAttach(Activity activity) {
        	parent = activity;
        	super.onAttach(activity);
        }
        
        public void downloadAnimeList() {
        	mText.setText("Feething data...");
        	mSearch.setVisibility(View.GONE);
        	parent.setProgressBarIndeterminateVisibility(true);
        	new AnimeListDownloader(){
        		protected void onPostExecute(ArrayList<Anime> results) {
        			cm.remove(this.hashCode());

        			animeList.clear();
        			for(Anime result : results) {
        				animeList.add(result);
        			}
        			
        			mAdapter.notifyDataSetChanged();
        			if(cm.count() == 0)
        				parent.setProgressBarIndeterminateVisibility(false);
        			
        			if(!animeList.isEmpty()) {
        				mSearch.setVisibility(View.VISIBLE);
        				setupSearchView();
        			}else{
        				mText.setText("No results found!");
        			}
        		};
        	}.execute();
        }
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
        	downloadAnimeList();
        	super.onActivityCreated(savedInstanceState);
        }
        
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
        	Log.d("JD", "onclick");
        	
        	Anime selectedAnime=(Anime)getListView().getItemAtPosition(position);
        	
        	Intent intent = new Intent(parent, AnimeEpizodesActivity.class);
        	intent.putExtra("url", selectedAnime.URL);
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
        	mSearch = (SearchView) view.findViewById(R.id.search_view);
        	mListView = (ListView) view.findViewById(android.R.id.list);
            mListView.setAdapter(mAdapter = new ArrayAdapter<Anime>(c,
            	R.layout.listview_item,
            	animeList));
            mListView.setTextFilterEnabled(true);

            return view;
        }

        private void setupSearchView() {
        	mSearch.setIconifiedByDefault(false);
        	mSearch.setOnQueryTextListener(this);
            mSearch.setSubmitButtonEnabled(false);
            mSearch.setQueryHint("Enter anime title");
        }

        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                mListView.clearTextFilter();
            } else {
                mListView.setFilterText(newText.toString());
            }
            return true;
        }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    }