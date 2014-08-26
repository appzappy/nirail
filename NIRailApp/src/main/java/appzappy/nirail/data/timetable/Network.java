package appzappy.nirail.data.timetable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appzappy.nirail.data.collections.ConstrainedPriorityQueue;
import appzappy.nirail.data.collections.LinkingLocations;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.LocationDistance;
import appzappy.nirail.geolocation.Geolocation;



/**
 * A collection of Locations
 * 
 */
public class Network
{
	private List<Location> locations = new ArrayList<Location>();
	private Map<String, Location> aliasLookup = new HashMap<String, Location>();
	private Map<Integer, Location> idLookup = new HashMap<Integer, Location>();
	
	public LinkingLocations touching_locations = new LinkingLocations();

	public LinkingLocations swapping_locations = new LinkingLocations();
	
	/**
	 * Add a location to the network. Don't add additional aliases to a location
	 * after adding it to a Network
	 * 
	 * @param location The location to add
	 */
	public void addLocation(Location location)
	{
		locations.add(location);

		// handle the alias lookup map
		aliasLookup.put(location.getRealName(), location);
		int count = location.countAliases();
		for (int i = 0; i < count; i++)
		{
			aliasLookup.put(location.getAlias(i), location);
		}
		idLookup.put(location.get_id(), location);
	}

	/**
	 * Get all the locations that contain an alias with the "substring"
	 * parameter as a substring
	 * 
	 * @param substring The substring to find as part of an alias
	 * @return The locations containing 'substring' as a substring. OR empty
	 *         array if substring is empty
	 */
	public List<Location> getMatchingLocations(String substring)
	{
		if (substring.equals(""))
			return new ArrayList<Location>();

		substring = substring.toLowerCase();
		
		List<Location> loc = new ArrayList<Location>();
		for (Location l: locations)
		{
			if(l.isSubstringOfAlias(substring))
			{
				loc.add(l);
			}
		}
		return loc;
	}

	/**
	 * Get the number of locations in this network
	 * 
	 * @return Number of locations in the network
	 */
	public int countLocation()
	{
		return locations.size();
	}

	/**
	 * Does the network contain a Location with the alias
	 * 
	 * @param alias The alias to check
	 * @return True if the alias is in the network, false otherwise
	 */
	public boolean containsLocation(String alias)
	{
		return get(alias) != null;
	}

	/***
	 * Get the location from a string alias
	 * 
	 * @param alias The string alias of a location
	 * @return The location. Null if not found
	 */
	public Location get(String alias)
	{
		Location found = aliasLookup.get(alias);
		if (found == null)
			throw new NullPointerException("Location not found. Alias: " + alias);
		return found;
	}
	
	/**
	 * Get the location from its ID
	 * @param location_id The unique id number for this location object
	 * @return The location object requested
	 */
	public Location get(int location_id)
	{
		Location found = idLookup.get(location_id);
		if (found == null)
			throw new NullPointerException("Location ID not found. ID: " + location_id);
		return found;
	}


	/**
	 * Get a copy of all the locations in this network
	 * @return An array of all the location objects
	 */
	public List<Location> getAllLocations()
	{
		return new ArrayList<Location>(this.locations);
	}

	/**
	 * Get the nearby stations sorted with closest first
	 * @param number The number of stations to get
	 * @param currentLocation Current location
	 * @return Ascending list of nearest stations
	 */
	public List<Location> getNearbyLocations(int number, Geolocation currentLocation)
	{
		List<Location> all = getAllLocations();
		List<LocationDistance> sorted = Location.getLocationDistances(all, currentLocation);
		
		
		// using the queue to efficiency select the nearest stations
		Comparator<LocationDistance> comparator = new Network.LocationDistanceDesc();
		ConstrainedPriorityQueue<LocationDistance> queue = new ConstrainedPriorityQueue<LocationDistance>(number, comparator);
		
		for (LocationDistance ld : sorted)
		{
			queue.add(ld);
		}
		
		List<Location> loc = new ArrayList<Location>(queue.size());
		for (LocationDistance ld : queue)
		{
			loc.add(ld.location);
		}
		
		return loc;
	}
	public static class LocationDistanceDesc implements Comparator<LocationDistance>
	{
		public int compare(LocationDistance o1, LocationDistance o2)
		{
			int starting1 = o1.distance;
			int starting2 = o2.distance;
			return starting2 - starting1;
		}
	}
	
}
