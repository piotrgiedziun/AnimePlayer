package eu.isweb.animeplayer;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class WebVideoActivity extends Activity {
	WebView webView = null;
	Video video;
	Anime anime;
	AnimeDatabaseManager db;
	int mLastSystemUiVis;

	Runnable mNavHider = new Runnable() {
		public void run() {
			setNavVisibility(false);
		}
	};

	void setNavVisibility(boolean visible) {
		int newVis = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		if (!visible) {
			newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}

		if (visible) {
			Handler h = getWindow().getDecorView().getHandler();
			if (h != null) {
				h.removeCallbacks(mNavHider);
				h.postDelayed(mNavHider, 3000);
			}
		}

		getWindow().getDecorView().setSystemUiVisibility(newVis);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getActionBar().setBackgroundDrawable(null);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_test);

		db = ((AnimeApp) getApplication()).getDB();
		
		View view = findViewById(R.id.video_player_layout);
		view.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				int diff = mLastSystemUiVis ^ visibility;
				mLastSystemUiVis = visibility;
				if ((diff & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0
						&& (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
					setNavVisibility(true);
				}
			}
		});
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		webView = (WebView) findViewById(R.id.webview);
		webView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setNavVisibility(false);
				return false;
			}
		});

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setPluginsEnabled(true);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(false);
		webView.getSettings().setBuiltInZoomControls(false);
		//webView.getSettings().setDefaultZoom(ZoomDensity.FAR);

		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});
		if (extras != null) {
			video = (Video) extras.getSerializable("video");
			anime = (Anime) extras.getSerializable("anime");
			Epizode e = (Epizode) extras.getSerializable("epizode");
			
			setTitle(anime.name + " : " + e.name);
			
			webView.stopLoading();
			webView.loadUrl(video.URL);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			webView.stopLoading();
			webView.loadUrl("about:blank");
			webView.removeAllViews();
			Log.d("JD", "close!");
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();

		callHiddenWebViewMethod("onPause");

		webView.pauseTimers();
		if (isFinishing()) {
			webView.loadUrl("about:blank");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.resumeTimers();
		callHiddenWebViewMethod("onResume");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	private void callHiddenWebViewMethod(String name) {
		if (webView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(webView);
			} catch (Exception e) {
				Log.d("JD", "Exception: " + name + e);
			}
		}
	}
}
