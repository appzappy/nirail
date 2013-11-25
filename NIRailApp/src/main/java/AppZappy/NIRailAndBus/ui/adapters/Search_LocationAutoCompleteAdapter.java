package AppZappy.NIRailAndBus.ui.adapters;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.ui.UIUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class Search_LocationAutoCompleteAdapter extends ArrayAdapter<String> 
{
	public AutoCompleteTextView actv = null;
	
	public Search_LocationAutoCompleteAdapter(Context c)
	{
		super(c, R.layout.ui_search_location_autocomplete_item);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		LocationHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) super.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_search_location_autocomplete_item, parent, false);
			holder = new LocationHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (LocationHolder) row.getTag();
		}
		
		holder.populateFrom(getItem(position), actv);

		return row;
	}
	
	private static IUIInterface datainterface = UIInterfaceFactory.getInterface();
	
	static class LocationHolder
	{
		private TextView text = null;
		private ImageView icon = null;

		LocationHolder(View row)
		{
			text = (TextView) row.findViewById(R.id.location_auto_complete_line);
			icon = (ImageView) row.findViewById(R.id.location_auto_complete_image);
		}

		void populateFrom(String locationName, AutoCompleteTextView actv)
		{
			String[] parts = locationName.split(";");
			
			String realname = parts[0].trim(); 
			String textToDisplay = realname;
			String searchText = actv.getText().toString().toLowerCase();
			int length = Integer.MAX_VALUE;
			// display the shortest match or the match that starts with the search text
			for (int i=0;i<parts.length;i++)
			{
				String alias = parts[i].toLowerCase();
				if (alias.startsWith(searchText))
				{
					textToDisplay = parts[i].trim();
					break;
				}
				if (alias.contains(searchText))
				{
					if (alias.length() < length)
					{
						textToDisplay = parts[i].trim();
						length = alias.length();
					}
				}
			}
			
			Location item = datainterface.getLocation(realname);
			if (item == null)
				throw new RuntimeException("Item == null. Failed to find location with name : "+ locationName);
			text.setText(textToDisplay);
			icon.setImageResource(UIUtils.getImageForLocationType(item.getLocationType()));
		}

	}
}