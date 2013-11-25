package AppZappy.NIRailAndBus.ui.adapters;

import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.LocationDistance;
import AppZappy.NIRailAndBus.ui.UIUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationMap_GalleryAdapter extends ArrayAdapter<LocationDistance>
{
	public StationMap_GalleryAdapter(Context c)
	{
		super(c, R.layout.ui_station_finder_map_gallery_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		StationFinder_GalleryItemHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_station_finder_map_gallery_item, parent, false);
			holder = new StationFinder_GalleryItemHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (StationFinder_GalleryItemHolder) row.getTag();
		}

		holder.populateFrom(getItem(position));

		return row;
	}

	static class StationFinder_GalleryItemHolder
	{
		private TextView destination = null;
		private TextView distance = null;
		private ImageView icon = null;

		StationFinder_GalleryItemHolder(View row)
		{
			destination = (TextView) row.findViewById(R.id.stationMap_gallery_text_location);
			distance = (TextView) row.findViewById(R.id.stationMap_gallery_text_distance);
			icon = (ImageView) row.findViewById(R.id.stationMap_gallery_icon);
		}

		void populateFrom(LocationDistance locationDistance)
		{
			Location l = locationDistance.location;
			destination.setText(l.getRealName());
			distance.setText(locationDistance.getFormatedDistance());
			icon.setImageResource(UIUtils.getImageForStationType(l.getLocationType()));
		}

	}
}
