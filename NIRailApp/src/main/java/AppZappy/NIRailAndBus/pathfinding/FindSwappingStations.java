package AppZappy.NIRailAndBus.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import AppZappy.NIRailAndBus.data.collections.LinkingLocations;

public class FindSwappingStations
{
	public static List<Integer> swapping_path_inc_ends(LinkingLocations touching, LinkingLocations swapping, Integer source, Integer target)
	{
		List<Integer> swap_paths = swapping_path(touching, swapping, source, target);
		
		List<Integer> jump_path = new ArrayList<Integer>(swap_paths.size() + 2);
		if (swap_paths.size() == 0 || swap_paths.get(0) != source)
		{
			// starting point is not a jump point
			jump_path.add(source);
		}
		// add the jump path
		jump_path.addAll(swap_paths);
		if (jump_path.get(jump_path.size()-1) != target)
		{
			// final point is not a jump point
			jump_path.add(target);
		}
		return jump_path;
	}
	
	public static List<Integer> swapping_path(LinkingLocations touching, LinkingLocations swapping, Integer source, Integer target)
	{
		final Set<Integer> swapping_locations = swapping.getLocations();
		
		boolean source_valid = true;
		boolean target_valid = true;
		List<Integer> source_swap_posible = null;
		List<Integer> target_swap_posible = null;
		Integer source_swapping = source;
		Integer target_swapping = target;
		if (!swapping_locations.contains(source))
		{
			// source is NOT a swapping station
			// find the correct swap
			source_valid = false;
			source_swap_posible = nearestSwapping(touching, swapping, source);
			if (source_swap_posible.size() == 1)
			{
				source_swapping = source_swap_posible.get(0);
				source_valid = true;
			}
		}
		
		if (!swapping_locations.contains(target));
		{
			// target is not a swapping station
			// find the correct swap
			target_valid = false;
			
			target_swap_posible = nearestSwapping(touching, swapping, target);
			if (target_swap_posible.size() == 1)
			{
				target_swapping = target_swap_posible.get(0);
				target_valid = true;
			}
		}
		
		if (!source_valid || !target_valid)
		{
			// either 1st or 2nd is not valid
			// pathfind from 1st swap to final swap
			
			Integer temp1 = source_swapping; 
			if (!source_valid)
			{
				temp1 = source_swap_posible.get(0);
			}
			Integer temp2 = target_swapping;
			if (!target_valid)
			{
				temp2 = target_swap_posible.get(0);
			}
			List<Integer> path = FindPath.get_linking_locations(swapping, temp1, temp2);
			
			if (!source_valid)
			{
				if (path.contains(source_swap_posible.get(1)))
				{
					// contains the 2nd value in the path
					// therefore the 1st item is "futher" away
					source_swapping = source_swap_posible.get(1);
				}
				else
				{
					// not contained, so 2nd is futher away
					source_swapping = source_swap_posible.get(0);
				}
				source_valid = true;
			}
			
			
			if (!target_valid)
			{
				if (path.contains(target_swap_posible.get(1)))
				{
					// contains the 2nd value in the path
					// therefore the 1st item is "futher" away
					target_swapping = target_swap_posible.get(1);
				}
				else
				{
					// not contained, so 2nd is futher away
					target_swapping = target_swap_posible.get(0);
				}
				target_valid = true;
			}
			
		}
		
		if (source_swapping.equals(target_swapping))
		{
			// then stations are next to same swapping station
			List<Integer> output = new ArrayList<Integer>();
			//output.add(source_swapping);
			return output;
		}
		
		// check that the source and destination aren't between the source and target
		List<Integer> minor_route = FindPath.get_linking_locations(touching, source_swapping, target_swapping);
		boolean source_invalid = minor_route.contains(source) && !minor_route.get(0).equals(source);
		boolean target_invalid = minor_route.contains(target) && !minor_route.get(minor_route.size()-1).equals(target);
		
		if (source_invalid && target_invalid)
		{
			// both are between swaps. NO SWAP REQUIRED
			return new ArrayList<Integer>();
		}
		else if (source_invalid)
		{
			// start is between the source swap and target swap
			// so source swap is invalid
			List<Integer> output = new ArrayList<Integer>();
			output.add(target_swapping);
			return output;
		}
		else if (target_invalid)
		{
			// target is between the source swap and target swap
			// so target swap is invalid
			List<Integer> output = new ArrayList<Integer>();
			output.add(source_swapping);

			return output;
		}
		List<Integer> path = FindPath.get_linking_locations(swapping, source_swapping, target_swapping);
		
		
		
		return path;
	}
	
	public static List<Integer> nearestSwapping(LinkingLocations touching, LinkingLocations swapping, Integer location_id)
	{
		Set<Integer> swaps = swapping.getLocations();
		
		if (swaps.contains(location_id))
		{
			// entered location is a swapping location
			List<Integer> out = new ArrayList<Integer>();
			out.add(location_id);
			return out;
		}
		
		List<Integer> root_linked = touching.getLinkedLocations(location_id);
		
		List<Integer> output = new ArrayList<Integer>();
		
		Queue<Integer> toCheck = new LinkedList<Integer>(root_linked);
		Set<Integer> checked = new HashSet<Integer>();
		checked.add(location_id);
		
		// go through the graph looking for swapping stations
		while (output.size() < 2 && toCheck.size() > 0)
		{
			Integer current = toCheck.poll();
			
			// dont try again if checked
			if (checked.contains(current))
			{
				continue;
			}
			
			checked.add(current);
			
			if (swaps.contains(current))
			{
				output.add(current);
				// reached a stop this way
				// don't add its linking locations
				continue;
			}
			
			List<Integer> touch = touching.getLinkedLocations(current);
			for(Integer t: touch)
			{
				if (checked.contains(t)) // don't add checked positions to queue
				{
					continue;
				}
				toCheck.add(t);
			}
			
		}
		
		return output;
	}
	
}