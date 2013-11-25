package AppZappy.NIRailAndBus.pathfinding;


import AppZappy.NIRailAndBus.data.*;
import AppZappy.NIRailAndBus.data.enums.TransportType;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.util.timing.TimeFormatter;

/**
 * A part of a path between two locations
 */
public class JourneyPortion
{
	private Location _start = null;
	private Location _end = null;
	private Route _route = null;
	private short _startTime = 0;
	private short _endTime = 0;

	/**
	 * Get the starting time for this portion
	 * 
	 * @return Starting time
	 */
	public short getStartTime()
	{
		return _startTime;
	}
	
	/**
	 * Get the start time formatted into a string
	 * @return
	 */
	public String getStartTimeFormatted()
	{
		short time = getStartTime();
		if (time < 0)
			return "invalid";
		return TimeFormatter.formattedStringFromTime(time);
	}

	/**
	 * Get the ending time for this portion
	 * 
	 * @return Ending time
	 */
	public short getEndTime()
	{
		return _endTime;
	}

	/**
	 * Get the end time formatted into a string
	 * @return
	 */
	public String getEndTimeFormatted()
	{
		short time = getEndTime();
		if (time < 0)
			return "invalid";
		return TimeFormatter.formattedStringFromTime(time);
	}
	
	/**
	 * Get the length of this portion
	 * @return
	 */
	public String getLengthFormatted()
	{
		return TimeFormatter.formattedStringFromLength((short)(getEndTime()-getStartTime()));
	}
	
	/**
	 * Get the type of transport
	 * @return
	 */
	public TransportType getTransportType()
	{
		return _route.getTransportType();
	}

	/**
	 * Get the starting location for this portion
	 * 
	 * @return Starting Location
	 */
	public Location getStart()
	{
		return _start;
	}

	/**
	 * Get the ending location for this portion
	 * 
	 * @return Ending location
	 */
	public Location getEnd()
	{
		return _end;
	}

	/**
	 * Get the route followed for this portion
	 * 
	 * @return Route used
	 */
	public Route getRoute()
	{
		return _route;
	}

	/**
	 * Get the position of the start of this journey portion in the route
	 * @return
	 */
	public int getStartPositionInRoute()
	{
		return _route.getPositionOfStop(_start, _startTime);
	}

	/**
	 * Get the position of the end of this journey portion in the route
	 * @return
	 */
	public int getEndPositionInRoute()
	{
		return _route.getPositionOfStop(_end, _endTime);
	}
	
	private JourneyPortion() { }
	
	/**
	 * Create a new PathPortion object
	 * 
	 * @param start The starting point for this portion
	 * @param startTime The starting time for this portion
	 * @param end The ending point for this portion
	 * @param endTime The ending time for this portion
	 * @param route The route this portion follows
	 * @return Newly created PathPortion object
	 */
	public static JourneyPortion create(Location start, short startTime,
			Location end, short endTime, Route route)
	{
		JourneyPortion pp = new JourneyPortion();
		pp._start = start;
		pp._end = end;
		pp._startTime = startTime;
		pp._endTime = endTime;
		pp._route = route;
		return pp;
	}
	
	@Override
	public JourneyPortion clone()
	{
		return JourneyPortion.create(_start, _startTime, _end, _endTime, _route);
	}
	
	@Override
	public String toString()
	{
		return "JourneyPortion: " + _start.getRealName() + "@" + _startTime + ". " + _end.getRealName() + "@" + _endTime +". R:"+_route.get_id();
	}
}