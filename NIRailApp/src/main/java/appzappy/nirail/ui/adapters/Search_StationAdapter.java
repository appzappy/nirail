package appzappy.nirail.ui.adapters;


import appzappy.nirail.R;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.ui.UIUtils;
import appzappy.nirail.userdata.Favourite.FavouriteType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Search_StationAdapter extends ArrayAdapter<Location>
{
	public Search_StationAdapter(Context c)
	{
		super(c, R.layout.ui_search_station_item);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		StationHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.ui_search_station_item, parent, false);
			holder = new StationHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (StationHolder) row.getTag();
		}
		
		holder.populateFrom(getItem(position), getContext());

		return row;
	}
	
	
	
	
	static class StationHolder
	{
		private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();
		
		private TextView text = null;
		private ImageView icon = null;

		StationHolder(View row)
		{
			text = (TextView) row.findViewById(R.id.stationText);
			icon = (ImageView) row.findViewById(R.id.stationIcon);
		}

		void populateFrom(Location item, Context c)
		{
			text.setText(item.getRealName());
			
			if (dataInterface.isLocationFavourite(item))
			{
				icon.setImageResource(UIUtils.getImageForFavouriteType(FavouriteType.station));
			}
			else
			{
				icon.setImageResource(UIUtils.getImageForStationType(item.getLocationType()));
			}
		}

	}


	
}