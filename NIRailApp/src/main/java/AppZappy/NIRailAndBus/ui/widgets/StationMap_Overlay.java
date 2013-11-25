package AppZappy.NIRailAndBus.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.events.Event;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class StationMap_Overlay extends ItemizedOverlay<OverlayItem>
{
	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private List<Location> locations = new ArrayList<Location>();

	
	public StationMap_Overlay(Drawable marker)
	{
		super(boundCenterBottom(marker));
		setLastFocusedIndex(-1);
		populate();
	}
	
	public StationMap_Overlay(Drawable marker, boolean boundCenter)
	{
		super(boundCenter(marker));
		setLastFocusedIndex(-1);
		populate();
	}
	/**
	 * Add a range of locations to this overlay
	 * 
	 * @param locations The locations to add
	 */
	public void addRange(List<Location> locations)
	{
		for (Location l : locations)
		{
			addOne(l);
		}
		setLastFocusedIndex(-1);
		populate();
	}

	/**
	 * Add a location to this overlay
	 * 
	 * @param location The location
	 */
	public void add(Location location)
	{
		addOne(location);
		setLastFocusedIndex(-1);
		populate();
	}
	
	public Location get(int index)
	{
		return locations.get(index);
	}

	/**
	 * Remove a location from this overlay
	 * 
	 * @param location The location to remove
	 */
	public void remove(Location location)
	{
		int position = locations.indexOf(location);

		if (position >= 0)
		{
			mOverlays.remove(position);
			locations.remove(position);
			setLastFocusedIndex(-1);
			populate();
		}
	}

	private void addOne(Location location)
	{
		locations.add(location);
		GeoPoint point = location.getGeoPoint();
		
		OverlayItem item = new OverlayItem(point, location.getRealName(), location.getRealName());
		mOverlays.add(item);
	}

	@Override
	protected boolean onTap(int index)
	{
		itemTapped.triggerEvent(locations.get(index));
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
		return mOverlays.get(i);
	}

	@Override
	public int size()
	{
		return mOverlays.size();
	}

}
