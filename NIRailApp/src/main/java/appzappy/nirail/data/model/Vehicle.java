package appzappy.nirail.data.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


import appzappy.nirail.data.LoadData;
import appzappy.nirail.geolocation.Geolocation;
import appzappy.nirail.util.L;

public class Vehicle
{
	public Route route = null;
	
	public Vehicle (Route route)
	{
		this.route = route;
	}
	
	private int previousStop_position = 0;
	
	private Stop previous = null;
	private Stop next = null;
	
	private int timeDiff = 0;
	
	private List<VehicleCheckPoint> checkpoints = new ArrayList<VehicleCheckPoint>();
	
	public static class VehicleCheckPoint
	{
		public VehicleCheckPoint(Location l)
		{
			location = l.getGeoPoint();
		}
		public LatLng location = null;
		public float proportion_from_last = 0.0f;
	}
	
	public LatLng getCurrentLocation (short currentTime, double seconds)
	{
		if (next == null || next.getTime() <= currentTime)
		{
			// next stop not set or next stop has already occurred
			if (route.getFinalStop().getTime() < currentTime)
			{
				// route finished
				return route.getFinalStop().getLocation().getGeoPoint(); 
			}
			
			// find new previous stop AND next stop data
			int nextStop_position = route.countStops()-1;
			
			for (int i=previousStop_position+1;i<route.countStops();i++)
			{
				Stop s = route.getStop(i);
				if (s.getTime() > currentTime)
				{
					nextStop_position = i;
					break;
				}
			}
			previousStop_position = nextStop_position - 1;
			
			previous = route.getStop(previousStop_position);
			next = route.getStop(nextStop_position);
			
			timeDiff = next.getTime() - previous.getTime();
			
			// update the intermediate positions
			checkpoints.clear();
			List<Location> locations = LoadData.getNetwork().touching_locations.getPath(previous.getLocation().get_id(), next.getLocation().get_id());
			for(Location l : locations)
			{
				checkpoints.add(new VehicleCheckPoint(l));
			}
			
			// calculate the distances between each of the locations
			List<Float> distances = new ArrayList<Float>();
			distances.add(0.0f);
			float total_distance = 0;
			for (int i=1;i<locations.size();i++)
			{
				Location p_loc = locations.get(i-1);
				Location n_loc = locations.get(i);
				float dis = n_loc.getDistanceBetweenLocations(p_loc);
				distances.add(dis);
				total_distance += dis;
			}
			if (total_distance < 0.1)
			{
				checkpoints.get(checkpoints.size()-1).proportion_from_last = 1.0f;
			}
			else
			{
				// set the proportion distance 
				for (int i=1;i<distances.size();i++)
				{
					float proportion = distances.get(i)/total_distance;
					checkpoints.get(i).proportion_from_last = proportion;
				}
			}
			
		}
		
		
		int time_gap = currentTime - previous.getTime();
		double time_gap_d = ((double) time_gap) + seconds;
		
		float proportion = (float) (time_gap_d / timeDiff);
		
		if (proportion < 0)
		{
			// before the start
			return previous.getLocation().getGeoPoint();
		}
		if (proportion > 1)
		{
			// after the end of next stop
			return next.getLocation().getGeoPoint();
		}
		
		float total = 0;
		for(int i=1;i<checkpoints.size();i++)
		{
			VehicleCheckPoint v = checkpoints.get(i);
			total += v.proportion_from_last;
			
			if (total >= proportion )
			{
				total -= v.proportion_from_last;
				
				float mini_portion = (proportion - total) / v.proportion_from_last;

				// smoothly go from gap to gap
				LatLng point_previous = checkpoints.get(i-1).location;
				LatLng point_next = v.location;
				
				LatLng mid = Geolocation.linearStretch(point_previous, point_next, mini_portion);
				return mid;
			}
		}
				
		L.d("NEVER: Check" + checkpoints.size() + " " + route.toString());
		
		return route.getFinalStop().getLocation().getGeoPoint();
	}
	
	public boolean isValid(short currentTime)
	{
		boolean res = route.getFinalStop().getTime() >= currentTime;
		return res;
	}
}
