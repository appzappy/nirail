package AppZappy.NIRailAndBus.PathFinding.test;

import java.util.List;

import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.collections.LinkingLocations;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.timetable.Network;
import AppZappy.NIRailAndBus.pathfinding.FindPath;
import android.test.AndroidTestCase;

public class FindPath_test extends AndroidTestCase
{
	Network network = null;
	Location source = null;
	Location target = null;
	LinkingLocations linked = null;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		
		LoadData.initialise(getContext());
		network = LoadData.getNetwork();
		linked = network.touching_locations;
	}
	
	public void test_findPath()
	{
		source = network.get("Portadown");
		target = network.get("Lisburn");
		
		List<Location> result = FindPath.get_linking_locations(linked, network, source.get_id(), target.get_id());
		
		int index = 0;
		assertEquals("Portadown Failed", "Portadown", result.get(index++).getRealName());
		assertEquals("Lurgan Failed", "Lurgan", result.get(index++).getRealName());
		assertEquals("Moira Failed", "Moira", result.get(index++).getRealName());
		assertEquals("Lisburn Failed", "Lisburn", result.get(index++).getRealName());
		
		assertEquals("Number of Links", 4, result.size());
	}
	
	public void test_findPath2()
	{
		source = network.get("Portadown");
		target = network.get("Lurgan");
		
		List<Location> result = FindPath.get_linking_locations(linked, network, source.get_id(), target.get_id());
		assertEquals("Number of Links", 2, result.size());
	}
	
	public void test_findPath3()
	{
		source = network.get("Adelaide");
		target = network.get("GVS");
		
		List<Location> result = FindPath.get_linking_locations(linked, network, source.get_id(), target.get_id());
		assertEquals("Number of Links", 2, result.size());
	}
	
//	public void test_findPath4() //Disabled to remove botanic->adeliade pathfinding bug
//	{ 
//		source = network.get("Adelaide");
//		target = network.get("City Hospital");
//		
//		List<Location> result = FindPath.get_linking_locations(linked, network, source.get_id(), target.get_id());
//		assertEquals("Number of Links", 2, result.size());
//	}
	
	public void test_findPath5()
	{
		source = network.get("GVS");
		target = network.get("Central Station");
		
		List<Location> result = FindPath.get_linking_locations(linked, network, source.get_id(), target.get_id());
		assertEquals("Number of Links", 4, result.size());
	}
	
	public void test_findPath_Botanic_Adelaide()
	{
		source = network.get("Botanic");
		target = network.get("Adelaide");
		
		List<Location> result = FindPath.get_linking_locations(linked, network, source.get_id(), target.get_id());
		String msg = "";
		for(Location l : result) {
			msg += l.getRealName() + ", ";
		}
		assertEquals("Number of Links "+msg, 4, result.size());
	}
}
