package AppZappy.NIRailAndBus.data.db;

import java.util.ArrayList;
import java.util.HashMap;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import AppZappy.NIRailAndBus.data.collections.LinkingLocations;
import AppZappy.NIRailAndBus.data.enums.TransportType;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.timetable.Network;
import AppZappy.NIRailAndBus.util.timing.Timer;

public class SQLNetworkSource
{

	public Network createNetwork()
	{
		int timer_id = Timer.start_timer();
		
		SQLiteDatabase database = SQLiteManager.getDb();
		Network network = new Network();
		
		
		// get the aliases
		HashMap<Integer, ArrayList<String>> items = new HashMap<Integer, ArrayList<String>>();
		
		Cursor aliases = database.query(SQLFieldNames.LOCATION_ALIASES, null, null, null, null, null, null);
		
		int name_pos = aliases.getColumnIndex(SQLFieldNames.LOCATION_ALIASES_NAME);
		int location_id_pos = aliases.getColumnIndex(SQLFieldNames.LOCATION_ALIASES_LOCATION_ID);
		while (aliases.moveToNext())
		{
			int location_id = aliases.getInt(location_id_pos);
			String name = aliases.getString(name_pos);
		
			if (!items.containsKey(location_id))
				items.put(location_id, new ArrayList<String>());
			
			items.get(location_id).add(name);
		}
		aliases.close();
		
		
		
		// make the locations
		Cursor locations = database.query(SQLFieldNames.LOCATIONS, null, null, null, null, null, null);
		
		int pos_loc_id = locations.getColumnIndex(SQLFieldNames.LOCATIONS_ID);
		int pos_loc_lat = locations.getColumnIndex(SQLFieldNames.LOCATIONS_LATITUDE);
		int pos_loc_lon = locations.getColumnIndex(SQLFieldNames.LOCATIONS_LONGITUDE);
		int pos_loc_real = locations.getColumnIndex(SQLFieldNames.LOCATIONS_REAL_NAME);
		int pos_loc_type = locations.getColumnIndex(SQLFieldNames.LOCATIONS_TYPE);
		
		while (locations.moveToNext())
		{
			int id  = locations.getInt(pos_loc_id);
			String realName = locations.getString(pos_loc_real);
			double latitude = locations.getDouble(pos_loc_lat);
			double longitude = locations.getDouble(pos_loc_lon);
			TransportType type = TransportType.valueOf(locations.getString(pos_loc_type));
			
			Location loc = Location.create(id, realName, latitude, longitude, type, items.get(id).toArray(new String[0]));
			network.addLocation(loc);
		}
		locations.close();
		
		
		// load the network items
		Cursor touching_locations = database.query(SQLFieldNames.TOUCHING_LOCATION, null, null, null, null, null, null);
		
		int pos_loc_id1 = touching_locations.getColumnIndex(SQLFieldNames.TOUCHING_LOCATION_LOCATION1_ID);
		int pos_loc_id2 = touching_locations.getColumnIndex(SQLFieldNames.TOUCHING_LOCATION_LOCATION2_ID);
		
		LinkingLocations touching = network.touching_locations;
		while(touching_locations.moveToNext())
		{
			int id1 = touching_locations.getInt(pos_loc_id1);
			int id2 = touching_locations.getInt(pos_loc_id2);
			touching.addLink(id1, id2);
		}
		touching_locations.close();
		
		
		// load the swapping locations
		Cursor swapping_locations = database.query(SQLFieldNames.SWAP_LOCATIONS, null, null, null, null, null, null);
		
		pos_loc_id1 = touching_locations.getColumnIndex(SQLFieldNames.SWAP_LOCATIONS_LOCATION1_ID);
		pos_loc_id2 = touching_locations.getColumnIndex(SQLFieldNames.SWAP_LOCATIONS_LOCATION2_ID);
		
		LinkingLocations swapping = network.swapping_locations;
		while(swapping_locations.moveToNext())
		{
			int id1 = swapping_locations.getInt(pos_loc_id1);
			int id2 = swapping_locations.getInt(pos_loc_id2);
			swapping.addLink(id1, id2);
		}
		swapping_locations.close();
		
		
		
		database.close();
		
		Timer.report_timer("Network Loading", timer_id);
		Timer.stop_timer(timer_id);
		return network;  
	}

}
