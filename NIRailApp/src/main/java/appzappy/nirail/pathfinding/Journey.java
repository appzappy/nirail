package appzappy.nirail.pathfinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.Route;
import appzappy.nirail.util.timing.TimeFormatter;

/**
 * A journey between 2 points
 */
public class Journey implements Iterable<JourneyPortion>
{
	private List<JourneyPortion> _portions = new ArrayList<JourneyPortion>();


	/**
	 * Get the number of portions involved in this journey
	 * 
	 * @return Number of portions
	 */
	public int size()
	{
		return _portions.size();
	}

	/**
	 * Get a portion of the journey
	 * 
	 * @param index The portion position to get. 0 indexed
	 * @return The requested portion of the journey
	 */
	public JourneyPortion getPortion(int index)
	{
		return _portions.get(index);
	}

	/**
	 * Get the first portion of the journey
	 * @return First portion
	 */
	public JourneyPortion getFirstPortion()
	{
		return _portions.get(0);
	}

	/**
	 * Get the last portion of this journey
	 * @return Final portion
	 */
	public JourneyPortion getFinalPortion()
	{
		return _portions.get(_portions.size()-1);
	}
	
	/**
	 * Add a journey portion to the journey
	 * @param portion The portion to add
	 */
	public void addPortion(JourneyPortion portion)
	{
		_portions.add(portion);
	}

	/**
	 * Get the length of this journey using the default interchange time
	 * @return Length of journey in seconds
	 */
	public String getLengthFormatted()
	{
		return TimeFormatter.formattedStringFromLength((short)getLength());
	}
	
	/**
	 * Get the length of this journey using the default interchange time
	 * @return Length of journey in seconds
	 */
	public int getLength()
	{
		int time = getFinalPortion().getEndTime() - getFirstPortion().getStartTime();
		return time;
	}
	


	/**
	 * Get the number of changes for this journey
	 * @return Number of changes for this journey. If 2 trains, then returns 1; if 3, returns 2 etc. If no trains, returns -1
	 */
	public int getNumberChanges()
	{
		final int size = _portions.size();
		return size - 1;
	}
	
	
	/**
	 * Get the starting time of the journey
	 * @return The starting time
	 */
	public short getStartingTime()
	{
		if (_portions.size() == 0)
			return -1;
		
		return _portions.get(0).getStartTime();
	}
	
	/**
	 * Get the starting time of the journey in a human readable format
	 * @return 
	 */
	public String getStartingTimeFormated()
	{
		short time = getStartingTime();
		if (time < 0)
			return "invalid";
		return TimeFormatter.formattedStringFromTime(time);
	}
	
	/**
	 * Get the ending time of the journey 
	 * @return The ending time
	 */
	public short getEndingTime()
	{
		if (_portions.size() == 0)
			return -1;
		
		return _portions.get(_portions.size()-1).getEndTime();
	}
	
	/**
	 * Get the ending time of the journey in a human readable format
	 * @return 
	 */
	public String getEndingTimeFormated()
	{
		short time = getEndingTime();
		if (time < 0)
			return "invalid";
		return TimeFormatter.formattedStringFromTime(time);
	}
	
	/**
	 * Get the starting location
	 * @return
	 */
	public Location getStartingLocation()
	{
		return getFirstPortion().getStart();
	}
	
	/**
	 * Get the ending location
	 * @return
	 */
	public Location getEndingLocation()
	{
		return getFinalPortion().getEnd();
	}
	
	/**
	 * Get the starting location name
	 * @return
	 */
	public String getStartingLocationName()
	{
		return getFirstPortion().getStart().getRealName();
	}
	
	/**
	 * Get the destination name
	 * @return
	 */
	public String getEndingLocationName()
	{
		return getFinalPortion().getEnd().getRealName();
	}
	
	@Override
	public String toString()
	{
		return "Journey";
	}

	
	/**
	 * Copy into this journey all journey portions found within journey j
	 * @param j The journey to include in active journey
	 */
	public void copyIn(Journey j)
	{
		for (int i=0;i<j.size();i++)
		{
			JourneyPortion portion = j.getPortion(i);
			
			this.addPortion(portion.clone());
		}
	}

	
	/**
	 * The iterator object for this object
	 */
	public Iterator<JourneyPortion> iterator()
	{
		return _portions.iterator();
	}

	public void compact()
	{
		List<JourneyPortion> portions = new ArrayList<JourneyPortion>();
		for (int i=0;i<size();i++)
		{
			JourneyPortion startPortion = getPortion(i);
			
			int route_id = startPortion.getRoute().get_id();
			
			int matching_index = i;
			for (int j=i+1;j<size();j++)
			{
				int next_route_id = getPortion(j).getRoute().get_id();
				if (route_id == next_route_id)
				{
					matching_index = j;
				}
				else
				{
					// not the same so stop
					// previous is the match
					break;
				}
			}
			
			if (matching_index != i)
			{
				// then there are pairings to be made
				JourneyPortion endPortion = getPortion(matching_index);
				
				Location start = startPortion.getStart();
				short startTime = startPortion.getStartTime();
				Location end = endPortion.getEnd();
				short endTime = endPortion.getEndTime();
				Route route = startPortion.getRoute();
				
				JourneyPortion newPortion = JourneyPortion.create(start, startTime, end, endTime, route);
				portions.add(newPortion);
				i = matching_index;
			}
			else
			{
				// no pairings
				portions.add(startPortion);
			}
		}
		// save the new compacted list
		
		this._portions = portions;
	}
}
