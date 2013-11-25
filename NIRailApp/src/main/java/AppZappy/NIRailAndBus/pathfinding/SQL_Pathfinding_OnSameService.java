package AppZappy.NIRailAndBus.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import android.database.sqlite.SQLiteDatabase;

import AppZappy.NIRailAndBus.data.*;
import AppZappy.NIRailAndBus.data.collections.DataPointer;
import AppZappy.NIRailAndBus.data.db.SQLiteHelper;
import AppZappy.NIRailAndBus.data.db.SQLiteManager;
import AppZappy.NIRailAndBus.data.db.SQLiteHelper.RouteInformation;
import AppZappy.NIRailAndBus.data.db.SQLiteHelper.StopDetail;
import AppZappy.NIRailAndBus.data.enums.DayOfWeek;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.data.timetable.Service;
import AppZappy.NIRailAndBus.data.timetable.Timetable;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.util.L;
import AppZappy.NIRailAndBus.util.timing.TimeFormatter;
import AppZappy.NIRailAndBus.util.timing.Timer;

public class SQL_Pathfinding_OnSameService implements IPathfindingAlgorithm
{
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	public List<List<Journey>> findPaths(Timetable timetable, Location source, Location destination)
	{
		// get journey data
		DayOfWeek[] days = getDays();
		
		
		SQLiteDatabase db = SQLiteManager.getDb();
		List<Location> locs = new ArrayList<Location>(2);
		locs.add(source);
		locs.add(destination);
		Map<Integer,Map<Integer, RouteInformation>> location_routes = SQLiteHelper.get_routes_at_station(db, locs);
		
		List<List<Journey>> res = findPaths(db, location_routes, timetable, source, destination, days);
		
		db.close();
		
		return res;
	}

	public DayOfWeek[] getDays()
	{
		DayOfWeek firstItem = dataInterface.getCurrentDayOfWeek();
		if (firstItem == DayOfWeek.Saturday || firstItem == DayOfWeek.Sunday)
			firstItem = DayOfWeek.Monday;
		
		DayOfWeek[] days = new DayOfWeek[] {firstItem, DayOfWeek.Saturday, DayOfWeek.Sunday};
		return days;
	}
	
	public List<List<Journey>> findPaths(SQLiteDatabase db, Map<Integer,Map<Integer, RouteInformation>> location_routes, Timetable timetable, Location source, Location destination)
	{
		DayOfWeek[] days = getDays();
		
		return findPaths(db, location_routes, timetable, source, destination, days);
	}
	
	private List<List<Journey>> findPaths(SQLiteDatabase db, Map<Integer,Map<Integer, RouteInformation>> location_routes, Timetable timetable, Location source, Location destination, DayOfWeek[] days)
	{
		int timer_id = Timer.start_timer();
		L.d("SQL Pathfinding: " + source.getRealName() + " to " + destination.getRealName());
		
		List<List<Journey>> finalOutSet = new ArrayList<List<Journey>>();
		
		final int[] dayPos = new int[7];
		for (int z=0; z<7; z++)
		{
			dayPos[z] = -1;
		}
		
		int day_i=0;
		for(DayOfWeek d : days)
		{
			finalOutSet.add(new ArrayList<Journey>());
			dayPos[d.ordinal()] = day_i;
			day_i++;
		}

		int today = TimeFormatter.getTodaysDateNumber();
		DayOfWeek todayDay = UIInterfaceFactory.getInterface().getCurrentDayOfWeek();
		
		
		
		
		Map<Integer, RouteInformation> source_routes = location_routes.get(source.get_id());
		Map<Integer, RouteInformation> destin_routes = location_routes.get(destination.get_id());
		//Map<Integer, RouteInformation> source_routes = SQLiteHelper.get_routes_at_station(db, source);
		//Map<Integer, RouteInformation> destin_routes = SQLiteHelper.get_routes_at_station(db, destination);
		
		int matching_size = Math.max(source_routes.size(), destin_routes.size());
		Set<Integer> matching_route_ids = new HashSet<Integer>(matching_size);
		

		// clone the sets for editing
		Map<Integer, RouteInformation> new_source_routes = new HashMap<Integer, RouteInformation>(source_routes);
		Map<Integer, RouteInformation> new_destin_routes = new HashMap<Integer, RouteInformation>(destin_routes);
		
		
		// Look for the routes whose starting stops all happen after the 
		// destination stops. These routes can't have any trains so remove them
		// from the processing sets since the train is going the wrong direction
		for (Integer source_route_id : source_routes.keySet())
		{
			RouteInformation source_info = source_routes.get(source_route_id);
				
			RouteInformation dest_info = destin_routes.get(source_route_id);
			if (dest_info == null)
			{
				continue;
			}
			
			short earlistStartTime = source_info.get(0).time;
			for (int i=1;i<source_info.size();i++)
			{
				short currentTime = source_info.get(i).time;
				if (currentTime < earlistStartTime)
					earlistStartTime = currentTime;
			}
			
			short latestEndTime = dest_info.get(0).time;
			for (int i=1;i<dest_info.size();i++)
			{
				short currentTime = dest_info.get(i).time;
				if (currentTime > latestEndTime)
					latestEndTime = currentTime;
			}
			
			if (earlistStartTime > latestEndTime)
			{
				// then earliest start is after the last end
				// so dont process
				new_source_routes.remove(source_route_id);
				new_destin_routes.remove(source_route_id);
			}
		}

		
		source_routes = new_source_routes;
		destin_routes = new_destin_routes;
		
		
		for(Integer source_route_id : source_routes.keySet())
		{
			if (destin_routes.containsKey(source_route_id))
			{
				matching_route_ids.add(source_route_id);
			}
		}

		if (matching_route_ids.size() == 0)
		{
			return finalOutSet;
		}
		
		// load all the matching routes
		Route.from_SQL(db, matching_route_ids, Route.LOAD_STOPS__NULL);
		
		
		boolean valid_today = false;
		
		for(Integer route_id : matching_route_ids)
		{
			Route route = DataPointer.get_Object(Route.class, route_id);
			RouteInformation sourceInfo = source_routes.get(route_id);
			RouteInformation destinInfo = destin_routes.get(route_id);
			
			// check the service is valid for today
			Service service = route.get_service();
			valid_today = false;
			for(DayOfWeek d : days)
			{
				if (TimeFormatter.isValidDate(todayDay, today, d, service.get_start_date(), service.get_end_date()))
				{
					valid_today = true;
					break;
				}
			}
			if (!valid_today)
				continue;
			
			
			// check valid route date
			valid_today = false;
			for(DayOfWeek d : days)
			{
				if (TimeFormatter.isValidDate(todayDay, today, d, route.get_start_date(), route.get_end_date()))
				{
					valid_today = true;
					break;
				}
			}
			if (!valid_today)
				continue;
			
			// find the days this route is valid for
			List<DayOfWeek> valid_days = new ArrayList<DayOfWeek>();
			for(DayOfWeek day : days)
			{
				if (route.isRunning(day))
				{
					valid_days.add(day);
				}
			}
			if (valid_days.size() == 0) // this route isn't running on any of the requested days
			{
				continue;
			}
			
			List<Integer> start_ids = sourceInfo.get_Stop_Ids();
			List<Integer> end_ids = destinInfo.get_Stop_Ids();
			
			removeIrrevelantIds(start_ids, end_ids);
			
			// removes the duplicates found at great victoria and central
			removeTouchingStops(start_ids, end_ids);
			


			if (start_ids.size() == 0) // start position not in this route
				continue; 
			if (end_ids.size() == 0) // end position not in this route
				continue; 
			
			
			for(Integer start_stop_id : start_ids)
			{
				StopDetail source_detail = sourceInfo.getStopDetail(start_stop_id);
				
				
				// skip the starting value if isn't not a pickup location
				if (!source_detail.pickup)
					continue;
					
				for (Integer end_stop_id: end_ids)
				{
					if (start_stop_id >= end_stop_id) // start happens after end INVALID
						continue;
					
					StopDetail destin_detail = destinInfo.getStopDetail(end_stop_id);
					
					
					// skip the ending position if it isn't a dropoff location
					if (!destin_detail.dropoff)
						continue;

					// only valid combinations here
					
					Journey j = new Journey();
					JourneyPortion portion = JourneyPortion.create(source, source_detail.time, destination, destin_detail.time, route);
					j.addPortion(portion);
					if (j.getLength() > 0)
					{
						// add this journey to every day that has requested it
						for(DayOfWeek day : valid_days)
						{
							if (!TimeFormatter.isValidDate(todayDay, today, day, route.get_start_date(), route.get_end_date()))
							{
								continue;
							}
							int output_pos = dayPos[day.ordinal()];
							finalOutSet.get(output_pos).add(j);
						}
						
					}
				}
			}
		}
		
		
		Timer.report_timer("Time to find SQL paths", timer_id);
		Timer.stop_timer(timer_id);
		
		return finalOutSet;
	}
	
	
	
	private void removeTouchingStops(List<Integer> startPositions, List<Integer> endPositions)
	{
		if (startPositions.size() > 1)
		{
			// STARTING POSITIONS
			// if there are any points going to the same place, that are in positions next to itself
			// remove the 1st one since this is a starting search
			int previous = startPositions.get(startPositions.size()-1);
			for(int i=startPositions.size()-1;i>=0;i--)
			{
				int current = startPositions.get(i);
				if (current + 1 == previous)
				{
					// current is the smaller number
					startPositions.remove(i);
				}
				else
				{
					previous = current;
				}
			}
		}
		if (endPositions.size() > 1)
		{
			// ENDING POSITIONS
			// if there are any points going to the same place, that are in positions next to itself
			// remove the 2nd one since this is an ending search
			int previous = endPositions.get(endPositions.size()-1);
			for(int i=endPositions.size()-1;i>=0;i--)
			{
				int current = endPositions.get(i);
				if (current + 1 == previous)
				{
					// current is the smaller number
					// so remove the previous
					endPositions.remove(i+1);
				}
				else
				{
					previous = current;
				}
			}
		}
	}


	
	
	/**
	 * Remove the ids that happen in start before end and in end after start
	 * @param start
	 * @param end
	 */
	private static void removeIrrevelantIds(List<Integer> start, List<Integer> end)
	{
		int startItems = start.size();
		int endItems = end.size();
		if (startItems == 0 || endItems == 0)
			return;
		
		// while the 1st stop of the end is before the 1st starting point remove the 1st stop
		while(endItems > 0 && (end.get(0) <= start.get(0)))
		{
			end.remove(0);
			endItems--;
		}
		
		if (endItems == 0) // removed all end points
		{
			return; 
		}
		
		int finalEnd = endItems - 1;
		int finalStart = startItems - 1;
		// while the final starting position is after the final stop position, remove the final start
		while (finalStart > -1 && (start.get(finalStart) >= end.get(finalEnd)))
		{
			start.remove(finalStart);
			finalStart--;
		}
	}


	@Override
	public String toString()
	{
		return "SQL_Pathfinding_onsameservice";
	}
}
