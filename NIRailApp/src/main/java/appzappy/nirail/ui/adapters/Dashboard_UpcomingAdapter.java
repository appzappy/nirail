package appzappy.nirail.ui.adapters;


import appzappy.nirail.R;
import appzappy.nirail.pathfinding.Journey;
import appzappy.nirail.ui.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Dashboard_UpcomingAdapter extends ArrayAdapter<Journey>
{
	public Activity activity = null;
	public Dashboard_UpcomingAdapter(Context c, Activity activity)
	{
		super(c, R.layout.upcoming_item);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		UpcomingHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.upcoming_item, parent, false);
			holder = new UpcomingHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (UpcomingHolder) row.getTag();
		}

		holder.populateFrom(getItem(position), activity);

		return row;
	}

	static class UpcomingHolder
	{
		private TextView destination = null;
		private TextView source = null;
		private TextView time = null;

		UpcomingHolder(View row)
		{
			time = (TextView) row.findViewById(R.id.upcoming_time);
			source = (TextView) row.findViewById(R.id.upcoming_station);
			destination = (TextView) row.findViewById(R.id.upcoming_dest);
		}

		void populateFrom(Journey item, Activity activity)
		{
			time.setText(item.getStartingTimeFormated());
			//dest.setText(item.getFinalPortion().getEnd().getRealName());
			//station.setText(item.getLocationName());
			
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			double maxWidth = (double)metrics.widthPixels*0.35 - 1;
			
		
			TextPaint painter = source.getPaint();
			
			String left = item.getStartingLocationName();
			left = UIUtils.trimStringToLength(painter, left, "..." , maxWidth);
			
			String right = item.getEndingLocationName();
			right = UIUtils.trimStringToLength(painter, right, "...", maxWidth);
			
			source.setText(left);
			destination.setText(right);
		}
		
		

	}
}