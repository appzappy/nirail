package appzappy.nirail.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class BetterGallery extends Gallery {
	private boolean scrollingHorizontally = false;

	public BetterGallery(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}

	public BetterGallery(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public BetterGallery(Context context) {
	    super(context);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		return super.onFling(e1, e2, velocityX, velocityY);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
	    super.onInterceptTouchEvent(ev);
	    return scrollingHorizontally;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	    scrollingHorizontally = true;
	    return super.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    switch(event.getAction()) {
	    case MotionEvent.ACTION_UP:
	    case MotionEvent.ACTION_CANCEL:
	        scrollingHorizontally = false;
	    }

	    return super.onTouchEvent(event);
	}
}