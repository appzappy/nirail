package AppZappy.NIRailAndBus.PathFinding.test;


import java.util.List;

import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.timetable.Network;
import AppZappy.NIRailAndBus.pathfinding.IPathfindingAlgorithm;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.pathfinding.JourneyPortion;
import AppZappy.NIRailAndBus.pathfinding.SQL_Pathfinding_OnSameService;
import android.test.AndroidTestCase;

public class SQL_Pathfinding_OnSameService_test extends AndroidTestCase
{
	IPathfindingAlgorithm algo = null;
	Network network = null;
	Location source = null;
	Location target = null;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		
		LoadData.initialise(getContext());
		algo = new SQL_Pathfinding_OnSameService();
		network = LoadData.getNetwork();
		source = network.get("Portadown");
		target = network.get("Bangor");
	}
	
	

	
	public void test_pathfind()
	{
		List<List<Journey>> journeys = algo.findPaths(LoadData.getTimetable(), source, target);
		assertEquals("Number of Days", 3, journeys.size());
		
		assertEquals("Actual Results", true, journeys.get(0).size() > 0);
		
		List<Journey> day_of_journeys = journeys.get(0);
		for(int i=0;i<day_of_journeys.size();i++)
		{
			Journey j = day_of_journeys.get(i);
			
			assertEquals("Single Step", 1, j.size());
			
			JourneyPortion portion = j.getPortion(0);
			
			
			assertEquals("Start Location", true, portion.getStart() == source);
			assertEquals("Target Location", true, portion.getEnd() == target);
			
		}
		
	}

}
