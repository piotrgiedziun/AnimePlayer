package eu.isweb.animeplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	AnimeDatabaseManager db;
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Context instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        
        instance = this;
        db = ((AnimeApp) getApplication()).getDB();
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        
        new FlashPlayerManager(this).install();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
        	switch (i) {
        		case 0: return new AnimeHistoryFragment();
        		case 1: return new AnimeListFragment();
        		case 2: return new AnimeFavoritesFragment();
        	}
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "HISTORY";
                case 1: return "ALL";
                case 2: return "FAVORITES";
            }
            return null;
        }
    }
    
    private void about() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("All good stuff:\n- anime-shinden.info\n\nPizza lover:\n- JD\n\nBeta testing:\n- Madrim\n- Johniak")
    		   .setTitle("About")
    	       .setCancelable(false)
    	       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	builder.create().show();
    }
    
    private String getFragmentTag(int pos){
        return "android:switcher:"+R.id.pager+":"+pos;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			about();
			return true;
		case R.id.menu_refresh:
			AnimeFavoritesFragment.refreshFavorites();
			AnimeHistoryFragment.refreshHistory();
			AnimeListFragment f = (AnimeListFragment) MainActivity.this
			        .getSupportFragmentManager().findFragmentByTag(getFragmentTag(1));
			f.downloadAnimeList();
			return true;
		case R.id.menu_clearHistory:
			db.clearHistory();
			AnimeHistoryFragment.refreshHistory();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
