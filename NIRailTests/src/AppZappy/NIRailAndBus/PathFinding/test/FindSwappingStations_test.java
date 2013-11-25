package AppZappy.NIRailAndBus.PathFinding.test;

import java.util.List;

import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.collections.LinkingLocations;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.timetable.Network;
import AppZappy.NIRailAndBus.pathfinding.FindPath;
import AppZappy.NIRailAndBus.pathfinding.FindSwappingStations;
import android.test.AndroidTestCase;

public class FindSwappingStations_test extends AndroidTestCase
{
	Network network = null;
	Location source = null;
	Location target = null;
	LinkingLocations linked = null;
	LinkingLocations swapping = null;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		
		LoadData.initialise(getContext());
		network = LoadData.getNetwork();
		linked = network.touching_locations;
		swapping = network.swapping_locations;
	}
	
	


	public void test_find_swap_1()
	{
		source = network.get("Portadown");
		target = network.get("GVS");
		
		List<Integer> result = FindSwappingStations.swapping_path(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 3, result.size());
		
		int index = 0;
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(index++));
		assertEquals("Lisburn Failed", network.get("Lisburn").get_id(), (int)result.get(index++));
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
	}
	
	public void test_find_swap_2()
	{
		source = network.get("Lurgan");
		target = network.get("GVS");
		
		List<Integer> result = FindSwappingStations.swapping_path(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 2, result.size());
		
		int index = 0;
		assertEquals("Lisburn Failed", network.get("Lisburn").get_id(), (int)result.get(index++));
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
		
	}
	
	public void test_find_swap_3()
	{
		source = network.get("GVS");
		target = network.get("Bangor");
		
		List<Integer> result = FindSwappingStations.swapping_path(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 3, result.size());
		
		int index = 0;
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
		assertEquals("Central Failed", network.get("Central Station").get_id(), (int)result.get(index++));
		assertEquals("Holywood", network.get("Holywood").get_id(), (int)result.get(index++));
		
	}
	
	public void test_find_swap_4()
	{
		source = network.get("Portadown");
		target = network.get("Portrush");
		
		List<Integer> result = FindSwappingStations.swapping_path(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 6, result.size());
		
		int index = 0;
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(index++));
		assertEquals("Lisburn Failed", network.get("Lisburn").get_id(), (int)result.get(index++));
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
		assertEquals("Central Failed", network.get("Central Station").get_id(), (int)result.get(index++));
		assertEquals("Ballymena", network.get("Ballymena").get_id(), (int)result.get(index++));
		assertEquals("Coleraine", network.get("Coleraine").get_id(), (int)result.get(index++));
		
	}

	
	public void test_find_swap_inc_ends_1()
	{
		source = network.get("Portadown");
		target = network.get("GVS");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 3, result.size());
		
		int index = 0;
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(index++));
		assertEquals("Lisburn Failed", network.get("Lisburn").get_id(), (int)result.get(index++));
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
		
	}
	
	public void test_find_swap_inc_ends_2()
	{
		source = network.get("Lurgan");
		target = network.get("GVS");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 3, result.size());
		
		int index = 0;
		assertEquals("Lurgan Failed", network.get("Lurgan").get_id(), (int)result.get(index++));
		assertEquals("Lisburn Failed", network.get("Lisburn").get_id(), (int)result.get(index++));
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
		
	}
	
	public void test_find_swap_inc_ends_3()
	{
		source = network.get("GVS");
		target = network.get("Bangor");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 4, result.size());
		
		int index = 0;
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
		assertEquals("Central Failed", network.get("Central Station").get_id(), (int)result.get(index++));
		assertEquals("Holywood", network.get("Holywood").get_id(), (int)result.get(index++));
		assertEquals("Bangor", network.get("Bangor").get_id(), (int)result.get(index++));
		
	}
	
	public void test_find_swap_inc_ends_4()
	{
		source = network.get("Portadown");
		target = network.get("Portrush");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 7, result.size());
		
		int index = 0;
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(index++));
		assertEquals("Lisburn Failed", network.get("Lisburn").get_id(), (int)result.get(index++));
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
		assertEquals("Central Failed", network.get("Central Station").get_id(), (int)result.get(index++));
		assertEquals("Ballymena", network.get("Ballymena").get_id(), (int)result.get(index++));
		assertEquals("Coleraine", network.get("Coleraine").get_id(), (int)result.get(index++));
		assertEquals("Portrush", network.get("Portrush").get_id(), (int)result.get(index++));
	}
	

	public void test_find_swap_inc_ends_5()
	{
		source = network.get("Portadown");
		target = network.get("Lurgan");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 2, result.size());
		
		int index = 0;
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(index++));
		assertEquals("Lurgan Failed", network.get("Lurgan").get_id(), (int)result.get(index++));
	}
	
	public void test_find_swap_inc_ends_6()
	{
		source = network.get("Portadown");
		target = network.get("Moira");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 2, result.size());
		
		int index = 0;
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(index++));
		assertEquals("Moira Failed", network.get("Moira").get_id(), (int)result.get(index++));
	}
	
	public void test_find_swap_inc_ends_7()
	{
		source = network.get("Lurgan");
		target = network.get("Moira");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 2, result.size());
		
		int index = 0;
		assertEquals("Lurgan Failed", network.get("Lurgan").get_id(), (int)result.get(index++));
		assertEquals("Moira Failed", network.get("Moira").get_id(), (int)result.get(index++));
	}
	
	public void test_find_swap_inc_ends_8()
	{
		source = network.get("Bangor");
		target = network.get("Bangor West");
		
		List<Integer> result = FindSwappingStations.swapping_path_inc_ends(linked, swapping, source.get_id(), target.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Links", 2, result.size());
		
		int index = 0;
		assertEquals("Bangor Failed", network.get("Bangor").get_id(), (int)result.get(index++));
		assertEquals("Bangor West Failed", network.get("Bangor West").get_id(), (int)result.get(index++));
	}
	
	
	public void test_find_nearestSwaps_1()
	{
		source = network.get("Portadown");
		List<Integer> result = FindSwappingStations.nearestSwapping(linked, swapping, source.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Results", 1, result.size());
		
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(0));
	}
	
	public void test_find_nearestSwaps_2()
	{
		source = network.get("Lurgan");
		List<Integer> result = FindSwappingStations.nearestSwapping(linked, swapping, source.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Results", 2, result.size());
		
		int index = 0;
		assertEquals("Portadown Failed", network.get("Portadown").get_id(), (int)result.get(index++));
		assertEquals("Lisburn Failed", network.get("Lisburn").get_id(), (int)result.get(index++));
	}

	public void test_find_nearestSwaps_3()
	{
		source = network.get("Botanic");
		List<Integer> result = FindSwappingStations.nearestSwapping(linked, swapping, source.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Results", 2, result.size());
		
		int index = 0;
		assertEquals("Central Failed", network.get("Central Station").get_id(), (int)result.get(index++));
		assertEquals("GVS Failed", network.get("Great Victoria").get_id(), (int)result.get(index++));
	}
	public void test_find_nearestSwaps_4()
	{
		source = network.get("Portrush");
		List<Integer> result = FindSwappingStations.nearestSwapping(linked, swapping, source.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Results", 1, result.size());
		
		int index = 0;
		assertEquals("Coleraine Failed", network.get("Coleraine").get_id(), (int)result.get(index++));
	}
	public void test_find_nearestSwaps_5()
	{
		source = network.get("Bangor");
		List<Integer> result = FindSwappingStations.nearestSwapping(linked, swapping, source.get_id());

		assertFalse("Not Null", result == null);
		assertEquals("Number of Results", 1, result.size());
		
		int index = 0;
		assertEquals("Holywood Failed", network.get("Holywood").get_id(), (int)result.get(index++));
	}
}
