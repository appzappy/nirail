package AppZappy.NIRailAndBus.data.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import AppZappy.NIRailAndBus.data.collections.DataPointer;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.data.model.Stop;
import AppZappy.NIRailAndBus.data.timetable.Service;
import AppZappy.NIRailAndBus.util.timing.TimeFormatter;

public class SQLiteHelper
{

	public static List<DataPointer<Service>> get_Services(SQLiteDatabase db, int timetable_id)
	{
		List<DataPointer<Service>> ids = new ArrayList<DataPointer<Service>>();
		
		String where = SQLFieldNames.SERVICES_TIMETABLE_ID + " = '"+timetable_id+"'";
		Cursor data = db.query(SQLFieldNames.SERVICES, new String[] {SQLFieldNames.SERVICES_ID}, where, null, null, null, null);
		
		while(data.moveToNext())
		{
			ids.add(new DataPointer<Service>(Service.class, data.getInt(0)));
		}
		data.close();
		return ids;
	}
	
	public static List<DataPointer<Stop>> get_Stops(SQLiteDatabase db, int route_id)
	{
		List<DataPointer<Stop>> ids = new ArrayList<DataPointer<Stop>>();
		
		String where = SQLFieldNames.STOPS_ROUTE_ID + " = '"+route_id+"'";
		Cursor data = db.query(SQLFieldNames.STOPS, new String[] {SQLFieldNames.STOPS_ID}, where, null, null, null, null);
		
		while(data.moveToNext())
		{
			ids.add(new DataPointer<Stop>(Stop.class, data.getInt(0)));
		}
		data.close();
		return ids;
	}
	
	public static Map<Integer, List<DataPointer<Stop>>> get_Stops(SQLiteDatabase db, Set<Integer> route_ids)
	{
		final Map<Integer, List<DataPointer<Stop>>> stops = new HashMap<Integer, List<DataPointer<Stop>>>(route_ids.size());
		
		if (route_ids.size() == 0)
			return stops;
		
		final Iterator<Integer> iter = route_ids.iterator();
		
		Integer current = 0;
		
		final int capacity = 100 + 10 * route_ids.size();
		
		final StringBuilder sb = new StringBuilder(capacity);
		sb.append(SQLFieldNames.STOPS_ROUTE_ID);
		sb.append(" IN (");
		current = iter.next();
		sb.append(current);
		stops.put(current, new ArrayList<DataPointer<Stop>>(16));
		while(iter.hasNext())
		{
			sb.append(", ");
			current = iter.next();
			stops.put(current, new ArrayList<DataPointer<Stop>>(16));
			sb.append(current);
		}
		sb.append(')');
		
		final String where = sb.toString();
		final Cursor data = db.query(SQLFieldNames.STOPS, new String[] {SQLFieldNames.STOPS_ID, SQLFieldNames.STOPS_ROUTE_ID}, where, null, null, null, null);
		
		while(data.moveToNext())
		{
			Integer stop_id = data.getInt(0);
			Integer route_id = data.getInt(1);
			stops.get(route_id).add(new DataPointer<Stop>(Stop.class, stop_id));
		}
		data.close();
		return stops;
	}
	
	public static List<DataPointer<Route>> get_Routes(SQLiteDatabase db, int service_id)
	{
		List<DataPointer<Route>> ids = new ArrayList<DataPointer<Route>>();
		
		String where = SQLFieldNames.ROUTES_SERVICE_ID + " = '"+service_id+"'";
		Cursor data = db.query(SQLFieldNames.ROUTES, new String[] {SQLFieldNames.ROUTES_ID}, where, null, null, null, null);
		
		while(data.moveToNext())
		{
			ids.add(new DataPointer<Route>(Route.class, data.getInt(0)));
		}
		data.close();
		
		return ids;
	}
	
	public static int get_default_timetable_id()
	{
		SQLiteDatabase db =  SQLiteManager.getDb();
		String maxID = "MAX("+SQLFieldNames.TIMETABLE_ID+")"; 
		Cursor data = db.query(SQLFieldNames.TIMETABLES, new String[] {maxID}, null, null, null, null, null);
		
		data.moveToNext();

		int timetable_id = data.getInt(0);
		data.close();
		db.close();
		return timetable_id;
	}

	
	/**
	 * Get the routes at a station
	 * 
	 * @param db Database to retrieve data from
	 * @param locations The locations to access
	 * @return Outer Integer = Location ID. Inner INTEGER = ROUTE ID. RouteInformation = Set of stops
	 */
	public static Map<Integer,Map<Integer, RouteInformation>> get_routes_at_station(SQLiteDatabase db, List<Location> locations)
	{
		Map<Integer,Map<Integer, RouteInformation>> location_routes = new HashMap<Integer, Map<Integer, RouteInformation>>(locations.size());

		if (locations.size() == 0)
			return location_routes;
		
		for(Location l: locations)
		{
			location_routes.put(l.get_id(), new HashMap<Integer, RouteInformation>());
		}
		
		
		String where = SQLFieldNames.STOPS_LOCATION_ID + " IN (";
		where += locations.get(0).get_id();
		for (int i=1;i<locations.size();i++)
		{
			where += ", ";
			where += locations.get(i).get_id();
		}
		where += ')';
		final Cursor data = db.query(SQLFieldNames.STOPS, null, where, null, null, null, null);
		
		final int pos_id = data.getColumnIndex(SQLFieldNames.STOPS_ID);
		final int pos_location_id = data.getColumnIndex(SQLFieldNames.STOPS_LOCATION_ID);
		final int pos_time = data.getColumnIndex(SQLFieldNames.STOPS_TIME);
		final int pos_pickup = data.getColumnIndex(SQLFieldNames.STOPS_PICKUP);
		final int pos_dropoff = data.getColumnIndex(SQLFieldNames.STOPS_DROPOFF);
		final int pos_route_id = data.getColumnIndex(SQLFieldNames.STOPS_ROUTE_ID);
		
		
		while(data.moveToNext())
		{
			final Integer route_id = data.getInt(pos_route_id);
			final Integer location_id = data.getInt(pos_location_id);
			final short time = data.getShort(pos_time);
			final short proper_time = TimeFormatter.timeFromPlainTime(time);
			final int stop_id = data.getInt(pos_id);
			final boolean pickup = data.getInt(pos_pickup) == 1;
			final boolean dropoff = data.getInt(pos_dropoff) == 1;
			
			Map<Integer, RouteInformation> routes = location_routes.get(location_id);
			
			if (routes.containsKey(route_id))
			{
				// invalid for optimisations
				// since this route has already existed at this location
				// ie route stops at this location twice
				routes.get(route_id).add(new StopDetail(stop_id, proper_time, pickup, dropoff));
			}
			else
			{
				// valid for optimisations
				RouteInformation information = new RouteInformation(route_id);
				information.add(new StopDetail(stop_id, proper_time, pickup, dropoff));
				routes.put(route_id, information);
			}

		}

		data.close();
		return location_routes;
	}
	
	public static Map<Integer, RouteInformation> get_routes_at_station(SQLiteDatabase db, Location location)
	{
		Map<Integer, RouteInformation> routes = new HashMap<Integer, RouteInformation>(256);
		
		
		String where = SQLFieldNames.STOPS_LOCATION_ID + "=" + location.get_id();
		Cursor data = db.query(SQLFieldNames.STOPS, null, where, null, null, null, null);
		
		final int pos_id = data.getColumnIndex(SQLFieldNames.STOPS_ID);
		final int pos_time = data.getColumnIndex(SQLFieldNames.STOPS_TIME);
		final int pos_pickup = data.getColumnIndex(SQLFieldNames.STOPS_PICKUP);
		final int pos_dropoff = data.getColumnIndex(SQLFieldNames.STOPS_DROPOFF);
		final int pos_route_id = data.getColumnIndex(SQLFieldNames.STOPS_ROUTE_ID);
		
		
		while(data.moveToNext())
		{
			final Integer route_id = data.getInt(pos_route_id);
			final short time = data.getShort(pos_time);
			final short proper_time = TimeFormatter.timeFromPlainTime(time);
			final int stop_id = data.getInt(pos_id);
			final boolean pickup = data.getInt(pos_pickup) == 1;
			final boolean dropoff = data.getInt(pos_dropoff) == 1;
			
			
			
			if (routes.containsKey(route_id))
			{
				// invalid for optimisations
				// since this route has already existed at this location
				// ie route stops at this location twice
				routes.get(route_id).add(new StopDetail(stop_id, proper_time, pickup, dropoff));
			}
			else
			{
				// valid for optimisations
				RouteInformation information = new RouteInformation(route_id);
				information.add(new StopDetail(stop_id, proper_time, pickup, dropoff));
				routes.put(route_id, information);
			}

		}

		data.close();
		return routes;
	}
	
	public static class RouteInformation extends ArrayList<StopDetail>
	{
		public RouteInformation(Integer route_id)
		{
			this.route_id = route_id;
		}
		
		private RouteInformation(Integer route_id, ArrayList<StopDetail> old_set)
		{
			super(old_set);
			this.route_id = route_id;
		}
		public Integer route_id = 0;
		
		/**
		 * A serial UID?
		 */
		private static final long serialVersionUID = -1292498185294428344L;
		
		public List<Integer> get_Stop_Ids()
		{
			List<Integer> out = new ArrayList<Integer>();
			for(StopDetail detail : this)
			{
				out.add(detail.stop_id);
			}
			return out;
		}
		
		public StopDetail getStopDetail(int stop_id)
		{
			for(StopDetail detail : this)
			{
				if (detail.stop_id == stop_id)
					return detail;
			}
			return null;
		}
		
		public void removeLastTouching()
		{
			for (int i=0;i<this.size()-1;i++)
			{
				StopDetail current = this.get(i);
				StopDetail next = this.get(i+1);
				if (current.stop_id == next.stop_id - 1)
				{
					this.remove(i+1);
					i--;
				}
			}
		}
		public void removeEarlyTouching()
		{
			for (int i=0;i<this.size()-1;i++)
			{
				StopDetail current = this.get(i);
				StopDetail next = this.get(i+1);
				if (current.stop_id == next.stop_id - 1)
				{
					this.remove(i);
					i--;
				}
			}
		}
		
		
		public RouteInformation clone()
		{
			return new RouteInformation(this.route_id, this);
		}
	}
	public static class StopDetail
	{
		public StopDetail(int stop_id, short time, boolean pickup, boolean dropoff)
		{
			this.stop_id = stop_id;
			this.time = time;
			this.pickup = pickup;
			this.dropoff = dropoff;
		}
		public int stop_id = 0;
		public short time = 0;
		public boolean pickup = false;
		public boolean dropoff = false;
	}
	
}
