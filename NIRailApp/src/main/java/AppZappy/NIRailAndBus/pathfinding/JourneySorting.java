package AppZappy.NIRailAndBus.pathfinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Used to sort arrays of Journeys according to starting time or by ending time
 */
public class JourneySorting
{
	private JourneySorting() {}
	
	/**
	 * Sort a set of journeys with ascending start time
	 * @param journeys The journeys
	 */
	public static void sortByStartTime(List<Journey> journeys)
	{
		Collections.sort(journeys, new StartTimeComparator());
	}
	
	/**
	 * Sort a set of journeys with ascending end time
	 * @param journeys The journeys
	 */
	public static void sortByEndTime(List<Journey> journeys)
	{
		Collections.sort(journeys, new EndTimeComparator());
	}
	
	/**
	 * Sort a set of journeys with descending start time
	 * @param journeys The journeys
	 */
	public static void sortByStartTimeDescending(List<Journey> journeys)
	{
		Collections.sort(journeys, new StartTimeComparatorDesc());
	}
	
	/**
	 * Sort a set of journeys with descending end time
	 * @param journeys The journeys
	 */
	public static void sortByEndTimeDescending(List<Journey> journeys)
	{
		Collections.sort(journeys, new EndTimeComparatorDesc());
	}
	
	private static class StartTimeComparator implements Comparator<Journey>
	{
		public int compare(Journey o1, Journey o2)
		{
			return o1.getStartingTime() - o2.getStartingTime();
		}
		
		@Override
		public String toString()
		{
			return "Start Time Comparator";
		}
	}
	
	private static class StartTimeComparatorDesc implements Comparator<Journey>
	{
		public int compare(Journey o1, Journey o2)
		{
			return o2.getStartingTime() - o1.getStartingTime();
		}
		
		@Override
		public String toString()
		{
			return "Start Time Comparator Desc";
		}
	}
	
	private static class EndTimeComparator implements Comparator<Journey>
	{
		public int compare(Journey o1, Journey o2)
		{
			return o1.getEndingTime() - o2.getEndingTime();
		}
		
		@Override
		public String toString()
		{
			return "End Time Comparator";
		}
	}
	
	private static class EndTimeComparatorDesc implements Comparator<Journey>
	{
		public int compare(Journey o1, Journey o2)
		{
			return o2.getEndingTime() - o1.getEndingTime();
		}
		
		@Override
		public String toString()
		{
			return "End Time Comparator Desc";
		}
	}
	
	@Override
	public String toString()
	{
		return "JourneySorting";
	}
}
