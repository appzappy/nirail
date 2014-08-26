package appzappy.nirail.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import appzappy.nirail.data.collections.LinkingLocations;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.Location_Link_Unit;
import appzappy.nirail.data.timetable.Network;

public class FindPath
{
	public static List<Location> get_linking_locations(LinkingLocations touching_data, Network network, Integer start_id, Integer target_id)
	{
		List<Integer> temp = get_linking_locations(touching_data, start_id, target_id);
		
		List<Location> output = new ArrayList<Location>(temp.size());
		for(int i=0;i<temp.size();i++)
		{
			Integer location_id = temp.get(i);
			output.add(network.get(location_id));
		}
		return output;
	}
	
	
	public static List<Integer> get_linking_locations(LinkingLocations touching_data, Integer start_id, Integer target_id)
	{
		List<Integer> touching = touching_data.getLinkedLocations(start_id);
		
		// more efficient lookup for when they're already touching
		if (touching.contains(target_id) || start_id.equals(target_id))
		{
			List<Integer> output = new ArrayList<Integer>(2);
			output.add(start_id);
			output.add(target_id);
			return output;
		}
		
		// longer reach
		// using Dijkstra's algorithm
		Queue<Location_Link_Unit> to_process = new LinkedList<Location_Link_Unit>();
		
		for(Integer i : touching)
		{
			to_process.add(new Location_Link_Unit(i, start_id));
		}
		
		Map<Integer, Location_Link_Unit> processed_nodes = new HashMap<Integer, Location_Link_Unit>(); 
		
		while (to_process.size() > 0)
		{
			Location_Link_Unit unit = to_process.poll();
			Integer i = unit.location_id;
			if (processed_nodes.containsKey(i))
				continue;
			processed_nodes.put(i, unit);
			
			if (i.equals(target_id))
			{
				break;
			}
			
			List<Integer> items = touching_data.getLinkedLocations(i);
			for (Integer item : items)
			{
				if (processed_nodes.containsKey(item))
					continue;
				to_process.add(new Location_Link_Unit(item, i));
			}
			
		}
		
		// build the output data from the final item working backwards
		List<Integer> out_reversed = new ArrayList<Integer>();
		out_reversed.add(target_id);
		
		Integer current = target_id;
		while (!current.equals(start_id))
		{
			current = processed_nodes.get(current).previous_id;
			out_reversed.add(current);
		}
		
		List<Integer> output = new ArrayList<Integer>(out_reversed.size());
		
		// reverse the output set 
		for (int i=out_reversed.size()-1;i>=0;i--)
		{
			Integer loc_id = out_reversed.get(i);
			output.add(loc_id);
		}
		return output;
	}
}
