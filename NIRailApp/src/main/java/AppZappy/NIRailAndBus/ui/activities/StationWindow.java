package AppZappy.NIRailAndBus.ui.activities;

import java.util.List;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.pathfinding.RouteStopPosition;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.StationWindow_Adapter;
import AppZappy.NIRailAndBus.ui.widgets.ToggleImageButton;
import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.util.C;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StationWindow extends Activity
{
	public static final String BUNDLE_NAME = "Location";
	public static final String LOCATION_ID = "LocationKey";
	public static final String EARLIEST_DEPARTURE = "EarliestDeparture"; 
	
	public static void openNew(Context context, Location location)
	{
		Intent res = new Intent(context, StationWindow.class);

		Bundle b = new Bundle();

		int location_id = location.get_id();
		b.putInt(LOCATION_ID, location_id);
		res.putExtra(BUNDLE_NAME, b);
		context.startActivity(res);
	}


	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();

	Location _station = null;
	ListView arrivals;
	StationWindow_Adapter arrivalAdapter;
	ToggleImageButton favouriteToggleButton = null;
	ImageView stationType = null;
	TextView stationName, stationDist = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_stationview);
		
		arrivals = (ListView) findViewById(R.id.stationView_arrivalsListview);
		arrivalAdapter = new StationWindow_Adapter(this);
		arrivals.setAdapter(arrivalAdapter);
		
		stationType = (ImageView) findViewById(R.id.stationView_stationType);
		stationName = (TextView) findViewById(R.id.stationView_stationName);
		stationDist = (TextView) findViewById(R.id.stationView_stationDist);
		favouriteToggleButton = (ToggleImageButton) findViewById(R.id.stationView_Toggle_btn);
		
		Bundle b = getIntent().getBundleExtra(BUNDLE_NAME);
		if (b != null)
		{
			int location_id = b.getInt(LOCATION_ID);
			_station = dataInterface.getLocation(location_id);
			
			
			// set the final station to the adapter
			arrivalAdapter.setStation(_station);
			
			// set favourite icon
			boolean newState = dataInterface.isLocationFavourite(_station);
			favouriteToggleButton.setState(newState);
			
			// set station text
			stationName.setText(_station.getRealName());
			
			// set the distance text
			stationDist.setText("Distance: " + dataInterface.getFormattedDistanceToLocation(_station));
			// set the image resource
			stationType.setImageResource(UIUtils.getImageForStationType(_station.getLocationType()));
			
			
			// adds the list of routess
			List<RouteStopPosition> routes = dataInterface.getLocationRoutes(_station, 15);
			arrivalAdapter.clear();
			for (RouteStopPosition r : routes)
			{
				arrivalAdapter.add(r);
			}
			arrivals.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
				{
					RouteStopPosition rsp = arrivalAdapter.getItem(position);
					Location start = rsp.getLocation();
					Location end = rsp.getFinalStop_Location();
					if (start.get_id() != end.get_id())
					{
						JourneySet.openNew(StationWindow.this, start, end, rsp.route);
					}
					
				}
			});
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		LoadData.initialise(this);
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
	
	
	public void setAsFavourite(View view)
	{
		Location l = _station;
		
		dataInterface.toggleLocationFavourite(l);
		boolean newState = dataInterface.isLocationFavourite(l);
		favouriteToggleButton.setState(newState);
	}
	
	public void onNavigateClick (View v)
	{
		try
		{
			// THIS IS UNSUPPORTED FUNCTIONALITY. THIS MAY BREAK AT ANY POINT!
			Uri uri = Uri.parse("google.navigation:q=" + _station.getLatitude() + "," + _station.getLongitude() ); 
			Intent navigation = new Intent(Intent.ACTION_VIEW, uri);
			
			startActivity(navigation);
		}
		catch (Exception e)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Can not start Google Navigation."+C.new_line()+"Application is not installed or version is incompatible.")
		    .setCancelable(false)
		    .setPositiveButton("OK", new DialogInterface.OnClickListener()
		    {
		        public void onClick(DialogInterface dialog, int id)
		        {
		             
		        }
		    });
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	public void onMapClick (View v)
	{
		try
		{
			Uri uri = Uri.parse("geo:" + _station.getLatitude() + "," + _station.getLongitude() + "?z=20&q="  + _station.getLatitude() + "," + _station.getLongitude()); 
			Intent navigation = new Intent(Intent.ACTION_VIEW, uri); 
			startActivity(navigation); 
		}
		catch (Exception e)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Can not start Google Maps."+C.new_line()+"Application is not installed or version is incompatible.")
		    .setCancelable(false)
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		             
		        }
		    });
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	public void onHomeClick(View view)
	{
		UIUtils.goHome(this);
	}
}
