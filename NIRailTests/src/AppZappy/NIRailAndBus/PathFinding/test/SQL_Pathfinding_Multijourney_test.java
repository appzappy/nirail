package AppZappy.NIRailAndBus.PathFinding.test;

import java.util.List;

import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.timetable.Network;
import AppZappy.NIRailAndBus.pathfinding.IPathfindingAlgorithm;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.pathfinding.JourneyPortion;
import AppZappy.NIRailAndBus.pathfinding.SQL_Pathfinding_Multijourney;
import AppZappy.NIRailAndBus.util.L;
import android.test.AndroidTestCase;

public class SQL_Pathfinding_Multijourney_test extends AndroidTestCase
{
	Network network = null;
	Location source = null;
	Location target = null;
	
	IPathfindingAlgorithm algo = null;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		
		LoadData.initialise(getContext());
		algo = new SQL_Pathfinding_Multijourney();
		network = LoadData.getNetwork();
		source = network.get("Portadown");
		target = network.get("Bangor");
	}
	
	

	
	public void test_pathfind_simple_1()
	{
		source = network.get("Portadown");
		target = network.get("Lurgan");
		List<List<Journey>> journeys = algo.findPaths(LoadData.getTimetable(), source, target);
		assertEquals("Number of Days", 3, journeys.size());
		
		List<Journey> day_of_journeys = journeys.get(0);
		
		assertEquals("Actual Results", true, day_of_journeys.size() > 0);
		
		for(int i=0;i<day_of_journeys.size();i++)
		{
			Journey j = day_of_journeys.get(i);
			assertEquals("Single Step", 1, j.size());
			JourneyPortion portion = j.getPortion(0);
			assertEquals("Start Location", true, portion.getStart() == source);
			assertEquals("Target Location", true, portion.getEnd() == target);
		}
	}
	
	public void test_pathfind_simple_2()
	{
		source = network.get("Bangor");
		target = network.get("Bangor West");
		List<List<Journey>> journeys = algo.findPaths(LoadData.getTimetable(), source, target);
		assertEquals("Number of Days", 3, journeys.size());
		
		List<Journey> day_of_journeys = journeys.get(0);
		assertEquals("Actual Results", true, day_of_journeys.size() > 0);
		for(int i=0;i<day_of_journeys.size();i++)
		{
			Journey j = day_of_journeys.get(i);
			assertEquals("Single Step", 1, j.size());
			JourneyPortion portion = j.getPortion(0);
			assertEquals("Start Location", true, portion.getStart() == source);
			assertEquals("Target Location", true, portion.getEnd() == target);
		}
	}
	
	public void test_pathfind_steps_1()
	{
		source = network.get("Portadown");
		target = network.get("GVS");
		List<List<Journey>> journeys = algo.findPaths(LoadData.getTimetable(), source, target);
		assertEquals("Number of Days", 3, journeys.size());
		List<Journey> day_of_journeys = journeys.get(0);
		assertEquals("Actual Results", true, day_of_journeys.size() > 0);
		
		for(int i=0;i<day_of_journeys.size();i++)
		{
			Journey j = day_of_journeys.get(i);
			assertEquals("Multistep min", true, j.size() > 0);
			assertEquals("Multistep max", true, j.size() <= 2);
			JourneyPortion start_portion = j.getFirstPortion();
			assertEquals("Start Location", true, start_portion.getStart() == source);
			JourneyPortion end_portion = j.getFinalPortion();
			assertEquals("Target Location", true, end_portion.getEnd() == target);
			for (int s=0;s<j.size()-1;s++)
			{
				JourneyPortion j1 = j.getPortion(s);
				JourneyPortion j2 = j.getPortion(s+1);
				assertEquals("Linking Point", true, j1.getEnd() == j2.getStart());
			}
		}
	}
	
	public void test_pathfind_steps_botanic_adelaide()
	{
		source = network.get("Botanic");
		target = network.get("Adelaide");
		List<List<Journey>> journeys = algo.findPaths(LoadData.getTimetable(), source, target);
		assertEquals("Number of Days", 3, journeys.size());
		List<Journey> day_of_journeys = journeys.get(0);
		assertEquals("Returned Journeys?", true, day_of_journeys.size() > 0);
		
		for(int i=0;i<day_of_journeys.size();i++)
		{
			Journey j = day_of_journeys.get(i);
			assertEquals("Multistep min", true, j.size() > 0);
			assertEquals("Multistep max", true, j.size() <= 2);
			JourneyPortion start_portion = j.getFirstPortion();
			assertEquals("Start Location", true, start_portion.getStart() == source);
			JourneyPortion end_portion = j.getFinalPortion();
			assertEquals("Target Location", true, end_portion.getEnd() == target);
			for (int s=0;s<j.size()-1;s++)
			{
				JourneyPortion j1 = j.getPortion(s);
				JourneyPortion j2 = j.getPortion(s+1);
				assertEquals("Linking Point", true, j1.getEnd() == j2.getStart());
			}
		}
	}
	
	public void test_pathfind_long_1()
	{
		source = network.get("Portadown");
		target = network.get("Portrush");
		List<List<Journey>> journeys = algo.findPaths(LoadData.getTimetable(), source, target);
		assertEquals("Number of Days", 3, journeys.size());
		
		List<Journey> day_of_journeys = journeys.get(0);
		
		assertEquals("Actual Results", true, day_of_journeys.size() > 0);
		
		int total = 0;
		int items = 0;
		for(List<Journey> jj : journeys)
		{
			for (Journey j : jj)
			{
				total += j.getLength();
				items++;
			}
		}
		L.d("Portadown->Portrush Average Time ("+items+"): " + ((double)total)/items);
		
		
		for(int i=0;i<day_of_journeys.size();i++)
		{
			Journey j = day_of_journeys.get(i);
			
			assertEquals("Multistep min", true, j.size() >= 2);
			assertEquals("Multistep max", true, j.size() <= 4);
			
			JourneyPortion start_portion = j.getFirstPortion();
			assertEquals("Start Location", true, start_portion.getStart() == source);
			JourneyPortion end_portion = j.getFinalPortion();
			assertEquals("Target Location", true, end_portion.getEnd() == target);

			for (int s=0;s<j.size()-1;s++)
			{
				JourneyPortion j1 = j.getPortion(s);
				JourneyPortion j2 = j.getPortion(s+1);
				//L.d("Link: " + j1.getEnd().getRealName() + "->" + j2.getStart().getRealName());
				assertEquals("Linking Point", true, j1.getEnd().get_id() == j2.getStart().get_id());
				assertTrue("Time Clash", j1.getEndTime() <= j2.getStartTime());
			}
		}
	}
	
	public void test_pathfind_enterprise()
	{
		source = network.get("Portadown");
		target = network.get("Belfast Central");
		List<List<Journey>> journeys = algo.findPaths(LoadData.getTimetable(), source, target);
		assertEquals("Number of Days", 3, journeys.size());
		
		List<Journey> day_of_journeys = journeys.get(0);
		
		assertEquals("Actual Results", true, day_of_journeys.size() > 0);
		
		
		int total = 0;
		int items = 0;
		for(List<Journey> jj : journeys)
		{
			for (Journey j : jj)
			{
				total += j.getLength();
				items++;
			}
		}
		L.d("Enterprise Average Time ("+items+"): " + ((double)total)/items);
		
		
		boolean enterprise_route = false;
		
		for(int i=0;i<day_of_journeys.size();i++)
		{
			Journey j = day_of_journeys.get(i);
			
			assertEquals("Multistep min", true, j.size() >= 1);
			assertEquals("Multistep max", true, j.size() <= 2);
			
			JourneyPortion start_portion = j.getFirstPortion();
			assertEquals("Start Location", true, start_portion.getStart() == source);
			
			JourneyPortion end_portion = j.getFinalPortion();
			assertEquals("Target Location", true, end_portion.getEnd() == target);

			
			for (int s=0;s<j.size()-1;s++)
			{
				JourneyPortion j1 = j.getPortion(s);
				JourneyPortion j2 = j.getPortion(s+1);
				
				assertEquals("Linking Point", true, j1.getEnd() == j2.getStart());
				assertTrue("Time Clash", j1.getEndTime() <= j2.getStartTime());
			}
			
			if (j.size() == 1 && j.getFirstPortion().getRoute().get_service().getRouteSummary().contains("Dublin"))
			{
				// using dublin enterprise service
				
				enterprise_route = true;
			}
			
		}
		
		assertTrue("No Enterprise services", enterprise_route);
	}
}
