package AppZappy.NIRailAndBus.geolocation;

import AppZappy.NIRailAndBus.events.Event;
import AppZappy.NIRailAndBus.userdata.Settings;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Location source for the application
 * 
 * @author Kurru
 * 
 */
public class LocationSource
{
	private LocationSource()
	{
	}

	/**
	 * Singleton object
	 */
	private static LocationSource _singleton = new LocationSource();

	/**
	 * Get the instance of the location source
	 * 
	 * @return The singleton instance of the LocationSource object
	 */
	public static LocationSource getLocationSource()
	{
		return _singleton;
	}


	private LocationManager manager = null;
	private LocationListener coarse = null;
	private LocationListener gps = null;


	/**
	 * Start the location manager
	 * 
	 * @param manager The location manager to use
	 */
	public void start(LocationManager manager)
	{
		stop();

		this.manager = manager;

		Location lastKnown = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnown == null)
		{
			Geolocation cityHall = Settings.getCityHall();
			Location defaultLocation = new Location("Hand written");
			defaultLocation.setAccuracy(1);
			defaultLocation.setLatitude(cityHall.getLatitude());
			defaultLocation.setLongitude(cityHall.getLongitude());

			setBestCurrent(defaultLocation);
		}
		else
		{
			setBestCurrent(lastKnown);
		}

		coarse = new LocationListener()
		{
			public void onLocationChanged(Location location)
			{
				// Called when a new location is found by the network location
				// provider.
				newLocationEvent(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras)
			{
			}

			public void onProviderEnabled(String provider)
			{
			}

			public void onProviderDisabled(String provider)
			{
			}
		};

		// gps = coarse;

		float minimumDistance = 10f; // meters
		long minimumTime = 5 * 1000; // milliseconds

		// Register the listener with the Location Manager to receive location
		// updates
		if (coarse != null) {
			try {
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minimumTime, minimumDistance, coarse);
			}
			catch (IllegalArgumentException e) {
				// Failed to get location updates from NETWORK_PROVIDER
			}
		}
	}

	private void newLocationEvent(Location location)
	{
		if (isBetterLocation(location, currentLocation))
		{
			setBestCurrent(location);
			newLocation.triggerEvent(this.getCurrentLocation());
		}
	}

	/**
	 * Location has been updated. Trigger data contains new current location
	 */
	public Event newLocation = new Event();

	/**
	 * Stop listening for location updates
	 */
	public void stop()
	{
		if (coarse != null)
		{
			manager.removeUpdates(coarse);
			coarse = null;
		}
		if (gps != null)
		{
			manager.removeUpdates(gps);
			gps = null;
		}
		manager = null;
	}

	/**
	 * The most recently received location data
	 */
	public Location currentLocation = null;

	/**
	 * The current best location
	 * 
	 * @param best The best location object
	 */
	private void setBestCurrent(Location best)
	{
		currentLocation = best;
		_currentLocation = new Geolocation(best);
	}

	private Geolocation _currentLocation = null;

	/**
	 * Get the current location
	 */
	public Geolocation getCurrentLocation()
	{
		return _currentLocation;
	}


	private static final int MAX_TIME_DIFFERENCE = 1000 * 30; // if 2 updates
																// are 30seconds
																// apart, then
																// the newer one
																// is better


	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location The new Location that you want to evaluate
	 * @param currentBestLocation The current Location fix, to which you want to
	 *            compare the new one
	 * @see http 
	 *      ://developer.android.com/guide/topics/location/obtaining-user-location
	 *      .html
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation)
	{
		if (currentBestLocation == null)
		{
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > MAX_TIME_DIFFERENCE;
		boolean isSignificantlyOlder = timeDelta < -MAX_TIME_DIFFERENCE;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer)
		{
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		}
		else if (isSignificantlyOlder)
		{
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate)
		{
			return true;
		}
		else if (isNewer && !isLessAccurate)
		{
			return true;
		}
		else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two providers are the same
	 * 
	 * @see http 
	 *      ://developer.android.com/guide/topics/location/obtaining-user-location
	 *      .html
	 */
	private boolean isSameProvider(String provider1, String provider2)
	{
		if (provider1 == null)
		{
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Start tracking GPS location data
	 */
	public void startGPS()
	{
		gps = new LocationListener()
		{
			public void onLocationChanged(Location location)
			{
				// Called when a new location is found by the network location
				// provider.
				newLocationEvent(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras)
			{
			}

			public void onProviderEnabled(String provider)
			{
			}

			public void onProviderDisabled(String provider)
			{
			}
		};

		float minimumDistance = 10f; // meters
		long minimumTime = 2 * 1000; // milliseconds


		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minimumTime, minimumDistance, gps);
	}

	/**
	 * Stop tracking GPS location data
	 */
	public void stopGPS()
	{
		if (gps != null)
		{
			if (manager != null)
				manager.removeUpdates(gps);
			gps = null;
		}
	}

	@Override
	public String toString()
	{
		return "LocationSource: Current Location: " + _currentLocation.toString();
	}

}
