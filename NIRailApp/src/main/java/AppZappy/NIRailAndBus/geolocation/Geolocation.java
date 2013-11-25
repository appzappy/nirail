package AppZappy.NIRailAndBus.geolocation;

import com.google.android.maps.GeoPoint;

import android.location.Location;

/**
 * A geolocation object
 */
public class Geolocation
{
	private Location location = null;
	/**
	 * Get the latitude of this location
	 * @return
	 */
	public double getLatitude () {return location.getLatitude();}
	
	/**
	 * Get the longitude of this location
	 * @return
	 */
	public double getLongitude () { return location.getLongitude();}
	
	/**
	 * The accuracy of this location object
	 * @return Radius of accuracy in meters
	 */
	public float getAccuracy() { return location.getAccuracy();}
	
	/**
	 * Get the distance between 2 Geolocation objects
	 * @param otherLocation The other location to compare with
	 * @return The approximate distance between the 2 points in meters
	 */
	public float getDistance (Geolocation otherLocation)
	{
		return this.location.distanceTo(otherLocation.location);
	}
	
	/**
	 * Get a GeoPoint object for this location
	 * @return A GeoPoint object for this location. Used in Google's Map API MapView
	 */
	public GeoPoint getGeoPoint()
	{
		return new GeoPoint((int)(location.getLatitude()*1000000), (int) (location.getLongitude()*1000000));
	}
	
	/**
	 * Create a geolocation object
	 * @param latitude
	 * @param longitude
	 * @param accuracy
	 */
	public Geolocation(double latitude, double longitude, float accuracy)
	{
		Location l = new Location("Manual");
		l.setLatitude(latitude);
		l.setLongitude(longitude);
		l.setAccuracy(accuracy);
		this.location = l;
	}

	/**
	 * Create a geolocation object from an Android.Location object
	 * @param best The Android.Location object
	 */
	public Geolocation(Location best)
	{
		this.location = best;
	}

	/**
	 * Get the point positioned between a start and end point
	 * @param start The starting point
	 * @param end The ending point
	 * @param proportion The proportion from start to end. 0.0 to 1.0
	 * @return The point in the middle
	 */
	public static GeoPoint linearStretch(GeoPoint start, GeoPoint end, float proportion)
	{
		int lat = start.getLatitudeE6() + (int)(proportion * (end.getLatitudeE6() - start.getLatitudeE6()));
		int lon = start.getLongitudeE6() + (int)(proportion * (end.getLongitudeE6() - start.getLongitudeE6()));
		return new GeoPoint(lat, lon);
	}


	@Override
	public String toString()
	{
		return "GeoLocation: (" + getLatitude() + "," + getLongitude() +") Acc: " + getAccuracy();
	}
}
