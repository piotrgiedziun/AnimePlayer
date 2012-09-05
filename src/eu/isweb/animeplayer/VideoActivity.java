package eu.isweb.animeplayer;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ActionBar.OnMenuVisibilityListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.VideoView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VideoActivity extends Activity {

	protected static AlertDialog alertDialog;
	int mLastSystemUiVis;
	AnimeMediaController mc;
	ArrayList<Video> nextVideos;
	Video video;
	Anime anime;
	Epizode epizode;
	AnimeDatabaseManager db;
	Context instance;
	static ProgressBar mProgress;
	Button mCloseButton;
	Button mNextButton;
	VideoView videoView;
	boolean blockVisibilityChange = false;
	
    private void setFullscreen(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (on) {
            winParams.flags |=  bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    
    void setAndBlockNavVisibility(boolean visible) {
    	setNavVisibility(visible);
    	blockVisibilityChange = true;
    }
    
	void setNavVisibility(boolean visible) {
		if( blockVisibilityChange ) {
			return;
		}
		Log.d("JD", "setNavVisibility("+visible+")");
		int newVis = View.SYSTEM_UI_FLAG_VISIBLE;
		
		if (!visible) {
			newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}
		setFullscreen(visible);
		getWindow().getDecorView().setSystemUiVisibility(newVis);
		mc.toggle(visible);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void finish() {
		videoView.stopPlayback();
		super.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.video_jump:
			AlertDialog.Builder builder;

			Context mContext = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.video_jump, (ViewGroup) findViewById(R.id.layout_root));
			
			final Button jump = (Button) layout.findViewById(R.id.jump);
			final SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBar);
			sb.setMax(3*60);
			sb.setProgress(db.getJumpTime(anime.URL));
			jump.setText(getString(R.string.jump) + " +" + displayTime(sb.getProgress()) + " min");

			sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					jump.setText(getString(R.string.jump)+" +" + displayTime(progress) + " min");
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					db.setJumpTime(anime.URL, sb.getProgress());
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}
			});
			
			jump.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					videoView.seekTo(videoView.getCurrentPosition() + sb.getProgress()*1000);
					videoView.start();
					VideoActivity.alertDialog.dismiss();
				}
			});
					
			builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			alertDialog = builder.create();
			alertDialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private String displayTime(int sec) {
		int min = sec / 60;
		sec = sec - (min*60);
		return min + ":" + (sec<10?("0"+sec):sec);
	}
	
	
	public void goToNext() {
		if( nextVideos.size() < 1) {
			return;
		}
		Intent intent = new Intent(this, VideoActivity.class);
		intent.putExtra("video", nextVideos.get(0));
		intent.putExtra("anime", anime);
		intent.putExtra("epizode", epizode);
		nextVideos.remove(0);
	    intent.putExtra("next", nextVideos);
		startActivity(intent);
		
		finish();
	}
	
	@Override
	protected void onPause() {
		videoView.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		videoView.start();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getActionBar().setBackgroundDrawable(null);
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        win.setAttributes(winParams);
        
        setContentView(R.layout.activity_video);

        instance = this;
        db = ((AnimeApp) getApplication()).getDB();
        
        mProgress = (ProgressBar)findViewById(R.id.progress);
        mNextButton = (Button)findViewById(R.id.next_button);
        mCloseButton = (Button)findViewById(R.id.close_button);
        videoView = (VideoView)findViewById(R.id.video);
        
        mCloseButton.setVisibility(View.INVISIBLE);
        mCloseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        mNextButton.setVisibility(View.GONE);
        mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToNext();
			}
		});
        
        mc = new AnimeMediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        mc.setOnHideListener(new OnHideListener() {
			@Override
			public void onHide() {
				Log.d("JD", "hide jo jo");
				setNavVisibility(false);
			}
		});
        videoView.setMediaController(mc);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				setNavVisibility(false);
				mProgress.setVisibility( View.INVISIBLE );
				mp.start();
			}
		});
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mProgress.setVisibility( View.INVISIBLE );
				mCloseButton.setVisibility(View.VISIBLE);
				
				if( nextVideos.size() >= 1) {
					mNextButton.setVisibility(View.VISIBLE);
				}
				
				setAndBlockNavVisibility(true);
			}
		});

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
        
		if (extras != null) {
			nextVideos = new ArrayList<Video>();
			video = (Video) extras.getSerializable("video");
			anime = (Anime) extras.getSerializable("anime");
			epizode = (Epizode) extras.getSerializable("epizode");
			nextVideos = (ArrayList<Video>) extras.getSerializable("next");
			
			setTitle(anime.name + " : " + epizode.name);

			if(video.type.equals(Video.TYPE_VK)) {
				new AnimeDownloader<Video>() {
					@Override
					protected void onPostExecuteAction(final ArrayList<Video> result) {
						AlertDialog.Builder builder = new AlertDialog.Builder(instance);
						
						if(result.size() == 0) {
							builder.setMessage("Error, can not find valid video url")
						       .setCancelable(false)
						       .setNeutralButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
						           @Override
								public void onClick(DialogInterface dialog, int id) {
						                VideoActivity.this.finish();
						           }
						       });
							AlertDialog alert = builder.create();
							alert.show();
							return;
						}
						
						ArrayList<String> stringResult = new ArrayList<String>();
						for(Video item : result)
							stringResult.add(item.name);
						
						//select quality
						builder.setTitle("Select quality");
						builder.setItems(stringResult.toArray(new CharSequence[stringResult.size()]), new DialogInterface.OnClickListener() {
						    @Override
							public void onClick(DialogInterface dialog, int item) {
						    	videoView.setVideoURI(Uri.parse(result.get(item).URL));
						        videoView.requestFocus();
						        videoView.start();  
						    }
						});
						AlertDialog alert = builder.create();
						alert.show();
						}

					@Override
					protected ArrayList<Video> doInBackgroundAction(Document doc) {
						ArrayList<Video> result = new ArrayList<Video>();
						
						Elements elements = doc.select("source[src]");
						for (Element element : elements) {
							String split[]= element.attr("src").split("\\.");
							if(split.length == 0) continue;
							
						    result.add(new Video( split[split.length-2] + "p" , element.attr("src"), Video.TYPE_VK, 0 ));
						}
						
						return result;
					};
				}.execute(video.URL);
			}else{
				videoView.setVideoURI(Uri.parse(video.URL));
		        videoView.requestFocus();
		        videoView.start();  
			}
		}
    }
	
}
