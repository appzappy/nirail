package AppZappy.NIRailAndBus.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.collections.LinkingLocations;
import AppZappy.NIRailAndBus.data.db.SQLiteHelper;
import AppZappy.NIRailAndBus.data.db.SQLiteManager;
import AppZappy.NIRailAndBus.data.db.SQLiteHelper.RouteInformation;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.timetable.Network;
import AppZappy.NIRailAndBus.data.timetable.Timetable;

public class SQL_Pathfinding_Multijourney implements IPathfindingAlgorithm
{
	public static SQL_Pathfinding_OnSameService single = new SQL_Pathfinding_OnSameService();
	
	public final int number_requested_days = 3;
	
	public List<List<Journey>> findPaths(Timetable timetable, Location source, Location destination)
	{
		final Integer source_id = source.get_id();
		final Integer target_id = destination.get_id();
		
		final Network network = LoadData.getNetwork();
		final LinkingLocations swappingLocations = network.swapping_locations;
		final LinkingLocations touchingLocations = network.touching_locations;
		
		List<Integer> jump_path = FindSwappingStations.swapping_path_inc_ends(touchingLocations, swappingLocations, source_id, target_id);
		
		
		
		// code for enterprise considerations, since it skips some stations
		Location portadown = network.get("Portadown");
		Location central = network.get("Central Station");
		List<Integer> enterprise_skips = FindSwappingStations.swapping_path_inc_ends(touchingLocations, swappingLocations, portadown.get_id(), central.get_id());
		
		List<Integer> enterprise_path = null;
		if (jump_path.containsAll(enterprise_skips))
		{
			// possible use for the enterprise route
			// duplicate calculations with jump_path missing lisburn station and gvs
			
			enterprise_skips.remove((Integer) portadown.get_id());
			enterprise_skips.remove((Integer) central.get_id());
			
			enterprise_path = new ArrayList<Integer>(jump_path);
			enterprise_path.removeAll(enterprise_skips);
		}
		
		
		
		SQLiteDatabase db = SQLiteManager.getDb();
		List<Location> stations_to_get = Location.getLocationList(network, jump_path);
		
		Map<Integer,Map<Integer, RouteInformation>> location_routes = SQLiteHelper.get_routes_at_station(db, stations_to_get);
		location_routes = WrongDirectionRoutes.removeWrongDirectionRoutes(jump_path, location_routes);
		
		

		// build the routes that link each of the locations together
		List<List<List<Journey>>> all_paths = get_paths(jump_path, network, timetable, db, location_routes);
		
		// build the complete journeys together
		List<List<Journey>> journey_data = make_journey_data(all_paths);
		
		if (enterprise_path != null)
		{
			// do calculations for enterprise trains
			int pos_portadown = jump_path.indexOf(portadown.get_id());
			int pos_central = jump_path.indexOf(central.get_id());
			
			int min = Math.min(pos_portadown, pos_central);
			int max = Math.max(pos_portadown, pos_central);
			Location start = min == pos_portadown ? portadown : central;
			Location end = min != pos_portadown ? portadown : central;
			
			List<List<List<Journey>>> e_all_paths = new ArrayList<List<List<Journey>>>(all_paths);
			List<List<Journey>> path = single.findPaths(db, location_routes, timetable, start, end);

			e_all_paths.set(min, path);

			for (int i=min+1;i<max;i++)
			{
				e_all_paths.remove(min+1);
			}
			
			
			// build the complete journeys together
			List<List<Journey>> e_journey_data = make_journey_data(e_all_paths);
			
			// add the enterprise journeys to the output set
			for (int i=0;i<e_journey_data.size();i++)
			{
				List<Journey> js = e_journey_data.get(i);
				for (Journey j : js)
				{
					journey_data.get(i).add(j);
				}
			}
		}
		
		// perform work on the journey list to join spilt routes into single journey portions
		for (List<Journey> day_journeys : journey_data)
		{
			for (Journey j : day_journeys)
			{
				j.compact();
			}
		}

		// remove the journeys that have a route appearing multiple times
		List<List<Journey>> output = new ArrayList<List<Journey>>();
		for (List<Journey> day_journeys : journey_data)
		{
			JourneySorting.sortByStartTimeDescending(day_journeys);
			HashSet<Integer> used_routes = new HashSet<Integer>(day_journeys.size()*4);
			
			// used to hold the correct output
			List<Journey> day_journeys_filtered = new ArrayList<Journey>(day_journeys.size());
			output.add(day_journeys_filtered);
			
			for (Journey journ : day_journeys)
			{
				boolean valid = true;
				for (JourneyPortion port: journ)
				{
					boolean new_add = used_routes.add(port.getRoute().get_id());
					if (!new_add)
					{
						// route already used 
						// don't add this route to output set
						valid = false;
						break;
					}
				}
				if (valid)
				{
					day_journeys_filtered.add(journ);
				}
			}
			

			JourneySorting.sortByEndTime(day_journeys_filtered);
		}

		db.close();
		return output;
	}
	
	
	private List<List<List<Journey>>> get_paths (List<Integer> jump_path, Network network, Timetable timetable, SQLiteDatabase db, Map<Integer,Map<Integer, RouteInformation>> location_routes)
	{
		List<List<List<Journey>>> all_paths = new ArrayList<List<List<Journey>>>();
		for (int i=0;i<jump_path.size()-1;i++)
		{
			Integer start_loc = jump_path.get(i);
			Integer end_loc = jump_path.get(i+1);
			
			Location start = network.get(start_loc);
			Location end = network.get(end_loc);
			
			List<List<Journey>> path = single.findPaths(db, location_routes, timetable, start, end);
			all_paths.add(path);
		}
		return all_paths;
	}
	
	
	private List<List<Journey>> make_journey_data(List<List<List<Journey>>> all_paths)
	{
		List<List<Journey>> journey_data = new ArrayList<List<Journey>>();
		
		// for each day type
		for (int day=0;day<number_requested_days;day++)
		{
			List<Journey> today_final = new ArrayList<Journey>();
			journey_data.add(today_final);
			
			List<List<Journey>> data = all_paths.get(0); 
			List<Journey> todays = data.get(day);
			
			
			// for each start route
			for (Journey j : todays)
			{
				// find the earliest possible journeys to match together
				Journey make = new Journey();
				boolean valid = true;
				make.copyIn(j);
				
				short ending_time = j.getEndingTime();
				
				for (int part=1;part<all_paths.size();part++)
				{
					List<Journey> parts = all_paths.get(part).get(day);
					
					// find the journey with the earliest start time yet >= ending_time
					int earliest_start = Integer.MAX_VALUE;
					Journey best = null;
					for (Journey journey: parts)
					{
						final short starting = journey.getStartingTime(); 
						if (starting >= ending_time && starting < earliest_start)
						{
							earliest_start = starting;
							best = journey;
						}
					}
					
					// take this journey portion and move onto next train segment
					
					if (best == null)
					{
						// no suitable train was found
						// this is an invalid route
						// move onto next starting route
						valid = false;
						break;
					}
					else
					{
						make.copyIn(best);
						ending_time = best.getEndingTime();
					}
				}
				
				if (valid)
				{
					// save the route data
					today_final.add(make);
				}
			}
		}
		
		return journey_data;
	}
}
