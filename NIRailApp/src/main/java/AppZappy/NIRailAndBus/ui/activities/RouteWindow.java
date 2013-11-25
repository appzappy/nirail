package AppZappy.NIRailAndBus.ui.activities;

import java.util.ArrayList;
import java.util.List;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.collections.DataPointer;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.data.model.Stop;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.pathfinding.JourneyPortion;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.Route_StopAdapter;
import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.util.timing.TimeFormatter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RouteWindow extends Activity
{
	public static final String BUNDLE_NAME = "journey_data";
	public static final String BUNDLE_ROUTE_ID = "route_id";
	public static final String BUNDLE_START_POSITION = "start_position";
	public static final String BUNDLE_END_POSITION = "end_position";
	
	public static void openNew(Context context, JourneyPortion portion)
	{
		Intent res = new Intent(context, RouteWindow.class);
		Bundle b = new Bundle();
		
		Route route = portion.getRoute();
		Route.from_SQL(route.get_id(), Route.LOAD_STOPS__ALL);
		
		int start = portion.getStartPositionInRoute();
		int end = portion.getEndPositionInRoute();
		
		b.putInt(BUNDLE_ROUTE_ID, route.get_id());
		b.putInt(BUNDLE_START_POSITION, start);
		b.putInt(BUNDLE_END_POSITION, end);
		b.putBoolean("IsReminder", false);
		
		res.putExtra(BUNDLE_NAME, b);

		context.startActivity(res);
	}
	
	public static void openNewNoBack(Context context, JourneyPortion portion)
	{		
		Intent res = new Intent(context, RouteWindow.class);
		Bundle b = new Bundle();
		res.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		Route route = portion.getRoute();
		Route.from_SQL(route.get_id(), Route.LOAD_STOPS__ALL);
		
		int start = portion.getStartPositionInRoute();
		int end = portion.getEndPositionInRoute();
		
		b.putInt(BUNDLE_ROUTE_ID, route.get_id());
		b.putInt(BUNDLE_START_POSITION, start);
		b.putInt(BUNDLE_END_POSITION, end);
		b.putBoolean("IsReminder", false);
		
		res.putExtra(BUNDLE_NAME, b);

		context.startActivity(res);
	}
	
	private JourneyPortion _journeyPortion = null;
	
	
	// TODO remove these?
	private boolean _isReminder;
	private Button reminderButton;
	
	private ListView stopList;
	private Route_StopAdapter stopAdapter;
	
	private LinearLayout start_layout = null;
	private LinearLayout end_layout = null;
	private TextView start_text = null;
	private TextView end_text = null;
	
	
	IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_route_view);

		reminderButton = (Button) findViewById(R.id.route_remind_button);
		start_layout = (LinearLayout) findViewById(R.id.route_view_starting_date);
		end_layout = (LinearLayout) findViewById(R.id.route_view_ending_date);
		start_text = (TextView) findViewById(R.id.route_view_start_date_text);
		end_text = (TextView) findViewById(R.id.route_view_end_date_text);
		
		// set up adapter and listview
		stopList = (ListView) findViewById(R.id.routeUI_listView);
		stopAdapter = new Route_StopAdapter(this);
		stopList.setAdapter(stopAdapter);
		
		stopList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Location l = stopAdapter.getItem(position).getLocation();
				StationWindow.openNew(RouteWindow.this, l);
			}
		});
		
		Bundle b = getIntent().getBundleExtra(BUNDLE_NAME);
		if (b != null)
		{
			int route_id = b.getInt(BUNDLE_ROUTE_ID);
			DataPointer<Route> pointer = new DataPointer<Route>(Route.class, route_id);
			
			Route route = pointer.get_Object_Cache();
			
			int start_position = b.getInt(BUNDLE_START_POSITION);
			int end_position = b.getInt(BUNDLE_END_POSITION);
			
			
			Stop first = route.getStop(start_position);
			Stop second = route.getStop(end_position);
			
			Location start = first.getLocation();
			short startTime = first.getTime();
			Location end = second.getLocation();
			short endTime = second.getTime();
			
			_journeyPortion = JourneyPortion.create(start, startTime, end, endTime, route);
			
			
			
			// set up highlighted colours
			List<Stop> highlighedStops = new ArrayList<Stop>();
			highlighedStops.add(first);
			highlighedStops.add(second);
			stopAdapter.setHighlightedStops(highlighedStops, getResources().getColor(R.color.highlighted_item));
			
			// trim the data at the stop of the route view to fit in the space
			TextView title = (TextView)findViewById(R.id.TitleButton);
			String initialText = first.getLocation().getRealName();
			String joiner = " to ";
			String endingText = second.getLocation().getRealName();
			String shortenedString = "...";
			TextPaint painter = title.getPaint();
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			double maxWidth = (double)metrics.widthPixels*0.7 - 1;
			
			String shortenedText = UIUtils.trimStringToLength(painter, initialText, joiner, endingText, shortenedString, maxWidth);
			title.setText(shortenedText);
			
			// add all stops to the list view
			int countStops = route.countStops();
			for (int i=0;i<countStops;i++)
			{
				Stop s = route.getStop(i);
				stopAdapter.add(s);
			}
			
			// display the starting and ending data for the current route

			// starting date
			int starting_date = dataInterface.getStartingDate(route);
			if (starting_date > 0)
			{
				// there is a starting date
				start_layout.setVisibility(View.VISIBLE);
				start_text.setText(TimeFormatter.getTodayDate_Formated(starting_date));
			}
			// ending date
			int ending_date = dataInterface.getEndingDate(route); 
			if (ending_date > 0)
			{
				// there is a starting date
				end_layout.setVisibility(View.VISIBLE);
				end_text.setText(TimeFormatter.getTodayDate_Formated(ending_date));
			}
			
			// handle the vertical scroll for the list views
			
			stopList.setSelection(start_position);

			_isReminder = b.getBoolean("IsReminder");
			
			if (_isReminder)
			{
				reminderButton.setText("Cancel Reminder");
			}
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();

		LoadData.initialise(this);
		
	}
	
	public void onHomeClick(View view)
	{
		UIUtils.goHome(this);
	}

	public void onCopyClick(View view)
	{
		Journey fake = new Journey();
		fake.addPortion(_journeyPortion);
		dataInterface.copyTrainToClipboard(fake);
	}
	
	public void onRemindClick(View view)
	{		
		if(_isReminder)
		{
			dataInterface.getTrainReminderService().cancelReminders(dataInterface.getAndroidContext());
			_isReminder = false;
			reminderButton.setText(R.string.title_remindMe);
			dataInterface.setTrainReminderService(null);
			return;
		}
		
		Journey fake = new Journey();
		fake.addPortion(_journeyPortion);
		dataInterface.setTrainReminder(this, fake);

		reminderButton = (Button) findViewById(R.id.route_remind_button);
		reminderButton.setText("Cancel Reminder");
		_isReminder = true;
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
