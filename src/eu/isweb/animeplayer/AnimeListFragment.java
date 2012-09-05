package eu.isweb.animeplayer;
import java.util.ArrayList;
import java.util.Vector;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import eu.isweb.animeplayer.HTMLLinkExtrator.HtmlLink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

    public class AnimeListFragment extends ListFragment
    	implements Refreshable, SearchView.OnQueryTextListener {
    	Activity parent;
    	ListView mListView;
    	ArrayAdapter<Anime> mAdapter;
    	TextView mText; 
    	SearchView mSearch;
    	ArrayList<Anime> animeList = new ArrayList<Anime>();
        
    	private String LIST_ALPHA_URL = "http://www.anime-shinden.info/animelist/index.php";
    	private String LIST_RATING_URL = "http://www.anime-shinden.info/animelist/index.php?first=rank";
    	private String LIST_VIEWS_URL = "http://www.anime-shinden.info/animelist/index.php?first=views";
    	
    	private String LIST_URL = LIST_ALPHA_URL;
    	
        @Override
        public void onAttach(Activity activity) {
        	parent = activity;
        	super.onAttach(activity);
        }
        
        public void onSort(MenuItem item) {
        	switch( item.getItemId() ) {
        	case R.id.action_sort_alpha:
        		downloadAnimeList( LIST_ALPHA_URL );
        		break;
        		
        	case R.id.action_sort_size:
        		downloadAnimeList( LIST_VIEWS_URL );
        		break;
        	}
        	
        }
        
        public void downloadAnimeList( String url ) {
        	LIST_URL = url;
        	mText.setText(getString(R.string.downloading_data));
        	
        	new AnimeDownloader<Anime>(parent){
        		@Override
        		protected void onPostExecuteAction(ArrayList<Anime> result) {
        			animeList.clear();
        			animeList.addAll(result);
        			
        			mAdapter.notifyDataSetChanged();
        			
        			if(!animeList.isEmpty()) {
        				mSearch.setVisibility(View.VISIBLE);
        			}else{
        				mSearch.setVisibility(View.GONE);
        				mText.setText(getString(R.string.no_results_found));
        			}
        		}

				@Override
				protected ArrayList<Anime> doInBackgroundAction(Vector<HtmlLink> links) 
					throws Exception {
					ArrayList<Anime> result = new ArrayList<Anime>();
					
					if( links.size() == 0) {
						throw new Exception("try to retry");
					}
					
					for (HtmlLink link : links) {
						result.add(new Anime(link.linkText, link.link));
					}
					
					return result;
				};
        	}.execute( url, "main" );
        }
        
        @Override
        public void onResume() {
        	getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        	super.onResume();
        }
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
        	refresh();
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
        	mSearch = (SearchView) view.findViewById(R.id.search_view);
        	mListView = (ListView) view.findViewById(android.R.id.list);
            mListView.setAdapter(mAdapter = new ArrayAdapter<Anime>(c,
            	R.layout.listview_item, animeList));
            mListView.setTextFilterEnabled(true);
            setupSearchView();
            return view;
        }

        private void setupSearchView() {
        	mSearch.setIconifiedByDefault(false);
        	mSearch.setOnQueryTextListener(this);
            mSearch.setSubmitButtonEnabled(false);
            mSearch.setQueryHint(getString(R.string.enter_anime_name));
        }

        @Override
		public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                mListView.clearTextFilter();
            } else {
                mListView.setFilterText(newText.toString());
            }
            return true;
        }

        @Override
		public boolean onQueryTextSubmit(String query) {
            return false;
        }

		@Override
		public void refresh() {
			this.downloadAnimeList( LIST_URL );
		}
    }