package AppZappy.NIRailAndBus.ui.activities;

import java.util.List;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.LocationDistance;
import AppZappy.NIRailAndBus.events.IEventListener;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.StationList_Adapter;
import AppZappy.NIRailAndBus.ui.adapters.StationList_Adapter.StationFinder_ListViewItemHolder;
import AppZappy.NIRailAndBus.ui.widgets.ToggleImageButton;
import AppZappy.NIRailAndBus.userdata.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class StationList extends Activity
{
	private IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	private IEventListener locationUpdatedListener;
	private StationList_Adapter stationListViewAdapter;
	private ListView stationListView;
	private TextView stationSortText;
	private EditText searchEditText;
	private Button searchButton, searchCancelButton;
	
	private List<Location> locationsToDisplay = null;
	private boolean _searchState = false;
	
	private enum SortingApproach
	{
		distance, a2z
	};

	private SortingApproach _sortList = SortingApproach.distance;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_station_finder_listview);
		
		stationSortText = (TextView) findViewById(R.id.stationFinderUI_sortButton);

		
		// initialise the list-view object
		stationListViewAdapter = new StationList_Adapter(this);
		stationListView = (ListView) findViewById(R.id.stationFinderUI_listview);
		stationListView.setAdapter(stationListViewAdapter);

		searchEditText = (EditText) findViewById(R.id.stationFinderUI_searchTextView);
		searchButton = (Button) findViewById(R.id.stationFinderUI_searchButton);
		searchCancelButton = (Button) findViewById(R.id.stationFinderUI_searchCancelButton);
		
		// register for location updates
		locationUpdatedListener = new IEventListener()
		{
			public void action(Object optionalData)
			{
				fillListView();
			}
		};

		
		stationListView.setClickable(true);

		// add the listener for the search box
		searchEditText.addTextChangedListener(new TextWatcher()
		{
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
				// update the listview item set
				fillListView();
			}
			
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{}
			
			public void afterTextChanged(Editable arg0)
			{}
		});
	}

	
	@Override
	protected void onResume()
	{
		super.onResume();

		LoadData.initialise(this);
		
		locationsToDisplay = dataInterface.getAllLocations();
		fillListView();
		
		dataInterface.registerLocationUpdates(locationUpdatedListener);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		dataInterface.removeLocationUpdates(locationUpdatedListener);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK) // && event.getRepeatCount() == 0
	    {
	    	if (_searchState)
	    	{
	    		setSearch(false);
	    		return true;
	    	}
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	
	
	public String getSearchText()
	{
		return searchEditText.getText().toString();
	}
	
	public void setSort(SortingApproach sorting)
	{
		_sortList = sorting;
		switch(_sortList)
		{
			case a2z:
				stationSortText.setText("Sorted by A - Z");
				break;
			case distance:
				stationSortText.setText("Sorted by Distance");
				break;
		}
		fillListView();
	}

	public void nextSort()
	{
		final SortingApproach sort = _sortList;
		final int position = sort.ordinal();
		
		final SortingApproach[] allSortings = SortingApproach.values();
		
		final int newPosition = (position + 1) % allSortings.length;
		
		final SortingApproach newSort = allSortings[newPosition];
		setSort(newSort);
	}
	
	
	
	public void listViewHasBeenClicked(View view)
	{
		StationFinder_ListViewItemHolder l = (StationFinder_ListViewItemHolder) view.getTag();
		StationWindow.openNew(this, l.currentData.location);
	}

	public List<LocationDistance> getSortedDisplayedData()
	{
		String searchText = getSearchText();
		
		List<Location> locations = dataInterface.filterLocations(locationsToDisplay, searchText);
		
		switch(_sortList)
		{
			case a2z:
				return dataInterface.getLocationDistances_SortByName(locations);
			default:
				return dataInterface.getLocationDistances(locations);
		}
	}
	
	public void fillListView()
	{
		List<LocationDistance> locationDistances = getSortedDisplayedData();
		
		stationListViewAdapter.clear();
		for (LocationDistance l : locationDistances)
		{
			stationListViewAdapter.add(l);
		}

		stationListView.setSelection(0);
	}
	
	
	public void toggleSearchState()
	{
		setSearch(!_searchState);
	}
	
	public void setSearch(boolean visible)
	{
		_searchState = visible;
		searchEditText.setText("");
		if (visible)
		{
			searchButton.setVisibility(View.GONE);
			searchCancelButton.setVisibility(View.VISIBLE);
			searchEditText.setVisibility(View.VISIBLE);
			searchEditText.requestFocus();
			searchEditText.requestFocusFromTouch();
			UIUtils.showKeyboard(this, stationSortText);
		}
		else
		{
			searchButton.setVisibility(View.VISIBLE);
			searchCancelButton.setVisibility(View.GONE);
			searchEditText.setVisibility(View.GONE);
			UIUtils.hideKeyboard(this, searchEditText);
		}
	}

	public void setAsFavourite(View v)
	{
		// Get parent and button
		Location location = (Location) v.getTag();

		ToggleImageButton button = (ToggleImageButton) v;

		// perform event handling
		boolean fav = button.toggleState();
		dataInterface.setLocationFavourite(location, fav);
	}
	
	@Override
	public boolean onSearchRequested()
	{
		toggleSearchState();
		return false;
	}

	public void onHomeClick(View view)
	{
		UIUtils.goHome(this);
	}

	public void onSortClick(View view)
	{
		nextSort();
	}

	public void onStationClick(View view)
	{
		Location l = (Location) view.getTag(100);
		StationWindow.openNew(this, l);
	}

	public void onSearchClick(View v)
	{
		// search button clicked
		setSearch(true);
	}
	
	public void onHideSearchClick(View v)
	{
		setSearch(false);
	}
	

	public void onOpenMapViewClick(View view)
	{
		Context context = view.getContext();
		final Intent intent = new Intent(context, StationMap.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(intent);
	}

	public static void openNew(Context context)
	{
		context.startActivity(new Intent(context, StationList.class));
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return UIUtils.setUpContextMenu(menu, getMenuInflater());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean res = UIUtils.handleContextMenuClick(this, item);
		if (!res)
			return super.onOptionsItemSelected(item);
		else
			return true;
	}
	
	
}
