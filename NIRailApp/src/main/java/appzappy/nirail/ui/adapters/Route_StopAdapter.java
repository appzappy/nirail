package appzappy.nirail.ui.adapters;

import java.util.List;

import appzappy.nirail.R;
import appzappy.nirail.data.model.Stop;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Route_StopAdapter extends ArrayAdapter<Stop>
{
	public Route_StopAdapter(Context c)
	{
		super(c, R.layout.ui_route_view_item);
	}
	
	public List<Stop> highlightStops = null;
	public int highlightColor = 0;
	/**
	 * Set the stops that are to be highlighted in the view
	 * @param stops
	 * @param color
	 */
	public void setHighlightedStops(List<Stop> stops, int color)
	{
		this.highlightStops = stops;
		this.highlightColor = color;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		RouteView_ListViewItemHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_route_view_item, parent, false);
			holder = new RouteView_ListViewItemHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (RouteView_ListViewItemHolder) row.getTag();
		}

		Stop stop = getItem(position);
		if (highlightStops != null && highlightStops.contains(stop))
		{
			holder.populateFrom(stop, highlightColor);
		}
		else
		{
			holder.populateFrom(stop, 0);
		}

		return row;
	}

	static class RouteView_ListViewItemHolder
	{
		private TextView destination = null;
		private TextView time = null;
		private View row = null;

		RouteView_ListViewItemHolder(View row)
		{
			destination = (TextView) row.findViewById(R.id.routeView_listview_item_location);
			time = (TextView) row.findViewById(R.id.routeView_listview_item_time);
			this.row = row;
		}

		void populateFrom(Stop stop, int color)
		{
			row.setBackgroundColor(color);
			
			destination.setText(stop.getLocation().getRealName());
			time.setText(stop.getFormatedTime());
		}

	}
	
}