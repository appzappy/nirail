package appzappy.nirail.ui.adapters;


import appzappy.nirail.R;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.LocationDistance;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.ui.UIUtils;
import appzappy.nirail.ui.widgets.ToggleImageButton;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationList_Adapter extends ArrayAdapter<LocationDistance>
{
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	
	public StationList_Adapter(Context c)
	{
		super(c, R.layout.ui_station_finder_listview_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		StationFinder_ListViewItemHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_station_finder_listview_item, parent, false);
			holder = new StationFinder_ListViewItemHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (StationFinder_ListViewItemHolder) row.getTag();
		}

		holder.populateFrom(getItem(position));

		return row;
	}

	public static class StationFinder_ListViewItemHolder
	{
		private TextView destination = null;
		private TextView distance = null;
		private ImageView icon = null;
		private ToggleImageButton favButton = null;
		public LocationDistance currentData = null;
		
		StationFinder_ListViewItemHolder(View row)
		{
			destination = (TextView) row.findViewById(R.id.stationFinder_listview_item_location);
			distance = (TextView) row.findViewById(R.id.stationFinder_listview_item_distance);
			icon = (ImageView) row.findViewById(R.id.stationFinder_listview_item_icon);
			favButton = (ToggleImageButton) row.findViewById(R.id.stationFinder_listview_Toggle_btn);
		}

		void populateFrom(LocationDistance locationDistance)
		{
			currentData = locationDistance;
			Location l = locationDistance.location;
			
			// set the fav button details
			favButton.setStateNoEvent(dataInterface.isLocationFavourite(l));
			favButton.setTag(l);
			
			destination.setText(l.getRealName());
			distance.setText(locationDistance.getFormatedDistance());
			icon.setImageResource(UIUtils.getImageForStationType(l.getLocationType()));
		}

	}
}