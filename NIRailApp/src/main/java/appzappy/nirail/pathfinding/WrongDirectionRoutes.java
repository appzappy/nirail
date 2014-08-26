package appzappy.nirail.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appzappy.nirail.data.db.SQLiteHelper.RouteInformation;

public class WrongDirectionRoutes
{
	public static Map<Integer,Map<Integer, RouteInformation>> removeWrongDirectionRoutes(List<Integer> jump_path, Map<Integer,Map<Integer, RouteInformation>> location_routes)
	{
		// clone the sets for editing
		Map<Integer,Map<Integer, RouteInformation>> location_routes_clone = new HashMap<Integer,Map<Integer, RouteInformation>>();
		for (Integer location_id : location_routes.keySet())
		{
			Map<Integer, RouteInformation> original = location_routes.get(location_id);
			Map<Integer, RouteInformation> clone = new HashMap<Integer, RouteInformation>(original);
			location_routes_clone.put(location_id, clone);
		}
		
		// Remove all routes going wrong way along journey
		for (int temp=0;temp<jump_path.size()-1;temp++)
		{
			Integer early_path = jump_path.get(temp);
			Integer later_path = jump_path.get(temp+1);
			
			Map<Integer, RouteInformation> source_routes = location_routes.get(early_path);
			Map<Integer, RouteInformation> destin_routes = location_routes.get(later_path);
			
			
			// clone the sets for editing
			Map<Integer, RouteInformation> new_source_routes = location_routes_clone.get(early_path);
			Map<Integer, RouteInformation> new_destin_routes = location_routes_clone.get(later_path);
			
			
			
			// Look for the routes whose starting stops all happen after the 
			// destination stops. These routes can't have any trains so remove them
			// from the processing sets since the train is going the wrong direction
			for (Integer source_route_id : source_routes.keySet())
			{
				RouteInformation source_info = source_routes.get(source_route_id);
					
				RouteInformation dest_info = destin_routes.get(source_route_id);
				if (dest_info == null)
				{
					// continue NOT REMOVE since this route may appear in
					// the 2nd journey portion of the pathfinding
					continue; 
				}
				
				// find the earliest start
				short earlistStartTime = source_info.get(0).time;
				for (int i=1;i<source_info.size();i++)
				{
					short currentTime = source_info.get(i).time;
					if (currentTime < earlistStartTime)
						earlistStartTime = currentTime;
				}
				
				// find the latest end
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

		}
		
		// update the references to the route data
		return location_routes_clone;
	}
}
