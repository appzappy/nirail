package AppZappy.NIRailAndBus.ui.activities;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.collections.DataPointer;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.pathfinding.JourneyPortion;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.JourneyWindow_PortionAdapter;
import AppZappy.NIRailAndBus.userdata.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class JourneyWindow extends Activity
{
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();

	
	public static final String BUNDLE_NAME = "journey_data";
	public static final String BUNDLE_PORTIONS = "portion_count";
	public static final String BUNDLE_START_PRE = "start_";
	public static final String BUNDLE_END_PRE = "end_";
	public static final String BUNDLE_TIME = "_time";
	public static final String BUNDLE_LOC = "_loc";
	public static final String BUNDLE_ROUTE = "route_id_";
	
	public static void openNew(Context context, Journey journey)
	{
		if(journey.size() == 1)
		{
			RouteWindow.openNew(context, journey.getPortion(0));
			return;
		}
		
		Intent res = new Intent(context, JourneyWindow.class);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_PORTIONS, journey.size());
		for (int i=0;i<journey.size();i++)
		{
			JourneyPortion portion = journey.getPortion(i);
			String start_l = BUNDLE_START_PRE + i + BUNDLE_LOC;
			String start_t = BUNDLE_START_PRE + i + BUNDLE_TIME;
			String end_l = BUNDLE_END_PRE + i + BUNDLE_LOC;
			String end_t = BUNDLE_END_PRE + i + BUNDLE_TIME;
			String route_field = BUNDLE_ROUTE + i;
			
			b.putInt(start_l, portion.getStart().get_id());
			b.putShort(start_t, portion.getStartTime());
			b.putInt(end_l, portion.getEnd().get_id());
			b.putShort(end_t, portion.getEndTime());
			b.putInt(route_field, portion.getRoute().get_id());
		}
		
		res.putExtra(BUNDLE_NAME, b);
		context.startActivity(res);
	}

	
	
	
	private Journey journey = null;
	private JourneyWindow_PortionAdapter portion_adapter = null;
	private ListView portionList = null;
	
	private TextView arrivalText;
	private TextView departText;
	private TextView changesText;
	private TextView timeText;
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_journey);
		
				
		Bundle b = getIntent().getBundleExtra(BUNDLE_NAME);
		if (b != null)
		{
			Journey journey = new Journey();
			
			final int portions = b.getInt(BUNDLE_PORTIONS);
			
			
			
			for (int i=0;i<portions;i++)
			{
				String start_l = BUNDLE_START_PRE + i + BUNDLE_LOC;
				String start_t = BUNDLE_START_PRE + i + BUNDLE_TIME;
				String end_l = BUNDLE_END_PRE + i + BUNDLE_LOC;
				String end_t = BUNDLE_END_PRE + i + BUNDLE_TIME;
				String route_field = BUNDLE_ROUTE + i;
				
				
				Location start = dataInterface.getLocation(b.getInt(start_l));
				short startTime = b.getShort(start_t);
				Location end = dataInterface.getLocation(b.getInt(end_l));
				short endTime = b.getShort(end_t);
				DataPointer<Route> route_pointer = new DataPointer<Route>(Route.class, b.getInt(route_field));
				Route route = route_pointer.get_Object_Cache();
				
				JourneyPortion portion = JourneyPortion.create(start, startTime, end, endTime, route);
				journey.addPortion(portion);
			}
			
			this.journey = journey;
						
			portionList = (ListView) findViewById(R.id.journeyUI_portion_listView);
			
			registerForContextMenu(portionList);
			
			// add the portions to the display section
			portion_adapter = new JourneyWindow_PortionAdapter(this);
			for (int i=0;i<journey.size();i++)
			{
				JourneyPortion portion = journey.getPortion(i);
				portion_adapter.add(portion);
			}
			
			// link the adapter and the list view together
			portionList.setAdapter(portion_adapter);
			portionList.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					JourneyPortion portion = JourneyWindow.this.journey.getPortion(position);
					RouteWindow.openNew(JourneyWindow.this, portion);
				}
			});
			
			TextView title = (TextView)findViewById(R.id.TitleButton);
			String initialText = journey.getFirstPortion().getStart().getRealName();
			String joiner = " to ";
			String endingText = journey.getFinalPortion().getEnd().getRealName();
			String shortenedString = "...";
			TextPaint painter = title.getPaint();
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			double maxWidth = (double)metrics.widthPixels*0.7 - 1;
			
			String shortenedText = UIUtils.trimStringToLength(painter, initialText, joiner, endingText, shortenedString, maxWidth);
			title.setText(shortenedText);
			
			timeText = (TextView) findViewById(R.id.journey_TotalTimeText);
			arrivalText = (TextView) findViewById(R.id.journey_arrivalTimeText);
			departText = (TextView) findViewById(R.id.journey_departureTimeText);
			changesText = (TextView) findViewById(R.id.journey_changesText);
			
			changesText.setText(""+journey.getNumberChanges());
			arrivalText.setText(""+journey.getEndingTimeFormated());
			departText.setText(""+journey.getStartingTimeFormated());
			timeText.setText(""+journey.getLengthFormatted());
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// ensure the system is initialised
		LoadData.initialise(this);
	}
	
	
	
	
	/** Handle "Home" action **/
	public void onHomeClick(View v)
	{
		UIUtils.goHome(this);
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
			JourneyWindow_PortionAdapter adapter = (JourneyWindow_PortionAdapter) list.getAdapter();
			JourneyPortion contextMenuJourneyPortion = adapter.getItem(info.position);
			
			contextMenuJourney = new Journey();
			contextMenuJourney.addPortion(contextMenuJourneyPortion);
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
