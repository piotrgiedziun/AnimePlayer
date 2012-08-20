package eu.isweb.animeplayer;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

    public class AnimeHistoryFragment extends ListFragment
    	implements Refreshable {
    	Activity parent;
    	TextView mText; 
    	static AnimeDatabaseManager db;
    	static ListView mListView;
    	static ArrayAdapter<History> mAdapter;
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
        	intent.putExtra("anime", new Anime(selectedAnime.name, selectedAnime.url));
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
        	mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        	mListView.setMultiChoiceModeListener(new AnimeHistoryDeleteCallback());
            mListView.setAdapter(mAdapter = new ArrayAdapter<History>(c,
            		R.layout.listview_item_selectable, animeList));

            refreshHistory();
            
            return view;
        }

        public static void refreshHistory() {
        	animeList.clear();
        	
        	if(db == null ) return;
        	for(History i : db.getHistory()) {
        		animeList.add(i);
        	}
        	mAdapter.notifyDataSetChanged();
		}
        
		@Override
		public void refresh() {
			refreshHistory();			
		}
        
        private class AnimeHistoryDeleteCallback  implements ListView.MultiChoiceModeListener {

        	
            @Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getActivity().getMenuInflater().inflate(R.menu.history_delete, menu);
                mode.setTitle(getString(R.string.select_items));
                mode.setSubtitle(getString(R.string.history_subtitile));
                return true;
            }

            @Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                case R.id.menu_delete:
                	SparseBooleanArray list = getListView().getCheckedItemPositions();
         			for(int i = 0; i < list.size(); i++) {
         				int lPos = list.keyAt(i);
         				if(list.get(lPos)) {
         					History selectedAnime=(History)getListView().getItemAtPosition(lPos);
         					db.removeHistory(selectedAnime.url);
         				}
         			}
         			refreshHistory();
                    Toast.makeText(getActivity(), getString(R.string.done), Toast.LENGTH_SHORT).show();
                    mode.finish();
                    break;
                }
                return true;
            }

            @Override
			public void onItemCheckedStateChanged(ActionMode mode,
                    int position, long id, boolean checked) {
            }
            
        	@Override
        	public void onDestroyActionMode(ActionMode arg0) {
        		
        	}
            
        }
    }