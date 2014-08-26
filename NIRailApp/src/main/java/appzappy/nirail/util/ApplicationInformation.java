package appzappy.nirail.util;

import java.util.ArrayList;
import java.util.List;

import appzappy.nirail.R;
import appzappy.nirail.mode.UIInterfaceFactory;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;

/**
 * ApplicationInformation returns information about the application including is debug release and application version codes
 * @author Admin
 *
 */
public class ApplicationInformation
{
	
	protected final static List<Integer> DEBUG_SIGNATURES = getSignatures();

	/**
	 * Cache object for isDevelopmentBuild()
	 */
	private static Boolean _isDevelopmentBuild = null;

	
	/**
	 * Is the program a development build?
	 * @return True if in development, false if release
	 */
	public static boolean isDevelopmentBuild()
	{
		return isDevelopmentBuild(UIInterfaceFactory.getInterface().getAndroidContext());
	}

	/**
	 * Is the program a development build?
	 * @see http://whereblogger.klaki.net/2009/10/choosing-android-maps-api-key-at-run.html
	 * @param context A context object from the running application
	 * @return True if in development, false if release
	 */
	public static boolean isDevelopmentBuild(Context context)
	{
		if (_isDevelopmentBuild == null)
		{
			if (context == null)
				return true;
			try
			{
				_isDevelopmentBuild = false;
				Signature[] sigs = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
				for (int i = 0; i < sigs.length; i++)
				{
					if (DEBUG_SIGNATURES.contains(sigs[i].hashCode()))
					{
						Log.d("appzappy", "This is a debug build!");
						_isDevelopmentBuild = true;
						break;
					}
				}
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return _isDevelopmentBuild;
	}
	
	/**
	 * The debug signature for the application keystore.
	 * Taken from sigs[i].hashCode()
	 */
	private static List<Integer> getSignatures() {
		List<Integer> signature_hash = new ArrayList<Integer>();
		signature_hash.add(143750377); // Kurru old
		signature_hash.add(183732786); // Kurru 2012.02.25
		
		// TODO: MICHAEL ADD YOUR DETAILS HERE
		// Log.d("appzappy", "Sig Hash " + i + " : " + sigs[i].hashCode());
		
		return signature_hash;
	}


	/**
	 * Get the correct GoogleMapsAPI key for use
	 * @param context The application's context
	 * @return The correct key for the Google Maps API 
	 */
	public static String getGoogleMapsAPI(Context context)
	{
		if (isDevelopmentBuild(context))
			return context.getResources().getString(R.string.google_maps_api_key_DEBUG);
		else
			return context.getResources().getString(R.string.google_maps_api_key_RELEASE);
	}

	
	/**
	 * Get the version number of the application. eg 1.5
	 * @return String representing the version of the software. eg 1.5
	 */
	public static String getVersionNumber()
	{
		return getVersionNumber(UIInterfaceFactory.getInterface().getAndroidContext());
	}
	
	/**
	 * Get the version number of the application. eg 1.5
	 * @return String representing the version of the software. eg 1.5
	 */
	public static String getVersionNumber(Context context)
	{
		PackageInfo pi = null;
		if (context != null)
		{
			try
			{
				pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			}
			catch (NameNotFoundException excep)
			{
				throw new RuntimeException("Name not found exception");
			}
		}
		else
		{
			throw new RuntimeException("Context is null");
		}

		return pi.versionName;
	}
	
	
	/**
	 * Get the version number of the application. eg 81
	 * @return Int representing the version of the software. eg 81
	 */
	public static int getVersionCode()
	{
		return getVersionCode(UIInterfaceFactory.getInterface().getAndroidContext());
	}
	
	/**
	 * Get the version number of the application. eg 81
	 * @return Int representing the version of the software. eg 81
	 */
	public static int getVersionCode(Context context)
	{
		PackageInfo pi = null;
		if (context != null)
		{
			try
			{
				pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			}
			catch (NameNotFoundException excep)
			{
				throw new RuntimeException("NameNotFoundException has been thrown");
			}
		}
		else
		{
			throw new RuntimeException("Android Context not Set");
		}

		return pi.versionCode;
	}
	
	/**
	 * The URL of the application in the Android Market
	 */
	public static final String MARKET_URL = "market://details?id=AppZappy.NIRailAndBus";

	private ApplicationInformation(){}
}
