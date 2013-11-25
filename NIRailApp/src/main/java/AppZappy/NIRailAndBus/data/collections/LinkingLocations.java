package AppZappy.NIRailAndBus.data.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.pathfinding.FindPath;

/**
 * A collection of linking locations
 * @author Kurru
 *
 */
public class LinkingLocations
{
	private Map<Integer, List<Integer>> _data = new HashMap<Integer, List<Integer>>();
	
	/**
	 * Get all the linking locations at a location
	 * @param location The location
	 * @return The locations that link with requested location
	 */
	public List<Integer> getLinkedLocations(Integer location)
	{
		return _data.get(location);
	}

	/**
	 * Add a link to the dataset
	 * @param location1_id Location 1
	 * @param location2_id Location 2
	 */
	public void addLink(Integer location1_id, Integer location2_id)
	{
		if (!_data.containsKey(location1_id))
		{
			_data.put(location1_id, new ArrayList<Integer>());
		}
		if (!_data.containsKey(location2_id))
		{
			_data.put(location2_id, new ArrayList<Integer>());
		}
		
		_data.get(location1_id).add(location2_id);
		_data.get(location2_id).add(location1_id);
	}
	
	/**
	 * Get the shortest path between two locations
	 * @param start_id Starting point
	 * @param target_id Target point
	 * @return List of locations from start to target inclusive
	 */
	public List<Location> getPath(Integer start_id, Integer target_id)
	{
		return FindPath.get_linking_locations(this, LoadData.getNetwork(), start_id, target_id);
	}
	
	/**
	 * Get the locations in this dataset
	 * @return Set of integers representing Location.get_id()
	 */
	public Set<Integer> getLocations()
	{
		return _data.keySet();
	}
	
}
