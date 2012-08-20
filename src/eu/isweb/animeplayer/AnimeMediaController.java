package eu.isweb.animeplayer;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.MediaController;

interface OnHideListener {
    void onHide();
}

public class AnimeMediaController extends MediaController {
	OnHideListener onHideListener = null;
	
	public AnimeMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimeMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public AnimeMediaController(Context context) {
        super(context, true);
    }

    @Override
	public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            ((Activity) getContext()).finish();

        return super.dispatchKeyEvent(event);
    }
    
    @Override
    public void hide() {
    	if(onHideListener != null)
    		onHideListener.onHide();
    	super.hide();
    }
    
    public void setOnHideListener(OnHideListener e) {
    	this.onHideListener = e;
    }
    
    public void toggle(boolean show) {
    	if( show )
    		super.show();
    	else
    		super.hide();
    }
}
