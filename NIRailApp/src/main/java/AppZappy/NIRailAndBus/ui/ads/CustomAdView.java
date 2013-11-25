package AppZappy.NIRailAndBus.ui.ads;

import AppZappy.NIRailAndBus.userdata.Settings;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CustomAdView extends LinearLayout
{
	public CustomAdView(Context context)
	{
		this(context, null);
	}
	
	public CustomAdView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		if (Settings.isUsingAds())
			AdsController.addAds(this, (Activity)context);
	}

}
