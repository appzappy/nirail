package appzappy.nirail.ui.activities;


import java.util.List;


import appzappy.nirail.R;
import appzappy.nirail.data.LoadData;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.ui.UIUtils;
import appzappy.nirail.ui.adapters.FavouritesAdapter;
import appzappy.nirail.ui.adapters.FavouritesAdapter.Favourites_ListViewItemHolder;
import appzappy.nirail.userdata.Favourite;
import appzappy.nirail.userdata.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;

public class Favourites extends Activity
{
	private IUIInterface dataInterface = UIInterfaceFactory.getInterface();
		
	private FavouritesAdapter _favouritesListViewAdapter;
	private ListView _favouriteListView;
	
	private Favourite _clickedFavourite = null;
	
	public static void openNew(Context context)
	{
		context.startActivity(new Intent(context, Favourites.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_favourites);
		
		// initialise the list-view object
		_favouritesListViewAdapter = new FavouritesAdapter(this);
		_favouriteListView = (ListView) findViewById(R.id.favouritesUI_favouriteList);
		_favouriteListView.setAdapter(_favouritesListViewAdapter);
		_favouriteListView.setItemsCanFocus(true);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// ensure the system is initialised
		LoadData.initialise(this);
		displayFavs();
	}


	public void displayFavs()
	{
		// add the current favourites to the display
		List<Favourite> favs = dataInterface.getFavourites();
		_favouritesListViewAdapter.clear();
		for (Favourite f: favs)
		{
			_favouritesListViewAdapter.add(f);
		}
	}
	
	public void removeFavourite(View button)
	{
		Favourite f = (Favourite) button.getTag();
		if (f==null)
			return;
		dataInterface.removeFavourite(f);
		displayFavs();
	}
	
	public void favouriteClick(View row)
	{
		Favourites_ListViewItemHolder holder = (Favourites_ListViewItemHolder) row.getTag();
		_clickedFavourite = holder.currentData;
		
		if (_clickedFavourite.isLinkedRoute())
			UIUtils.goFavourite(this, _clickedFavourite);
		else
		{
			registerForContextMenu(row);
			openContextMenu(row);
		}
	}
	
	public void onHomeClick(View view)
	{
		UIUtils.goHome(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo)
	{  
		super.onCreateContextMenu(menu, v, menuInfo);
		Location location = dataInterface.getLocation(_clickedFavourite.getDestination());
		UIUtils.createLocationMenu(menu, v, location);
	} 
	
	@Override  
    public boolean onContextItemSelected(MenuItem item)
	{  
		Location location = dataInterface.getLocation(_clickedFavourite.getDestination());
		UIUtils.handleLocationClickAction(this, item, location);
        return super.onContextItemSelected(item);  
    } 
}
