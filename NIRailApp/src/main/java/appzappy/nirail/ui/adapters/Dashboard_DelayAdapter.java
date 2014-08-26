package appzappy.nirail.ui.adapters;

import appzappy.nirail.R;
import appzappy.nirail.delays.DelayItem;
import appzappy.nirail.ui.UIUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Dashboard_DelayAdapter extends ArrayAdapter<DelayItem>
{

	public Dashboard_DelayAdapter(Context c)
	{
		super(c, R.layout.ui_delay_list_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		DelayHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) super.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_delay_list_item, parent, false);
			holder = new DelayHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (DelayHolder) row.getTag();
		}

		holder.populateFrom(super.getItem(position));

		return row;
	}

	static class DelayHolder
	{
		private TextView text = null;
		private TextView time = null;
		private ImageView icon = null;
		private ImageView link = null;
		
		DelayHolder(View row)
		{
			text = (TextView) row.findViewById(R.id.delayText);
			time = (TextView) row.findViewById(R.id.delayTime);
			icon = (ImageView) row.findViewById(R.id.delayIcon);
			link = (ImageView) row.findViewById(R.id.delays_islink_icon);
		}

		void populateFrom(DelayItem delayItem)
		{
			text.setText(delayItem.getText());
			time.setText(delayItem.getTime());
			icon.setImageResource(UIUtils.getImageForLocationType(delayItem.getType()));
			
			if (delayItem.getLink() != null)
			{
				// link is set
				link.setVisibility(View.VISIBLE);
			}
			else
			{
				// link is not set
				link.setVisibility(View.GONE);
			}
		}

	}
}