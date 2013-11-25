package AppZappy.NIRailAndBus.ui.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.data.model.Vehicle;
import AppZappy.NIRailAndBus.events.Event;
import AppZappy.NIRailAndBus.util.L;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class StationMap_TrainOverlay extends ItemizedOverlay<OverlayItem>
{
	private List<MoveableOverlayItem> _mOverlays = new ArrayList<MoveableOverlayItem>();
	private List<Vehicle> _vehicles = new ArrayList<Vehicle>();
	private Set<Route> _routes = new HashSet<Route>();
	
	public StationMap_TrainOverlay(Drawable marker)
	{
		super(boundCenter(marker));
		setLastFocusedIndex(-1);
		populate();
	}
	
	public StationMap_TrainOverlay(Drawable marker, boolean boundBottom)
	{
		super(boundCenterBottom(marker));
		setLastFocusedIndex(-1);
		populate();
	}
	
	
	
	public void update(short currentTime, double seconds)
	{
		for (int i=0;i<_vehicles.size();i++)
		{
			Vehicle v = _vehicles.get(i);
			MoveableOverlayItem item = _mOverlays.get(i);
			GeoPoint point = v.getCurrentLocation(currentTime, seconds); 
			item.setPoint(point);
		}
		setLastFocusedIndex(-1);
		populate();
	}
	

	public void clear()
	{
		L.d("Clearing Vehicles");
		_mOverlays.clear();
		_vehicles.clear();
		_routes.clear();
		setLastFocusedIndex(-1);
		populate();
	}
	
	public void clear_old (short currentTime)
	{
		List<MoveableOverlayItem> items = new ArrayList<MoveableOverlayItem>(_mOverlays.size());
		List<Vehicle> vech = new ArrayList<Vehicle>(_vehicles.size());
		
		for (int i=0;i<_vehicles.size();i++)
		{
			Vehicle v = _vehicles.get(i); 
			if (v.isValid(currentTime))
			{
				items.add(_mOverlays.get(i));
				vech.add(v);
			}
			else
			{
				// remove this item
				_routes.remove(v.route);
			}
		}
		
		_mOverlays = items;
		_vehicles = vech;
		setLastFocusedIndex(-1);
		populate();
	}
	
	public void add_routes(List<Route> routes, short currentTime, double seconds)
	{
		for (Route r : routes)
		{
			if (!_routes.contains(r))
			{
				_routes.add(r);
				Vehicle v = new Vehicle(r); 
				_vehicles.add(v);
				_mOverlays.add(new MoveableOverlayItem(v.getCurrentLocation(currentTime, seconds), "", ""));
			}
		}
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	protected boolean onTap(int index)
	{
		itemTapped.triggerEvent(_vehicles.get(index).route);
		return true;
	}
	
	public Event itemTapped = new Event();

	@Override
	public void draw(Canvas c, MapView m, boolean shadow)
	{
		if (shadow)
			return;

		super.draw(c, m, shadow);
	}

	@Override
	protected OverlayItem createItem(int i)
	{
		return _mOverlays.get(i);
	}

	@Override
	public int size()
	{
		return _mOverlays.size();
	}

}