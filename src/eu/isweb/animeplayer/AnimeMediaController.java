package eu.isweb.animeplayer;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.MediaController;

public class AnimeMediaController extends MediaController {
	public AnimeMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimeMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public AnimeMediaController(Context context) {
        super(context, true);
    }

    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            ((Activity) getContext()).finish();

        return super.dispatchKeyEvent(event);
    }
}
