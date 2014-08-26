package appzappy.nirail.pathfinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import appzappy.nirail.data.enums.TransportType;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.Route;
import appzappy.nirail.data.model.Stop;

public class RouteStopPosition
{
	public RouteStopPosition(Route route, int position)
	{
		this.route = route;
		this.position = position;
	}
	
	public Route route = null;
	public int position = 0;

	
	public boolean isFinalStop()
	{
		return position == route.countStops() -1;
	}
	
	public short getTime()
	{
		return route.getStop(position).getTime();
	}
	
	public String getFormattedTime()
	{
		return route.getStop(position).getFormatedTime();
	}
	
	
	
	public Location getFinalStop_Location()
	{
		return route.getFinalStop().getLocation();
	}
	public String getFinalStop_Name()
	{
		return route.getFinalStop().getLocation().getRealName();
	}
	
	public short getFinalStop_Time()
	{
		return route.getFinalStop().getTime();
	} 

	public String getFinalStop_FormattedTime()
	{
		return route.getStop(route.countStops()-1).getFormatedTime();
	}
	
	
	public Stop getCurrentStop()
	{
		return route.getStop(position);
	}
	
	public Location getLocation()
	{
		return route.getStop(position).getLocation();
	}
	
	public String getLocationName()
	{
		return route.getStop(position).getLocation().getRealName();
	}
	
	public TransportType getTransportType()
	{
		return route.getTransportType();
	}
	
	public static void sortByStartTime(List<RouteStopPosition> routes)
	{
		if (routes.size() == 0)
			return;
		Collections.sort(routes, new StartTimeComparator());
	}
	
	public static class StartTimeComparator implements Comparator<RouteStopPosition>
	{
		public int compare(RouteStopPosition o1, RouteStopPosition o2)
		{
			short starting1 = o1.route.getStop(o1.position).getTime();
			short starting2 = o2.route.getStop(o2.position).getTime();
			return starting1 - starting2;
		}
	}
	public static class StartTimeComparatorDesc implements Comparator<RouteStopPosition>
	{
		public int compare(RouteStopPosition o1, RouteStopPosition o2)
		{
			short starting1 = o1.route.getStop(o1.position).getTime();
			short starting2 = o2.route.getStop(o2.position).getTime();
			return starting2 - starting1;
		}
	}
	
}
