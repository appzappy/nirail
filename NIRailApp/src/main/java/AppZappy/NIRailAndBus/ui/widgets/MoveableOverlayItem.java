package AppZappy.NIRailAndBus.ui.widgets;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MoveableOverlayItem extends OverlayItem
{
	public MoveableOverlayItem(GeoPoint point, String title, String snippet)
	{
		super(point, title, snippet);
		_point = point;
	}
	
	GeoPoint _point = null;
	
	public void setPoint(GeoPoint point)
	{
		_point = point;
	}
	
	@Override
	public GeoPoint getPoint()
	{
		return _point;
	}

}
