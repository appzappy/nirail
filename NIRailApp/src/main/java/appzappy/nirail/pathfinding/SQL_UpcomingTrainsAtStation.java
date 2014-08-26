package appzappy.nirail.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import appzappy.nirail.data.LoadData;
import appzappy.nirail.data.collections.DataPointer;
import appzappy.nirail.data.db.SQLFieldNames;
import appzappy.nirail.data.db.SQLiteManager;
import appzappy.nirail.data.enums.DayOfWeek;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.Route;
import appzappy.nirail.data.timetable.Service;
import appzappy.nirail.data.timetable.Timetable;
import appzappy.nirail.util.timing.TimeFormatter;
import appzappy.nirail.util.timing.Timer;

public class SQL_UpcomingTrainsAtStation
{
	public List<RouteStopPosition> getUpcomingTrains(Timetable timetable, List<Location> locations, short earliestTime, short latestTime, DayOfWeek dayOfWeek, int numberItemsPerLocation)
	{
		int timer_id = Timer.start_timer();
		List<RouteStopPosition> d = getUpcomingTrainsAtStop(timetable, locations, earliestTime, latestTime, dayOfWeek, numberItemsPerLocation);
		d = removeTouchingEntries(d, false);
		Timer.report_timer("Get Upcoming Trains", timer_id);
		Timer.stop_timer(timer_id);
		
		return d;
	}
	
	

	
	public List<Journey> getUpcomingDepartures(Timetable timetable, List<Location> locations, short earliestTime, short latestTime, DayOfWeek dayOfWeek, int numberItemsPerLocation)
	{
		int timer_id = Timer.start_timer();
	
		List<RouteStopPosition> arrivalsdepartures = getUpcomingTrainsAtStop(timetable, locations, earliestTime, latestTime, dayOfWeek, numberItemsPerLocation);
		arrivalsdepartures = removeTouchingEntries(arrivalsdepartures, true);
		List<Journey> output = new ArrayList<Journey>(arrivalsdepartures.size());
		
		for (RouteStopPosition rsp : arrivalsdepartures)
		{
			if (rsp.isFinalStop())
			{
				// if the position of this route stop is at the end
				// don't add this item to the output set
				// i.e. Have all routes, but NOT terminating routes
				continue; 
			}
			
			// this stop is not a valid departing stop for this route
			// so don't include it in the output set
			if (!rsp.getCurrentStop().isPickUp()) 
				continue;
			
			Journey journey = new Journey();
			JourneyPortion portion = JourneyPortion.create(rsp.getLocation(), rsp.getTime(), rsp.getFinalStop_Location(), rsp.getFinalStop_Time(), rsp.route);
			journey.addPortion(portion);
			output.add(journey);
		}
		
		Timer.report_timer("Get Upcoming Departures", timer_id);
		Timer.stop_timer(timer_id);
		
		return output;
	}
	
	
	private static List<RouteStopPosition> removeTouchingEntries(List<RouteStopPosition> items, boolean remove_first)
	{
		Map<Integer, List<RouteStopPosition>> pairs = new HashMap<Integer, List<RouteStopPosition>>();
		
		for(RouteStopPosition i : items)
		{
			Integer route_id = i.route.get_id();
			if (!pairs.containsKey(route_id))
			{
				// route not in pairs set yet
				List<RouteStopPosition> list = new ArrayList<RouteStopPosition>();
				list.add(i);
				pairs.put(route_id, list);
			}
			else
			{
				List<RouteStopPosition> list = pairs.get(route_id);
				list.add(i);
			}
		}
		
		
		// go through the pair data and check if there are any touching entries
		List<RouteStopPosition> output = new ArrayList<RouteStopPosition>(items.size());
		
		for(Integer route_id : pairs.keySet())
		{
			List<RouteStopPosition> stops = pairs.get(route_id);
			
			if (stops.size() == 0)
			{
				continue;
			}
			if (stops.size() == 1)
			{
				// only 1 stop on this route
				output.add(stops.get(0));
				continue;
			}
			
			int num_stops = stops.size();
			
			for(int i=0;i<num_stops;i++)
			{
				// only thing we're checking is to keep rsp or not
				RouteStopPosition rsp = stops.get(i);
				boolean pair = false;
				for (int j=0;j<num_stops;j++)
				{
					if (i==j)
					{
						continue;
					}
					RouteStopPosition rsp2 = stops.get(j);
					
					if (rsp.getLocation().get_id() == rsp2.getLocation().get_id())
					{
						// at same location
						if (Math.abs(rsp.position - rsp2.position) == 1)
						{
							pair = true;
							// only 1 difference
							if (rsp.position < rsp2.position)
							{
								// rsp comes 1st / rsp2 2nd 
								if (remove_first)
								{
									// do nothing
								}
								else
								{
									// save it!
									output.add(rsp);
									break;
								}
							}
							else
							{
								// rsp2 1st / rsp 2nd
								if (remove_first)
								{
									output.add(rsp);
									break;
								}
								else
								{
									// do nothing
								}
							}
						}
					}
				}
				
				if (!pair)
				{
					output.add(rsp);
				}
			}
			
		}
		
		
		// sort the list again
		RouteStopPosition.sortByStartTime(output);
		
		return output;
	}
	
	private List<RouteStopPosition> getUpcomingTrainsAtStop(Timetable timetable, List<Location> locations, short earliestTime, short latestTime, DayOfWeek dayOfWeek, int numberItemsPerLocation)
	{
		List<RouteStopPosition> values = new ArrayList<RouteStopPosition>();
		SQLiteDatabase db = SQLiteManager.getDb();
		
		// generate the in_value 
		String in_value = "";
		for(Location l : locations)
			in_value += l.get_id() + ", ";

		if (in_value.length() >= 2)
			in_value = in_value.substring(0, in_value.length()-2); // clean the trailing ', '
		
		
		int today = TimeFormatter.getTodaysDateNumber();
		
		
		// get all the upcoming values
		// SELECT * FROM stops WHERE time >= earliestTime AND time < latestTime 
		//              AND location_id IN (location_values) 
		//              ORDER BY time LIMIT number*#location 
		
		String where = SQLFieldNames.STOPS_TIME + ">='" + TimeFormatter.basicStringFromTime(earliestTime) + "'";
		where += " AND ";
		where += SQLFieldNames.STOPS_TIME + "<'" + TimeFormatter.basicStringFromTime(latestTime) + "'";
		where += " AND ";
		where += SQLFieldNames.STOPS_LOCATION_ID + " IN (" + in_value + ")";
		
		String limit = "" + locations.size() * numberItemsPerLocation * 10;
		
		String order = SQLFieldNames.STOPS_TIME;
		Cursor data = db.query(SQLFieldNames.STOPS, null, where, null, null, null, order, limit);
		
		int pos_location_id = data.getColumnIndex(SQLFieldNames.STOPS_LOCATION_ID);
		int pos_route_id = data.getColumnIndex(SQLFieldNames.STOPS_ROUTE_ID);
		int pos_time = data.getColumnIndex(SQLFieldNames.STOPS_TIME);
		//int pos_id = data.getColumnIndex(SQLFieldNames.STOPS_ID);
		
		HashSet<Integer> route_ids = new HashSet<Integer>();
		
		// get all the route ids
		while(data.moveToNext())
		{
			int r_id = data.getInt(pos_route_id);
			route_ids.add(r_id);	
		}
		if (route_ids.size() > 0)
		{
			Route.from_SQL(db, route_ids, Route.LOAD_STOPS__ALL);
			
			// reset the cursor
			data.moveToPosition(-1);
			
			while(data.moveToNext())
			{
				int route_id = data.getInt(pos_route_id);
				DataPointer<Route> route_dp = new DataPointer<Route>(Route.class, route_id);
				Route route = route_dp.get_Object_Cache();
				
				
				// route not valid today
				if (!route.isRunning(dayOfWeek))
					continue;
				
				
				DataPointer<Service> service_dp = new DataPointer<Service>(Service.class, route.get_service_id());
				Service service = service_dp.get_Object_Cache();
				// check valid service date
				if (!TimeFormatter.isValidDate(dayOfWeek, today, dayOfWeek, service.get_start_date(), service.get_end_date()))
				{
					continue; // service date is not valid. Don't include this item
				}
				
				
				// check valid route date
				if (!TimeFormatter.isValidDate(dayOfWeek, today, dayOfWeek, route.get_start_date(), route.get_end_date()))
				{
					continue; // route date is not valid. Don't include this item
				}
				

				int location_id = data.getInt(pos_location_id);
				Location location = LoadData.getNetwork().get(location_id);
				
				String time_string = data.getString(pos_time); 
				short timeAtStop = TimeFormatter.timeFromString(time_string);
				
				int position = route.getPositionOfStop(location, timeAtStop);
				
				RouteStopPosition pos = new RouteStopPosition(route, position);
				values.add(pos);
			}
			
		}
		
		data.close();
		
		db.close();
		
		
		RouteStopPosition.sortByStartTime(values);
		
		return values;
	}
}
