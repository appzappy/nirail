package appzappy.nirail.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import appzappy.nirail.R;
import appzappy.nirail.pathfinding.JourneyPortion;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class JourneyWindow_PortionAdapter extends ArrayAdapter<JourneyPortion>
{
	public JourneyWindow_PortionAdapter(Context c, List<JourneyPortion> items)
	{
		super(c, R.layout.ui_journey_portion_item, items);
	}
	public JourneyWindow_PortionAdapter(Context c)
	{
		super(c, R.layout.ui_journey_portion_item, new ArrayList<JourneyPortion>());
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		JourneyPortionHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_journey_portion_item, parent, false);
			holder = new JourneyPortionHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (JourneyPortionHolder) row.getTag();
		}
		
		JourneyPortion item = super.getItem(position);
		
		holder.populateFrom(item);

		return row;
	}

	static class JourneyPortionHolder
	{
		private TextView location_source, location_target, lengthTime, source_time, target_time = null;
		
		JourneyPortionHolder(View row)
		{
			location_source = (TextView) row.findViewById(R.id.location_source);
			location_target = (TextView) row.findViewById(R.id.location_target);
			source_time = (TextView) row.findViewById(R.id.source_time);
			target_time = (TextView) row.findViewById(R.id.target_time);
			lengthTime = (TextView) row.findViewById(R.id.lengthTime);
		}

		void populateFrom(JourneyPortion portion)
		{
			location_source.setText(portion.getStart().getRealName());
			location_target.setText(portion.getEnd().getRealName());
			
			source_time.setText(portion.getStartTimeFormatted());
			target_time.setText(portion.getEndTimeFormatted());
			
			lengthTime.setText(portion.getLengthFormatted());
		}
		

	}
}
