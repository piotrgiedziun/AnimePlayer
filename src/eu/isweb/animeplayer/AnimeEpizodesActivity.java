package eu.isweb.animeplayer;

import java.util.ArrayList;

import org.jsoup.nodes.Element;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class AnimeEpizodesActivity extends ListActivity implements
		SearchView.OnQueryTextListener {

	Activity instance;
	AnimeDatabaseManager db;
	Menu menu;
	ListView mListView;
	ArrayAdapter<Epizode> mAdapter;
	TextView mText;
	SearchView mSearch;
	ArrayList<Epizode> epizodeList = new ArrayList<Epizode>();
	String animeName;
	String animeURL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_anime);

		instance = this;
		db = ((AnimeApp) getApplication()).getDB();

		mText = (TextView) findViewById(android.R.id.empty);
		mSearch = (SearchView) findViewById(R.id.search_view);
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter = new ArrayAdapter<Epizode>(this,
				R.layout.listview_item, epizodeList){
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				TextView t = (TextView) v.findViewById(android.R.id.text1);
				if(epizodeList.get(position).isLastWatched()) {
					t.setTextColor(Color.parseColor("#FF6C00"));
				}else{
					t.setTextColor(Color.BLACK);
				}
				return v;
			}
		});
		mListView.setTextFilterEnabled(true);

		if (extras != null) {
			downloadEpizodeList(extras.getString("url"));
			this.setTitle(extras.getString("name"));
			animeName = extras.getString("name");
			animeURL = extras.getString("url");
		}
	}

	private void updateMenuFavorites() {
		MenuItem favorites = menu.findItem(R.id.menu_favorites);
		if (db.isFavorites(animeURL)) {
			favorites.setIcon(android.R.drawable.btn_star_big_on);
		} else {
			favorites.setIcon(android.R.drawable.btn_star_big_off);
		}
		AnimeFavoritesFragment.refreshFavorites();
	}

	public void downloadEpizodeList(String url) {
		mText.setText("Feething data...");
		mSearch.setVisibility(View.GONE);
		this.setProgressBarIndeterminateVisibility(true);
		new AnimeEpizodesDownloader() {
			protected void onPostExecute(ArrayList<Epizode> results) {
				cm.remove(this.hashCode());

				epizodeList.clear();
				for (Epizode result : results) {
					epizodeList.add(result);
				}

				mAdapter.notifyDataSetChanged();
				if (cm.count() == 0)
					instance.setProgressBarIndeterminateVisibility(false);

				if (!epizodeList.isEmpty()) {
					mSearch.setVisibility(View.VISIBLE);
					setupSearchView();
					goToLastWatched();
				} else {
					mText.setText("No results found!");
				}
			};
		}.execute(url);
	}

	protected void goToLastWatched() {
		String lastURL = db.getLastEpizodeURL(animeURL);
		int position = 0;
		//find lastURL in list
		for(int i=0; i< epizodeList.size(); i++) {
			if(epizodeList.get(i).URL.equals(lastURL)) {
				position = i;
				break;
			}
		}
		//move listview
		mListView.setSelection(position);
		//unselect all
		for(int i=0; i< epizodeList.size(); i++) {
			epizodeList.get(i).lastWatched = false;
		}
		epizodeList.get(position).lastWatched = true;
		mAdapter.notifyDataSetChanged();		
	}

	private void saveCurrentListState(String epizodeName, String URL) {
		db.insertHistory(animeURL, animeName, epizodeName, URL);
		AnimeHistoryFragment.refreshHistory();
		goToLastWatched();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Epizode selectedEpizode = (Epizode) getListView().getItemAtPosition(
				position);

		saveCurrentListState(selectedEpizode.name, selectedEpizode.URL);
		Intent intent = new Intent(this, AnimeVideosActivity.class);
		intent.putExtra("url", selectedEpizode.URL);
		intent.putExtra("name", animeName + " : " + selectedEpizode.name);
		
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	private void setupSearchView() {
		mSearch.setIconifiedByDefault(false);
		mSearch.setOnQueryTextListener(this);
		mSearch.setSubmitButtonEnabled(false);
		mSearch.setQueryHint("Enter epizode title");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.activity_epizodes, menu);
		updateMenuFavorites();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_favorites:
			db.toogleFavorites(animeURL, animeName);
			updateMenuFavorites();
			return true;
		case R.id.menu_goToLastWatched:
			Log.d("JD", "gogo");
			goToLastWatched();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
