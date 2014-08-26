package appzappy.nirail.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import appzappy.nirail.data.collections.DataPointer;
import appzappy.nirail.data.db.SQLFieldNames;
import appzappy.nirail.data.db.SQLiteHelper;
import appzappy.nirail.data.db.SQLiteManager;
import appzappy.nirail.data.enums.DayOfWeek;
import appzappy.nirail.data.enums.TransportType;
import appzappy.nirail.data.timetable.Service;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.util.L;
import appzappy.nirail.util.timing.TimeFormatter;
import appzappy.nirail.util.timing.Timer;


/**
 * A set of stops that makes up a particular service instance. 
 * i.e Represents a single journey by a bus or train
 *
 */
public class Route implements IID
{
	public static final int LOAD_STOPS__IDS = 0;
	public static final int LOAD_STOPS__ALL = 1;
	public static final int LOAD_STOPS__NULL = 2;
	
	private int _id = 0;
	/**
	 * Get the id of this route
	 */
	public int get_id()
	{
		return _id;
	}
	
	private int _service_id = 0;
	/**
	 * The id of the service this route belongs to
	 * @return
	 */
	public int get_service_id()
	{
		return _service_id;
	}
	
	/**
	 * Is this route object fully loaded
	 */
	private boolean _fully_loaded = false;
	
	
	
	public Service get_service()
	{
		return DataPointer.get_Object(Service.class, _service_id);
	}
	
	/**
	 * Load a route from the default database. 
	 * @param route_id The id of the route to load
	 * @return
	 */
	public static DataPointer<Route> from_SQL(int route_id)
	{
		return from_SQL(route_id, Route.LOAD_STOPS__IDS);
	}
	
	/**
	 * Load a route from the default database. 
	 * @param route_id The id of the route to load
	 * @param loadStops Should all stops objects be loaded from the database too?
	 * @return
	 */
	public static DataPointer<Route> from_SQL(int route_id, int loadStops)
	{
		// this is the return value for the requested data
		DataPointer<Route> dataPointer = new DataPointer<Route>(Route.class, route_id);
		
		Route data = DataPointer.get_Object_If_Exists(Route.class, route_id);
		if (data != null) // object is already in the cache
		{
			if (loadStops == Route.LOAD_STOPS__ALL)
			{
				// all stops must be loaded as well
				if (data._fully_loaded)
				{
					// stop data is loaded
					return dataPointer;
				}
			}
			else
			{
				// dont need to have stop data loaded
				return dataPointer;
			}
		}
		
		// load the data from the database
		SQLiteDatabase db = SQLiteManager.getDb();
		String where = SQLFieldNames.ROUTES_ID + "=" + route_id;
		from_SQL(db, where, loadStops);
		db.close();
		
		return dataPointer;
	}


	/**
	 * Load a set of route objects from the database. 
	 * @param db The database to load the objects from
	 * @param route_ids The ids to load the data from
	 * @param loadStops Should all the stops within the stops be loaded as well?
	 */
	public static void from_SQL(SQLiteDatabase db, Set<Integer> route_ids, int loadStops)
	{
		int timer_id = Timer.start_timer();
		List<Integer> cloned = new ArrayList<Integer>(route_ids.size());
		
		for (Integer i : route_ids)
		{
			Route route = DataPointer.get_Object_If_Exists(Route.class, i);
			if (route != null)
			{
				// r is set. if r._full_loaded != true while loadstops == true add
				if (!route._fully_loaded && loadStops == LOAD_STOPS__ALL)
				{
					cloned.add(i);
				}
			}
			else
			{
				cloned.add(i);
			}
		}
		
		// dont load if no items to load
		if (cloned.size() == 0)
			return;
		
		int stringBuffer = cloned.size() * (2 + 6) + 100;
		
		StringBuilder builder = new StringBuilder(stringBuffer);
		
		builder.append(SQLFieldNames.ROUTES_ID);
		builder.append(" IN (");
		
		// start building the IN portion
		builder.append(cloned.get(0));
		
		for (int i=1;i<cloned.size();i++)
		{
			builder.append(", ");
			Integer value = cloned.get(i);
			builder.append(value);
		}
		
		// end the in ()'s
		builder.append(')');
		
		String where = builder.toString();
		from_SQL(db, where, loadStops);
		Timer.report_timer("Loaded routes ("+route_ids.size()+") Stops: " + loadStops, timer_id);
	}
	
	
	public static List<Route> from_SQL_Starting_Between(short start, short end, DayOfWeek day)
	{
		SQLiteDatabase db = SQLiteManager.getDb();
		
		String dayTerm = SQLFieldNames.getDayString(day) + "=1";
		String where = SQLFieldNames.ROUTES_START_TIME + " >= " + TimeFormatter.basicStringFromTime(start) + " AND " + SQLFieldNames.ROUTES_START_TIME + "<=" + TimeFormatter.basicStringFromTime(end) + " AND " + dayTerm;

		List<Route> routes = from_SQL(db, where, Route.LOAD_STOPS__ALL);
		
		// remove the non-current-running routes
		List<Route> output = new ArrayList<Route>(routes.size());
		IUIInterface d = UIInterfaceFactory.getInterface();
		for(Route r : routes)
		{
			if (TimeFormatter.isValidDate(d.getCurrentDayOfWeek(), TimeFormatter.getTodaysDateNumber(), day, r._start_date, r._end_date))
			{
				// route start/end dates are valid
				Service s = r.get_service();
				if (TimeFormatter.isValidDate(d.getCurrentDayOfWeek(), TimeFormatter.getTodaysDateNumber(), day, s.get_start_date(), s.get_end_date()))
				{
					// service start/end date is valid
					output.add(r);
				}
			}
		}
		return output;
	}
	
	public static List<Route> from_SQL_Running_Currently(short currentTime, DayOfWeek day)
	{
		SQLiteDatabase db = SQLiteManager.getDb();
		String dayTerm = SQLFieldNames.getDayString(day) + "=1";
		String time = TimeFormatter.basicStringFromTime(currentTime);
		String where = SQLFieldNames.ROUTES_START_TIME + "<=" + time + " AND " + SQLFieldNames.ROUTES_END_TIME + ">=" + time + " AND " + dayTerm;
		List<Route> routes = from_SQL(db, where, Route.LOAD_STOPS__ALL);

		// remove the non-current-running routes
		List<Route> output = new ArrayList<Route>(routes.size());
		IUIInterface d = UIInterfaceFactory.getInterface();
		for(Route r : routes)
		{
			if (TimeFormatter.isValidDate(d.getCurrentDayOfWeek(), TimeFormatter.getTodaysDateNumber(), day, r._start_date, r._end_date))
			{
				// route start/end dates are valid
				Service s = r.get_service();
				if (TimeFormatter.isValidDate(d.getCurrentDayOfWeek(), TimeFormatter.getTodaysDateNumber(), day, s.get_start_date(), s.get_end_date()))
				{
					// service start/end date is valid
					output.add(r);
				}
			}
		}
		return output;
	}
	
	/**
	 * Load route items from the database where routes are part of a service
	 * @param db The database to load the data from
	 * @param service_id The service id for all the routes to be loaded
	 */
	public static void from_SQL(SQLiteDatabase db, int service_id)
	{
		int timer_id = Timer.start_timer();
		
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		String where = SQLFieldNames.ROUTES_SERVICE_ID + "='"+service_id+"'";
		Cursor data = db.query(SQLFieldNames.ROUTES, null, where, null, null, null, null);
		
		int pos_id = data.getColumnIndex(SQLFieldNames.ROUTES_ID);
		int pos_mon = data.getColumnIndex(SQLFieldNames.ROUTES_MON);
		int pos_tue = data.getColumnIndex(SQLFieldNames.ROUTES_TUE);
		int pos_wed = data.getColumnIndex(SQLFieldNames.ROUTES_WED);
		int pos_thu = data.getColumnIndex(SQLFieldNames.ROUTES_THU);
		int pos_fri = data.getColumnIndex(SQLFieldNames.ROUTES_FRI);
		int pos_sat = data.getColumnIndex(SQLFieldNames.ROUTES_SAT);
		int pos_sun = data.getColumnIndex(SQLFieldNames.ROUTES_SUN);
		int pos_start_date = data.getColumnIndex(SQLFieldNames.ROUTES_START_DATE);
		int pos_end_date = data.getColumnIndex(SQLFieldNames.ROUTES_END_DATE);
		
		
		List<Route> loaded_routes = new ArrayList<Route>();
		HashMap<Integer, Route> routes_ids = new HashMap<Integer, Route>();
		while(data.moveToNext())
		{
			int route_id = data.getInt(pos_id);
			if (min > route_id) // get the min and max values for the route ids
				min = route_id;
			if (max < route_id)
				max = route_id;
			Route route = new Route(route_id, service_id);
			route.setRunning(DayOfWeek.Monday, data.getInt(pos_mon) == 1);
			route.setRunning(DayOfWeek.Tuesday, data.getInt(pos_tue) == 1);
			route.setRunning(DayOfWeek.Wednesday, data.getInt(pos_wed) == 1);
			route.setRunning(DayOfWeek.Thursday, data.getInt(pos_thu) == 1);
			route.setRunning(DayOfWeek.Friday, data.getInt(pos_fri) == 1);
			route.setRunning(DayOfWeek.Saturday, data.getInt(pos_sat) == 1);
			route.setRunning(DayOfWeek.Sunday, data.getInt(pos_sun) == 1);

			route._start_date = data.getInt(pos_start_date);
			route._end_date = data.getInt(pos_end_date);
			
			
			DataPointer.add_Object(Route.class, route);
			loaded_routes.add(route);
			routes_ids.put(route_id, route);
		}
		data.close();
		
		
		Map<Integer, List<Stop>> change_points = Stop.from_SQL(db, min, max);
		
		for(Integer route_id : change_points.keySet())
		{
			List<Stop> stops = change_points.get(route_id);
			List<DataPointer<Stop>> pointers = new ArrayList<DataPointer<Stop>>(stops.size());
			for(Stop s : stops)
			{
				pointers.add(new DataPointer<Stop>(Stop.class, s.get_id()));
			}
			Route route = routes_ids.get(route_id); 
			route._stops = pointers;
			route._fully_loaded = true;
		}
		
		Timer.report_timer("Routes & Stop Loaded. Service ID: " + service_id, timer_id);
		Timer.stop_timer(timer_id);
	}
	
	private static List<Route> from_SQL(SQLiteDatabase db, String where, int load_all_stops)
	{
		Cursor data = db.query(SQLFieldNames.ROUTES, null, where, null, null, null, null);
		
		int pos_id = data.getColumnIndex(SQLFieldNames.ROUTES_ID);
		int pos_service_id = data.getColumnIndex(SQLFieldNames.ROUTES_SERVICE_ID);
		int pos_mon = data.getColumnIndex(SQLFieldNames.ROUTES_MON);
		int pos_tue = data.getColumnIndex(SQLFieldNames.ROUTES_TUE);
		int pos_wed = data.getColumnIndex(SQLFieldNames.ROUTES_WED);
		int pos_thu = data.getColumnIndex(SQLFieldNames.ROUTES_THU);
		int pos_fri = data.getColumnIndex(SQLFieldNames.ROUTES_FRI);
		int pos_sat = data.getColumnIndex(SQLFieldNames.ROUTES_SAT);
		int pos_sun = data.getColumnIndex(SQLFieldNames.ROUTES_SUN);
		int pos_start_date = data.getColumnIndex(SQLFieldNames.ROUTES_START_DATE);
		int pos_end_date = data.getColumnIndex(SQLFieldNames.ROUTES_END_DATE);
		
		List<Route> routes = new ArrayList<Route>();
		HashSet<Integer> route_ids = new HashSet<Integer>();
		
		while(data.moveToNext())
		{
			int route_id = data.getInt(pos_id);
			int service_id = data.getInt(pos_service_id);
			Route route = new Route(route_id, service_id);
			route.setRunning(DayOfWeek.Monday, data.getInt(pos_mon) == 1);
			route.setRunning(DayOfWeek.Tuesday, data.getInt(pos_tue) == 1);
			route.setRunning(DayOfWeek.Wednesday, data.getInt(pos_wed) == 1);
			route.setRunning(DayOfWeek.Thursday, data.getInt(pos_thu) == 1);
			route.setRunning(DayOfWeek.Friday, data.getInt(pos_fri) == 1);
			route.setRunning(DayOfWeek.Saturday, data.getInt(pos_sat) == 1);
			route.setRunning(DayOfWeek.Sunday, data.getInt(pos_sun) == 1);
			
			route._start_date = data.getInt(pos_start_date);
			route._end_date = data.getInt(pos_end_date);
			

			// try to add object to database. use cached object if its contained inside cache already
			route = DataPointer.add_Object(Route.class, route);
			route_ids.add(route._id);
			routes.add(route);
		}
		data.close();
		
		if (routes.size() > 0)
		{
			// if loading the stops, load all the stop items, and link together with route objects
			if (load_all_stops == LOAD_STOPS__ALL)
			{
				Map<Integer,List<Stop>> all_stops = Stop.from_SQL(db, route_ids);
				
				for(Route route: routes)
				{
					int route_id = route._id;
					List<Stop> stops = all_stops.get(route_id);
					
					List<DataPointer<Stop>> pointers = new ArrayList<DataPointer<Stop>>();
					
					for(Stop s : stops)
					{
						pointers.add(new DataPointer<Stop>(Stop.class, s.get_id()));
					}
					
					route._stops = pointers; 
					route._fully_loaded = true;
				}
			}
			else if (load_all_stops == LOAD_STOPS__NULL)
			{
				for(Route route: routes)
				{
					route._stops = null;
				}
			}
			else
			{
				// not loading all stop information
				final Map<Integer,List<DataPointer<Stop>>> all_stops = SQLiteHelper.get_Stops(db, route_ids);
				
				for(Route route: routes)
				{
					int route_id = route._id;
					List<DataPointer<Stop>> stops = all_stops.get(route_id);
					
					route._stops = stops; 
					route._fully_loaded = false;
				}
			}
		}
		
		
		return routes;
	}
	
	private Route(int route_id, int service_id)
	{
		_id = route_id;
		_service_id = service_id;
	}


	private List<DataPointer<Stop>> _stops = new ArrayList<DataPointer<Stop>>();


	/**
	 * Get the number of stops on this route
	 * 
	 * @return Number of stops
	 */
	public int countStops()
	{
		loadStops();
		return _stops.size();
	}

	private void loadStops()
	{
		if (_stops == null)
		{
			L.d("Loading stops since null");
			_stops = Stop.from_SQL_Route(_id);
			_fully_loaded = true;
		}
	}
	
	/**
	 * Get the i'th stop from this route
	 * 
	 * @param index The position of the route
	 * @return The Stop on the route
	 */
	public Stop getStop(int index)
	{
		loadStops();
		return _stops.get(index).get_Object_Cache();
	}

	

	private boolean[] _running = new boolean[DayOfWeek.values().length];

	/**
	 * Is this route running on specified day?
	 * 
	 * @param day The day to check
	 * @return True if running, false otherwise
	 */
	public boolean isRunning(DayOfWeek day)
	{
		return _running[day.ordinal()];
	}
	
	private void setRunning(DayOfWeek day, boolean running)
	{
		_running[day.ordinal()] = running;
	}
	
	private int _start_date = 0;
	/**
	 * Get the start date value for this route
	 * @return
	 */
	public int get_start_date()
	{
		return _start_date;
	}
	
	private int _end_date = 0;
	/**
	 * Get the end date value for this route
	 * @return
	 */
	public int get_end_date()
	{
		return _end_date;
	}
	
	
	/**
	 * Get all positions of a stop
	 * @param stop_location The stop to find
	 * @return An array of positions of the stop
	 */
	public List<Integer> positionsOfStop(Location stop_location)
	{
		loadStops();
		List<Integer> positions = new ArrayList<Integer>();
		int numStops = _stops.size();
		for(int i=0;i<numStops;i++)
		{
			if (stop_location.get_id() == _stops.get(i).get_Object_Cache().getLocation().get_id())
				positions.add(i);
		}
		
		return positions;
	}
	
	/**
	 * Get the position of a stop in the route.
	 * @param location The location for the stop
	 * @param timeAtStop The time the train/bus is scheduled at this stop
	 * @return The position of the stop in this route
	 */
	public int getPositionOfStop(Location location, short timeAtStop)
	{
		loadStops();
		for(int i=0;i<_stops.size();i++)
		{
			Stop s = getStop(i);
			if (s.getLocation().get_id() == location.get_id() && s.getTime() == timeAtStop)
				return i;
			if (s.getTime() > timeAtStop) // looking at a time after current time, so stop looking
				return -1;
		}
		return -1;
	}

	/**
	 * Get the stop that stops at a location
	 * @param location
	 * @param timeAtStop 
	 * @return
	 */
	public Stop getStop(Location location, short timeAtStop)
	{
		int res = getPositionOfStop(location, timeAtStop);
		
		if (res == -1)
			return null;
		return getStop(res);
	}

	/**
	 * Get the final stop on this line
	 * @return
	 */
	public Stop getFinalStop()
	{
		loadStops();
		return _stops.get(_stops.size()-1).get_Object_Cache();
	}
	
	/**
	 * Get the first stop on this line
	 * @return
	 */
	public Stop getFirstStop()
	{
		loadStops();
		return _stops.get(0).get_Object_Cache();
	}
	
	public Stop getNextStop(short time)
	{
		loadStops();
		for(int i=0;i<_stops.size();i++)
		{
			Stop s = _stops.get(i).get_Object_Cache();
			if (s.getTime() > time)
			{
				return s;
			}
		}
		return getFinalStop();
	}
	
	/**
	 * Get the type of service this is
	 * @return The type of service this is
	 */
	public TransportType getTransportType()
	{
		DataPointer<Service> service = new DataPointer<Service>(Service.class, this._service_id);
		return service.get_Object_Cache().getType();
	}
	
	@Override
	public String toString()
	{
		loadStops();
		String out = "Route: ID=" + _id +" ";
		out += this.getFirstStop().getLocation().getRealName() + " @ " + getFirstStop().getFormatedTime();
		out += " to " + getFinalStop().getLocation().getRealName() + " @ " + getFinalStop().getFormatedTime();
		out += "    ";
		for (DayOfWeek day : DayOfWeek.values())
		{
			if (isRunning(day))
				out += day.toString() + ", ";
		}
		out += _stops.size() + " stops";
		return out;
	}
}
