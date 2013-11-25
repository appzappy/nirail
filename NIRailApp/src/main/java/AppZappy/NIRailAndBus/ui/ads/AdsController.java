package AppZappy.NIRailAndBus.ui.ads;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.location.Location;
import android.widget.LinearLayout;
import AppZappy.NIRailAndBus.geolocation.LocationSource;

public class AdsController
{
	public static final String AD_MOB_PUBLISHER_ID = "a14d7a132bbabbc";
	
	public static final String RICHARD_HTC_DESIRE = "E3DB1F89140B1B6DBBF2EBB6F2E2F557";


	public static void addAds(LinearLayout container, Activity activity)
	{
		AdView view = new AdView(activity, AdSize.BANNER, AD_MOB_PUBLISHER_ID);
		container.addView(view);
		
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);		// test emulator
		request.addTestDevice(RICHARD_HTC_DESIRE);			// Richard's MD5 code
		
		// TODO: MICHAEL DO THIS
		// ADD THE MD5-Hashed ID FOR MICHAEL
		// GET THIS BY FINDING THE "TO GET TEST ADS ON THIS DEVICE ...."
		// Filter for TAG = "Ads"

		Location loc = LocationSource.getLocationSource().currentLocation;
		if (loc != null)
			request.setLocation(loc);
		view.loadAd(request);
	}
}
