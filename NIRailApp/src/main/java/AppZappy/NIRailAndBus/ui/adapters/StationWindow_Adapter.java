package AppZappy.NIRailAndBus.ui.adapters;

import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.pathfinding.RouteStopPosition;
import AppZappy.NIRailAndBus.ui.UIUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationWindow_Adapter extends ArrayAdapter<RouteStopPosition>
{
	public StationWindow_Adapter(Context c)
	{
		super(c, R.layout.ui_station_window_listview_item);
	}

	private Location _station = null;
	
	public void setStation(Location location)
	{
		_station = location;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		StationWindow_Holder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_station_window_listview_item, parent, false);
			holder = new StationWindow_Holder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (StationWindow_Holder) row.getTag();
		}

		holder.populateFrom(getItem(position), _station);

		return row;
	}

	static class StationWindow_Holder
	{
		private TextView topline_journeyDetails = null;
		private TextView bottomLine_Time = null;
		private ImageView icon = null;

		StationWindow_Holder(View row)
		{
			topline_journeyDetails = (TextView) row.findViewById(R.id.stationWindow_listview_item_text);
			bottomLine_Time = (TextView) row.findViewById(R.id.stationWindow_listview_item_distance);
			icon = (ImageView) row.findViewById(R.id.stationWindow_listview_item_icon);
		}

		void populateFrom(RouteStopPosition routeStopPosition, Location finalLocation)
		{
			String topLine = null;
			String bottomLine = null;
			
			if (routeStopPosition.getFinalStop_Location().get_id() == finalLocation.get_id()) // this is the final station
			{
				topLine = routeStopPosition.getFormattedTime() + " from " + routeStopPosition.route.getFirstStop().getLocation().getRealName();
				bottomLine = "Terminating here at " + routeStopPosition.getFinalStop_FormattedTime();
			}
			else
			{
				topLine = routeStopPosition.getFormattedTime() + " to " + routeStopPosition.getFinalStop_Name();
				bottomLine = "Arrives at " + routeStopPosition.getFinalStop_FormattedTime();
			}
			topline_journeyDetails.setText(topLine);
			bottomLine_Time.setText(bottomLine);
			icon.setImageResource(UIUtils.getImageForLocationType(routeStopPosition.getTransportType()));
		}

	}
}