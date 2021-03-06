package eu.isweb.animeplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AboutAnimeActivity extends Activity {
	
	WebView webView;
	Anime anime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_test); 

		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
		webView = (WebView)findViewById(R.id.webview);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setProgressBarIndeterminateVisibility(true);
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				setProgressBarIndeterminateVisibility(false);
				super.onPageFinished(view, url);
			}
		});
		
		if (extras != null) {
			String type = extras.getString("type");
			anime = (Anime) extras.getSerializable("anime");
			
			if( type.equals("myanimelist.net") ) {
				webView.loadUrl("http://myanimelist.net/anime.php?q="+anime.name);
				setTitle("MyAnimeList : " + anime.name);
			} else {
				webView.loadUrl("http://tanuki.pl/szukaj/"+anime.name);
				setTitle("Tanuki : " + anime.name);
			}
        }  
		
		super.onCreate(savedInstanceState);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	    	webView.goBack();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
	    super.onConfigurationChanged(newConfig);
	} 
}
