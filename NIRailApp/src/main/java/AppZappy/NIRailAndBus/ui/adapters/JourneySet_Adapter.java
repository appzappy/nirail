package AppZappy.NIRailAndBus.ui.adapters;

import java.util.List;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.userdata.Settings;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class JourneySet_Adapter extends ArrayAdapter<Journey>
{
	/**
	 * Used to retrieve raw data from the system
	 */
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	
	private int NORMAL = -1;
	private int PAST = -1;
	
	private boolean filter = false;
	
	public int lastItem = -1;
	
	private int highlighted_color = 0;
	private Route highlighted_route = null;
	public void setHighlightedRoute(Route route, int color)
	{
		this.highlighted_route = route;
		this.highlighted_color = color;
	}
	
	public JourneySet_Adapter(Context c, List<Journey> items, boolean filter)
	{
		super(c, R.layout.ui_journey_set_list_item, items);

		this.filter = filter;
		
		
		Resources resource = dataInterface.getAndroidContext().getResources();
		if (Settings.isLightTheme())
		{
			PAST = resource.getColor(R.color.list_item_inactive_light);
			NORMAL = resource.getColor(R.color.list_item_active_light);
		}
		else
		{
			PAST = resource.getColor(R.color.list_item_inactive);
			NORMAL = resource.getColor(R.color.list_item_active);
		}
		
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		JourneyHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_journey_set_list_item, parent, false);
			holder = new JourneyHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (JourneyHolder) row.getTag();
		}
		
		int foreColor = NORMAL;
		int backColor = 0;
		Journey item = super.getItem(position);
		
		if (filter)
		{
			// TODO Check for inefficencies with dataInterface.getCurrentTime();
			if (item.getStartingTime() < dataInterface.getCurrentTime())
				foreColor = PAST;
		}
		
		if (highlighted_route != null)
		{
			// highlighted route is active
			if (item.getPortion(0).getRoute().get_id() == highlighted_route.get_id())
			{
				backColor=highlighted_color;
			}
		}
		
		holder.populateFrom(item, foreColor, backColor);

		return row;
	}

	public int getFirstNew(int time)
	{
		int count = this.getCount();
		for(int i=0;i<count;i++)
		{
			Journey j = this.getItem(i);
			if (j.getStartingTime()> time)
			{
				return i;
			}
		}
		return -1;
	}
	
	static class JourneyHolder
	{
		private TextView arriveTime, departTime, numberChanges, lengthTime = null;
		
		private View row = null;
		JourneyHolder(View row)
		{
			arriveTime = (TextView) row.findViewById(R.id.arriveTime);
			departTime = (TextView) row.findViewById(R.id.departTime);
			lengthTime = (TextView) row.findViewById(R.id.lengthTime);
			numberChanges = (TextView) row.findViewById(R.id.numberChanges);
			this.row = row;
		}

		void populateFrom(Journey item, int foreColor, int backColor)
		{
			row.setBackgroundColor(backColor);
			
			departTime.setText(item.getStartingTimeFormated());
			arriveTime.setText(item.getEndingTimeFormated());
			lengthTime.setText(item.getLengthFormatted());
			numberChanges.setText(item.getNumberChanges() + "");
			
			departTime.setTextColor(foreColor);
			arriveTime.setTextColor(foreColor);
			lengthTime.setTextColor(foreColor);
			numberChanges.setTextColor(foreColor);
		}
		

	}
}