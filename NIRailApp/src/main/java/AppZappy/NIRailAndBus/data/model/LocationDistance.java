package AppZappy.NIRailAndBus.data.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import AppZappy.NIRailAndBus.geolocation.Geolocation;
import AppZappy.NIRailAndBus.userdata.Settings;

public class LocationDistance
{
	private static DecimalFormat df = new DecimalFormat();
	private static boolean set = false;
	
	public Location location = null;
	public int distance = 0;
	
	public LocationDistance(Location l, Geolocation target)
	{
		if (!set)
		{
			set = true;
			df.setMaximumFractionDigits(1);
			df.setMinimumFractionDigits(1);
		}
		
		location = l;
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		Geolocation loc = new Geolocation(lat,lon, 0);
		distance = (int) target.getDistance(loc);
	}
	
	/**
	 * Get the distance formatted to 2DP
	 * @return
	 */
	public String getFormatedDistance()
	{
		if (distance < 1000) // less than 1k meters away. output as meters
			return distance + "m";
		
		String unit = "";
		double scalar = 0;
		if (Settings.isDistanceKM()) // pick and scale for km
		{
			unit = "km";
			scalar = ((double) distance) / 1000.0;
			
		}
		else // pick and scale for miles
		{
			unit = "miles";
			scalar = ((double) distance) / 1609.344;
		}
		
		if (scalar < 10) // output like 9.4miles
		{
			return df.format(scalar) + unit;
		}
		
		return ((int) scalar) + unit;
	}
	
	/**
	 * Sort a collection of locationDistances by their distance values
	 * @param locations_distances The locations to sort
	 * @return
	 */
	public static void sortLocationsByDistance(List<LocationDistance> locations_distances)
	{
		if (locations_distances.size() == 0)
			return;
		LocationDistance sortingItem = locations_distances.get(0);
		Collections.sort(locations_distances, sortingItem.new SortDistance());
	}
	
	/**
	 * Sort a collection of locationDistances by their location names
	 * @param locations_distances The locations to sort
	 * @return
	 */
	public static void sortLocationsDistancesByName(List<LocationDistance> locations_distances)
	{
		if (locations_distances.size() == 0)
			return;
		LocationDistance sortingItem = locations_distances.get(0);
		Collections.sort(locations_distances, sortingItem.new SortName());
	}
	
	
	private class SortDistance implements Comparator<LocationDistance> 
	{
		public int compare(LocationDistance o1, LocationDistance o2)
		{
			float diff = o1.distance - o2.distance;
			
			float delta = 1.0f;
			
			if (diff < delta)
				return -1;
			if (diff > delta)
				return 1;
			return 0;
		}
	}
	
	private class SortName implements Comparator<LocationDistance>
	{
		public int compare(LocationDistance o1, LocationDistance o2)
		{
			Location o1_location = o1.location;
			Location o2_location = o2.location;
			
			int nameComp = o1_location.getRealName().compareTo(o2_location.getRealName());
			
			return nameComp;
		}
	}
	
	public static List<Location> getLocations(List<LocationDistance> locationDistances)
	{
		int count = locationDistances.size();
		List<Location> out = new ArrayList<Location>(count);
		for(int i=0;i<count;i++)
		{
			out.add(locationDistances.get(i).location);
		}
		return out;
	}
	
	@Override
	public String toString()
	{
		return "Location Distance";
	}
}
