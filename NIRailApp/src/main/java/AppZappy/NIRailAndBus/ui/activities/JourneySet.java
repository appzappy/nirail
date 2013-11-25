package AppZappy.NIRailAndBus.ui.activities;

import java.util.List;

import com.viewpagerindicator.TitlePageIndicator;

import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.collections.DataPointer;
import AppZappy.NIRailAndBus.data.enums.DayOfWeek;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.JourneySet_Adapter;
import AppZappy.NIRailAndBus.ui.adapters.JourneySet_PagerAdapter;
import AppZappy.NIRailAndBus.ui.widgets.JourneySet_PagerUIElement;
import AppZappy.NIRailAndBus.ui.widgets.ToggleImageButton;
import AppZappy.NIRailAndBus.userdata.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class JourneySet extends Activity
{
	/**
	 * Used to retrieve raw data from the system
	 */
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();

	private Location _destination = null;
	private Location _source = null;
	private Button destinationTextLabel, sourceTextLabel;

	private ToggleImageButton favourite;

	private Route _highlighedRoute = null;

	public static final int WEEKDAY = 0;
	public static final int SATURDAY = 1;
	public static final int SUNDAY = 2;

	private static final String BUNDLE_SOURCE_ID = "source_id";
	private static final String BUNDLE_DESTINATION_ID = "destination_id";
	private static final String BUNDLE_NAME = "stations";
	private static final String BUNDLE_ROUTE_ID = "routeToHighlight_id";

	private ViewPager pager = null;
	private JourneySet_PagerAdapter pager_adapter = null;
	private TitlePageIndicator pager_indicator = null;

	public static void openNew(Context context, Location source, Location destination)
	{
		openNew(context, source.getRealName(), destination.getRealName());
	}

	public static void openNew(Context context, String source, String destination)
	{
		Location s = dataInterface.getLocation(source);
		Location d = dataInterface.getLocation(destination);
		openNew(context, s, d, null);
	}

	public static void openNew(Context context, Location source, Location destination, Route route)
	{
		Intent res = new Intent(context, JourneySet.class);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_SOURCE_ID, source.get_id());
		b.putInt(BUNDLE_DESTINATION_ID, destination.get_id());

		if (route != null)
		{
			int route_id = route.get_id();
			b.putInt(BUNDLE_ROUTE_ID, route_id);
		}
		res.putExtra(BUNDLE_NAME, b);
		context.startActivity(res);
	}


	private static void openNewNoBack(Context context, Location source, Location destination)
	{
		Intent res = new Intent(context, JourneySet.class);
		res.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_SOURCE_ID, source.get_id());
		b.putInt(BUNDLE_DESTINATION_ID, destination.get_id());
		res.putExtra(BUNDLE_NAME, b);
		context.startActivity(res);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_journey_set);

		
		favourite = (ToggleImageButton) findViewById(R.id.journey_favourite_toggle_btn);

		destinationTextLabel = (Button) this.findViewById(R.id.journeyUI_destText);
		sourceTextLabel = (Button) this.findViewById(R.id.journeyUI_departText);

		Bundle b = getIntent().getBundleExtra(BUNDLE_NAME);
		if (b != null)
		{
			_source = dataInterface.getLocation(b.getInt(BUNDLE_SOURCE_ID));
			_destination = dataInterface.getLocation(b.getInt(BUNDLE_DESTINATION_ID));

			int route_id = b.getInt(BUNDLE_ROUTE_ID);
			if (route_id > 0)
			{
				// route is set
				DataPointer<Route> pointer = new DataPointer<Route>(Route.class, route_id);
				_highlighedRoute = pointer.get_Object_Cache();
			}
			else
			{
				_highlighedRoute = null;
			}

			// set up the column headers
			destinationTextLabel.setText(_destination.getRealName());
			sourceTextLabel.setText(_source.getRealName());

			
			List<List<Journey>> allJourneyResults = dataInterface.getJourneys(_source, _destination);
			int highlight_color = getResources().getColor(R.color.highlighted_item);
			pager_adapter = new JourneySet_PagerAdapter(allJourneyResults, this, _highlighedRoute, highlight_color);
			
			pager = (ViewPager) findViewById(R.id.pager);
			pager.setAdapter(pager_adapter);
			
			pager_indicator = (TitlePageIndicator) findViewById(R.id.pager_indicator);
			pager_indicator.setViewPager(pager);
			
			for (JourneySet_PagerUIElement element : pager_adapter.pager_collection)
			{
				registerForContextMenu(element.getListView());
			}
			

			// set the favourite status of this item
			favourite.setStateNoEvent(dataInterface.isJourneyFavourite(_source, _destination));

			
			// select the correct tab for today
			DayOfWeek today = dataInterface.getCurrentDayOfWeek();
			switch (today)
			{
				case Sunday:
					setDay(SUNDAY);
					break;
				case Saturday:
					setDay(SATURDAY);
					break;
				default:
					setDay(WEEKDAY);
					break;
			}

		}
		else
		{
			destinationTextLabel.setText("NO PARAMETERS PASSED");
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// ensure the system is initialised
		LoadData.initialise(this);
	}


	private void toggleThisFavourite()
	{
		dataInterface.toggleLocationFavourite(_source, _destination);
		favourite.setState(dataInterface.isJourneyFavourite(_source, _destination));
	}

	public void setDay(int page)
	{
		pager.setCurrentItem(page);
	}
	


	/** Handle "Home" action **/
	public void onHomeClick(View v)
	{
		UIUtils.goHome(this);
	}


	public void onDestClick(View v)
	{
		StationWindow.openNew(this, _destination);
	}

	public void onDepartClick(View v)
	{
		StationWindow.openNew(this, _source);
	}

	public void setAsFavourite(View v)
	{
		toggleThisFavourite();
	}

	public void onSwapClick(View v)
	{
		openNewNoBack(this, _destination, _source);
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

	
	private Journey contextMenuJourney = null;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if (v instanceof ListView)
		{
			AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
			
			ListView list = (ListView) v;
			JourneySet_Adapter adapter = (JourneySet_Adapter) list.getAdapter();
			contextMenuJourney = adapter.getItem(info.position);
			UIUtils.createJourneyMenu(menu, v, contextMenuJourney);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getGroupId() == 1)
		{
			dataInterface.handleJourneyClickAction(this, item, contextMenuJourney);
		}
		return super.onContextItemSelected(item);
	}
	
}