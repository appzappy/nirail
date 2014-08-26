package appzappy.nirail.ui.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;
import appzappy.nirail.util.AndroidVersion;

/**
 * Fix for know bug with ViewFlipper.
 * All uses of ViewFlipper must be replaced with <AppZappy.NIRailAndBus.ui.widgets.FixedViewFlipper>
 * http://daniel-codes.blogspot.com/2010/05/viewflipper-receiver-not-registered.html
 * http://code.google.com/p/emailalbum/source/browse/EmailAlbumAndroid/tags/REL-2_10_3/src/com/kg/emailalbum/mobile/util/MyViewFlipper.java
 * http://www.izazael.com/?p=186
 */
public class FixedViewFlipper extends ViewFlipper
{

	public FixedViewFlipper(Context context)
	{
		super(context);
	}

    public FixedViewFlipper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
	
    @Override
    protected void onDetachedFromWindow()
    {
    	if (AndroidVersion.isValid(AndroidVersion.ANDROID_1_6, AndroidVersion.ANDROID_2_3__3_4))
    	{
    		try
            {
                super.onDetachedFromWindow();
            }
            catch (IllegalArgumentException e)
            {
                // Call stopFlipping() in order to kick off updateRunning()
                stopFlipping();
            }
    	}
    	else
    	{
    		super.onDetachedFromWindow();
    	}
    }
    
}
