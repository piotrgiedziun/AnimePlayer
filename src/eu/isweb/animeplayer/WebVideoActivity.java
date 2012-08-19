package eu.isweb.animeplayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebVideoActivity extends Activity {
	WebView webView = null;
	
	private final Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
	    public void run() {
	    	getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras(); 
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        
		setContentView(R.layout.activity_test); 

		webView = (WebView)findViewById(R.id.webview);
		webView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			    handler.postDelayed(runnable, 3000);
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
		webView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setWebChromeClient(new WebChromeClient());	
		webView.setWebViewClient(new WebViewClient(){ 
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				return true;
			} 
		});
        if (extras != null) {
        	webView.stopLoading();
        	if(extras.getString("type").equals("anime-shinden.info")) {
        		webView.loadDataWithBaseURL("animeplayer://animename", extras.getString("url"), "text/html", "UTF-8", null);
        	}else{
        		webView.loadUrl(extras.getString("url"));
        	}
        }  
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			webView.stopLoading();
			webView.loadUrl("about:blank");
			webView.removeAllViews();
			handler.removeCallbacks(runnable);
			runnable = null;
			Log.d("JD", "close!");
			finish();
	    }
		return super.onKeyDown(keyCode, event);
	}

    
    @Override
    protected void onPause(){
        super.onPause();
        
        callHiddenWebViewMethod("onPause");

        webView.pauseTimers();
        if(isFinishing()){
        	webView.loadUrl("about:blank");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        webView.resumeTimers();
        callHiddenWebViewMethod("onResume");
        handler.postDelayed(runnable, 3000);
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig){
	    super.onConfigurationChanged(newConfig);
	}
    
    private void callHiddenWebViewMethod(String name){
        if( webView != null ){
            try {
                Method method = WebView.class.getMethod(name);
                method.invoke(webView);
            } catch (Exception e) {
                Log.d("JD", "Exception: " + name + e);
            }
        }
    }
}
    