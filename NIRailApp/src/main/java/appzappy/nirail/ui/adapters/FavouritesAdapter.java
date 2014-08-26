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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FavouritesAdapter extends ArrayAdapter<Favourite>
{
	public FavouritesAdapter(Context c)
	{
		super(c, R.layout.ui_favourites_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		Favourites_ListViewItemHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			row = inflater.inflate(R.layout.ui_favourites_item, parent, false);
			holder = new Favourites_ListViewItemHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder = (Favourites_ListViewItemHolder) row.getTag();
		}

		holder.populateFrom(getItem(position));

		return row;
	}

	public static class Favourites_ListViewItemHolder
	{
		private TextView textView = null;
		private Button button = null;
		public Favourite currentData = null;
		private ImageView icon = null;
		
		Favourites_ListViewItemHolder(View row)
		{
			textView = (TextView) row.findViewById(R.id.favouritesUI_textMessage);
			button = (Button) row.findViewById(R.id.favouritesUI_deleteButton);
			icon = (ImageView) row.findViewById(R.id.favouritesUI_icon);
		}

		void populateFrom(Favourite fav)
		{
			currentData = fav;
			
			button.setTag(fav);
			
			String text = fav.getDestination();
			if (fav.isLinkedRoute())
			{
				text = fav.getSource() + " to " + text;
				icon.setImageResource(UIUtils.getImageForFavouriteType(FavouriteType.journey));
			}
			else
				icon.setImageResource(UIUtils.getImageForFavouriteType(FavouriteType.station));
			
			textView.setText(text);
			
		}
		
	}
}
