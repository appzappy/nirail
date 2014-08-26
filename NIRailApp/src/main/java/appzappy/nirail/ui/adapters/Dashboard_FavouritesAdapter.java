package appzappy.nirail.ui.adapters;

import appzappy.nirail.R;
import appzappy.nirail.ui.UIUtils;
import appzappy.nirail.userdata.Favourite;
import appzappy.nirail.userdata.Favourite.FavouriteType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Dashboard_FavouritesAdapter extends ArrayAdapter<Favourite>
{
	public Dashboard_FavouritesAdapter(Context c)
	{
		super(c, R.layout.mainwindow_favourite_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		MainWindow_FavouriteHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.mainwindow_favourite_item, parent, false);
			holder = new MainWindow_FavouriteHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (MainWindow_FavouriteHolder) row.getTag();
		}

		holder.populateFrom(getItem(position));

		return row;
	}

	static class MainWindow_FavouriteHolder
	{
		private TextView destination = null;
		private ImageView icon = null;
		View self;

		MainWindow_FavouriteHolder(View row)
		{
			self = row;
			destination = (TextView) row.findViewById(R.id.mainwindow_favs_destination);
			icon = (ImageView) row.findViewById(R.id.mainwindow_favs_icon);
		}

		void populateFrom(Favourite item)
		{
			if (item.isLinkedRoute())
			{
				destination.setText(item.getSource() + " to " + item.getDestination());
				icon.setImageResource(UIUtils.getImageForFavouriteType(FavouriteType.journey));
			}
			else
			{
				destination.setText(item.getDestination());
				icon.setImageResource(UIUtils.getImageForFavouriteType(FavouriteType.station));
				self.setTag(item.getDestination());
			}
		}
	}
}