package AppZappy.NIRailAndBus.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.db.SQLFieldNames;
import AppZappy.NIRailAndBus.data.db.SQLiteManager;
import AppZappy.NIRailAndBus.data.enums.TransportType;
import AppZappy.NIRailAndBus.data.timetable.Network;
import AppZappy.NIRailAndBus.geolocation.Geolocation;
import AppZappy.NIRailAndBus.util.timing.Timer;

import com.google.android.maps.GeoPoint;



/**
 * This class contains location data for a stop in the system
 * 
 */
public class Location
{
	private int _id = 0;
	public int get_id()
	{
		return _id;
	}
	
	private String _realName = "";

	/**
	 * Get the displayed name for this stop
	 * 
	 * @return The proper name for this location
	 */
	public String getRealName()
	{
		return _realName;
	}

	private double _latitude = 0.0;
	private double _longtitude = 0.0;

	/**
	 * Get the Latitude of this location
	 * 
	 * @return The latitude in double format
	 */
	public double getLatitude()
	{
		return _latitude;
	}

	/**
	 * Get the Longitude of this location
	 * 
	 * @return The longitude in double format
	 */
	public double getLongitude()
	{
		return _longtitude;
	}
	
	/**
	 * Get this location as a GeoPoint
	 * @return
	 */
	public GeoPoint getGeoPoint()
	{
		return new GeoPoint((int)(_latitude*1000000), (int)(_longtitude*1000000));
	}
	
	/**
	 * Get this location's geolocation
	 */
	public Geolocation getGeoLocation()
	{
		return new Geolocation(_latitude, _longtitude, 0.0f);
	}
	
	
	private TransportType _type = TransportType.Metro;

	/**
	 * Get the location type of this location
	 * 
	 * @return A LocationType object. Such as Train, Bus or TrainBus
	 */
	public TransportType getLocationType()
	{
		return _type;
	}

	private String[] _aliases = new String[0];

	/**
	 * Is this an alias of this location
	 * 
	 * @param The String which may be an alias
	 * @return True if the String is either the 'RealName' or a stored Alias
	 */
	public boolean isAliasOf(String name)
	{
		if (_realName.equalsIgnoreCase(name))
			return true;
		for (String s : _aliases)
		{
			if (s.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	/**
	 * Does this location have a name containing substring?
	 * @param substring The substring to compare. <b>MUST BE LOWERCASE</b>
	 * @return True if is a substring value
	 */
	public boolean isSubstringOfAlias(String substring)
	{
		if (_realName.toLowerCase().contains(substring))
			return true;
		for(String s : _aliases)
		{
			if (s.toLowerCase().contains(substring))
				return true;
		}
		return false;
	}

	/**
	 * Get the number of aliases for this Location
	 * 
	 * @return number of aliases. 0-n
	 */
	public int countAliases()
	{
		return _aliases.length;
	}

	/**
	 * Get an alias at a specified index
	 * 
	 * @param index The index to get
	 * @return The alias at provided index
	 */
	public String getAlias(int index)
	{
		return _aliases[index];
	}

	/**
	 * Get a string containing all aliases for this Location
	 * @return
	 */
	public String getAllAliasString(char separater)
	{
		if (_aliases.length == 0)
			return "";
		String out = _aliases[0];
		for (int i=1;i<_aliases.length;i++)
			out += " " + separater + " " + _aliases[i];
		return out;
	}
	
	/**
	 * Get a string containing the real name and aliases separated by sep
	 * @param sep The character to separate the items
	 * @return if sep==; then "Real Name ; Alias1 ; Alias2 ; Alias3"
	 */
	public String getFullString(char sep)
	{
		String out = this.getRealName();
		out += " " + sep + " ";
		out += this.getAllAliasString(sep);
		return out;
	}
	

	
	/**
	 * Get the distance between these 2 locations
	 * @param location The other location
	 * @return The distance in meters between the 2 locations
	 */
	public float getDistanceBetweenLocations(Location location)
	{
		return getGeoLocation().getDistance(location.getGeoLocation());
	}
	
	
	private Location(int id, String realName, double latitude, double longitude,
			TransportType type, String[] aliases)
	{
		_id = id;
		_realName = realName;
		_latitude = latitude;
		_longtitude = longitude;
		_type = type;
		_aliases = aliases;
	}
	
	/**
	 * Create a new Location Object
	 * @param realName The real name of this location
	 * @param latitude The latitude of the location
	 * @param longitude The longitude of the location
	 * @param type The type of this location. Example: Train, Bus or TrainBus
	 * @param aliases A set of String aliases for this location
	 */
	public static Location create (int id, String realName, double latitude, double longitude,
			TransportType type, String[] aliases)
	{
		return new Location(id, realName, latitude, longitude,
				type, aliases);
	}

	private List<Location> _linkedLocations = null;
	/**
	 * Get all locations that can be reached from this Location with 1 train journey
	 * @return A list of locations reachable from this Location.
	 */
	public List<Location> getReachableLocations()
	{
		if (_linkedLocations != null)
			return _linkedLocations;
		
		int timing_id = Timer.start_timer();
		SQLiteDatabase db =  SQLiteManager.getDb();
		
		
		
		String table = SQLFieldNames.STOPS;
		String[] what = new String[] {SQLFieldNames.STOPS_ROUTE_ID};
		String where = SQLFieldNames.STOPS_LOCATION_ID + "='" + this._id + "'";
		String group = SQLFieldNames.STOPS_ROUTE_ID;
		
		List<Integer> route_ids = new ArrayList<Integer>(256);
		
		Cursor route_id_select = db.query(table, what, where, null, group, null, null);
		while(route_id_select.moveToNext())
		{
			// route ids will be unique
			Integer route_id = route_id_select.getInt(0);
			route_ids.add(route_id);
		}
		route_id_select.close();
		
		
		// build the new selection "where"
		StringBuilder sb = new StringBuilder();
		
		if (route_ids.size() > 0)
		{
			// add the 1st item
			sb.append(route_ids.get(0));
		}
		for(int i=1;i<route_ids.size();i++)
		{
			sb.append(", ");
			sb.append(route_ids.get(i));
		}
		where = sb.toString();
				
		
		table = SQLFieldNames.STOPS;
		what = new String[] {SQLFieldNames.STOPS_LOCATION_ID};
		where = SQLFieldNames.STOPS_ROUTE_ID + " IN (" + where + ")";
		group = SQLFieldNames.STOPS_LOCATION_ID;
		
		Set<Integer> location_ids = new HashSet<Integer>(64);
		
		Cursor location_id_select = db.query(table, what, where, null, group, null, null);
		while(location_id_select.moveToNext())
		{
			// route ids will be unique
			Integer location_id = location_id_select.getInt(0);
			location_ids.add(location_id);
		}
		location_id_select.close();
		
		// close the db
		db.close();
		
		// remove the reference to current location
		location_ids.remove(this._id);
		
		
		// convert the ids to lcoation objects
		List<Location> locations = new ArrayList<Location>(location_ids.size());
		Network network = LoadData.getNetwork();
		for(Integer location_id: location_ids)
		{
			Location loc = network.get(location_id);
			locations.add(loc);
		}
		
		// save the reference for cache purposes
		_linkedLocations = locations; 
		
		// end the timer
		Timer.report_timer("Linking locations for " + this._realName, timing_id);
		Timer.stop_timer(timing_id);
		
		return _linkedLocations;
	}
	
	/**
	 * Get all locations that can be reached from this Location with 1 train journey
	 * @return A list of locations reachable from this Location. Sorted by real name
	 */
	public List<Location> getReachableLocationsSorted()
	{
		List<Location> locations = getReachableLocations();
		sortLocationsByName(locations);
		return locations;
	}

		
	/**
	 * Used to sort a set of locations based on their RealName
	 */
	private class LocationSortName implements Comparator<Location> 
	{
		public int compare(Location o1, Location o2)
		{
			return o1.getRealName().compareTo(o2.getRealName());
		}
		
		@Override
		public String toString()
		{
			return "Location Sort Name";
		}
	}
	
	/**
	 * Sort a collection of location objects by name
	 * @param locations The location objects to sort
	 */
	public static void sortLocationsByName(List<Location> locations)
	{
		if (locations.size() > 1)
		{
			Location l = locations.get(0);
			Collections.sort(locations, l.new LocationSortName());
		}
	}
	
	
	/**
	 * Get the locations' distance from a central location
	 * @param locations The locations
	 * @param currentLocation The central location
	 * @return
	 */
	public static List<LocationDistance> getLocationDistances(List<Location> locations, Geolocation currentLocation)
	{
		if (locations == null)
		{
			return new ArrayList<LocationDistance>();	
		}
		
		int numItems = locations.size();
		if (numItems == 0)
			return new ArrayList<LocationDistance>();
		
		List<LocationDistance> locationDistances = new ArrayList<LocationDistance>(numItems);
		for(int i=0; i<numItems; i++)
		{
			locationDistances.add(new LocationDistance(locations.get(i), currentLocation));
		}
		return locationDistances;
	}
	
	
	
	@Override
	public String toString()
	{
		return "Location of stop";
	}
	
	/**
	 * Hashcode is the location ID
	 */
	@Override
	public int hashCode()
	{
		return _id;
	};
	
	/**
	 * Get a list of location objects from a list of location ids
	 * @param network The network to use for lookups
	 * @param locations The UID's of location objects
	 * @return List of locations
	 */
	public static List<Location> getLocationList(Network network, List<Integer> locations)
	{
		List<Location> output = new ArrayList<Location>(locations.size());
		for(int i=0;i<locations.size();i++)
		{
			Location loc = network.get(locations.get(i));
			output.add(loc);
		}
		return output;
	}
	
}
