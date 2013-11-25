package AppZappy.NIRailAndBus.ui.activities;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.LocationDistance;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.events.IEventListener;
import AppZappy.NIRailAndBus.geolocation.Geolocation;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.ProgramMode;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.StationMap_GalleryAdapter;
import AppZappy.NIRailAndBus.ui.widgets.StationMap_Overlay;
import AppZappy.NIRailAndBus.ui.widgets.StationMap_TrainOverlay;
import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.util.ApplicationInformation;
import AppZappy.NIRailAndBus.util.L;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;

public class StationMap extends MapActivity
{
	private IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	
	private MapView mapView;
	
	private List<Overlay> mapOverlays;
	
	private StationMap_Overlay pinOverlay;
	private StationMap_Overlay dotOverlay;
	
	private StationMap_TrainOverlay trainOverlay;
	private Handler trainDotUpdater = new Handler();
	private static final int TRAIN_DOT_UPDATE_DELAY = 500;
	
	Runnable trainDotUpdater_func = new Runnable()
	{
		private short last_time = 0;
		private final static int jump_size = 120000;
		private int counter = jump_size - 1;
		public void run()
		{
			short currentTime = dataInterface.getCurrentTime();
			double seconds = dataInterface.getMinuteProgress();
			
			counter += TRAIN_DOT_UPDATE_DELAY;
			
			if (counter > jump_size)
			{
				L.d("2 minute update");
				counter -= jump_size;
				
				if (last_time == 0)
					last_time = (short) (currentTime-1);
				
				// Remove the finished trains
				trainOverlay.clear_old(currentTime); 
				
				short startTime = (short)(last_time+1);
				short endTime = (short)(currentTime + 1);
				
				
				
				// Add new routes to the map
				List<Route> routes = dataInterface.getUpcomingRoutes(startTime, endTime);

				if (routes.size() > 0)
				{
					trainOverlay.add_routes(routes, currentTime, seconds); 
				}
			}
			
			trainOverlay.update(currentTime, seconds); // send in second data. Call every few seconds
			mapView.invalidate();
			
			trainDotUpdater.removeCallbacks(trainDotUpdater_func); // remove the old callback
			trainDotUpdater.postDelayed(trainDotUpdater_func, TRAIN_DOT_UPDATE_DELAY); // register a new one
		}
	};
	
	private MapController mapController;
	
	private IEventListener locationUpdatedListener = new IEventListener()
	{
		public void action(Object optionalData)
		{
			setCurrentLocation((Geolocation) optionalData);
		}
	};
	
	private StationMap_GalleryAdapter stationGalleryAdapter;
	private Gallery stationGallery;
	private static Geolocation myLocation = null;
	private MyLocationOverlay locationOverlay;
	
	private List<Location> locationsToDisplay;
	
	/**
	 * Is the map currently zoomed in close? 
	 */
	private boolean zoomedClose = true;
	
	/**
	 * Used to update the map overlays to either the pin or the dot
	 */
	private Handler zoomHandler = new Handler();
	
	/**
	 * The time between checking if the icon type is of the correct type
	 */
	public static final int ZOOM_CHECKING_DELAY = 500; // ms
	
	Runnable zoomIconChecker = new Runnable()
	{
		public void run()
		{
			checkMapIcons();
			
			zoomHandler.removeCallbacks(zoomIconChecker); // remove the old callback
			zoomHandler.postDelayed(zoomIconChecker, ZOOM_CHECKING_DELAY); // register a new one
		}
	};
	

	IEventListener map_StationIconClicked = new IEventListener()
	{
		public void action(Object optionalData)
		{
			Location location = (Location) optionalData;
			_map_SelectedItem = location;
			registerForContextMenu(mapView);
			openContextMenu(mapView);
		}
	};
	
	IEventListener map_TrainIconClicked = new IEventListener()
	{
		public void action(Object optionalData)
		{
			Route location = (Route) optionalData;
			_map_SelectedItem = location;
			registerForContextMenu(mapView);
			openContextMenu(mapView);
		}
	};
	
	
	Object _map_SelectedItem = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_station_finder_mapview);
		
		mapView = new MapView(this, ApplicationInformation.getGoogleMapsAPI(this));
		mapView.setClickable(true);
		((LinearLayout) findViewById(R.id.stationFinderUI_MapHolder)).addView(mapView);
		
		//mapView = (MapView) findViewById(R.id.stationFinderUI_mapview);
		mapView.setBuiltInZoomControls(true);
		
		
		
		// ADDING THE OVERLAYS
		mapOverlays = mapView.getOverlays();
		
		
		
		// The station pins overlay
		Drawable pinOverlayImage = getResources().getDrawable(R.drawable.map_station_marker);
		pinOverlay = new StationMap_Overlay(pinOverlayImage);
		pinOverlay.itemTapped.addListener(map_StationIconClicked);
		mapOverlays.add(pinOverlay);
		
		// The station dots overlay
		Drawable dotOverlayImage = getResources().getDrawable(R.drawable.map_station_marker_dot);
		dotOverlay = new StationMap_Overlay(dotOverlayImage, true);
		dotOverlay.itemTapped.addListener(map_StationIconClicked);
		//mapOverlays.add(dotOverlay);
		

		// the moving train dots
		Drawable trainOverlayImage = getResources().getDrawable(R.drawable.map_train_marker);
		trainOverlay = new StationMap_TrainOverlay(trainOverlayImage);
		trainOverlay.itemTapped.addListener(map_TrainIconClicked);
		mapOverlays.add(trainOverlay);
		
		// The google location overlay
		locationOverlay = new MyLocationOverlay(this, mapView);
		mapOverlays.add(locationOverlay);
		
		
		
		mapController = mapView.getController();
		mapController.setZoom(14); 
		centerLocation(Settings.getCityHall().getGeoPoint());
		
		stationGalleryAdapter = new StationMap_GalleryAdapter(this);
		stationGallery = (Gallery) findViewById(R.id.stationFinderUI_station_gallery_view);
		stationGallery.setAdapter(stationGalleryAdapter);
		stationGallery.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				StationWindow.openNew(StationMap.this, stationGalleryAdapter.getItem(position).location);
			}
		});
		UIUtils.setGallerySize_Resource(this, stationGallery, R.dimen.item_width, R.dimen.stationMap_galleryHeight);

		
		
		
		
		myLocation = dataInterface.getCurrentLocation(); 
		setCurrentLocation(myLocation);
	}
	
	
	public void checkMapIcons()
	{
		int zoomLevel = mapView.getZoomLevel();
		
		boolean close = false;
		if (zoomLevel > ProgramMode.singleton().getZoomThreshold())
		{
			// small icons
			close = true;
		}
		
		if (close != zoomedClose)
		{
			// changed
			zoomedClose = close;
			
			if (zoomedClose)
			{
				// normal icon
				mapOverlays.remove(0);
				mapOverlays.add(0, this.pinOverlay);
				mapView.invalidate();
			}
			else
			{
				// dot icon
				mapOverlays.remove(0);
				mapOverlays.add(0, this.dotOverlay);
				
				mapView.invalidate();
			}
		}
	}
	

	
	private void add_all_current_trains_to_overlay()
	{
		short currentTime = dataInterface.getCurrentTime();
		
		List<Route> routes = dataInterface.getRunningRoutes(currentTime, dataInterface.getCurrentDayOfWeek());
		L.d("Adding routes to display: " + routes.size());
		trainOverlay.add_routes(routes, currentTime, dataInterface.getMinuteProgress());
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();

		
		LoadData.initialise(this);
		
		locationOverlay.enableMyLocation();
		locationOverlay.enableCompass();
		
		dataInterface.startLocationGPSUpdates();
		// register for location updates
		dataInterface.registerLocationUpdates(locationUpdatedListener);
		
		
		zoomHandler.postDelayed(zoomIconChecker, ZOOM_CHECKING_DELAY);
		trainDotUpdater.postDelayed(trainDotUpdater_func, TRAIN_DOT_UPDATE_DELAY);
		add_all_current_trains_to_overlay();
		
		
		
		// add the data to the display
		locationsToDisplay = dataInterface.getAllLocations();
		
		pinOverlay.addRange(locationsToDisplay);
		dotOverlay.addRange(locationsToDisplay);
		
		
		update_station_gallery();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		
		locationOverlay.disableMyLocation();
		locationOverlay.disableCompass();
		
		dataInterface.removeLocationUpdates(locationUpdatedListener);
		dataInterface.stopLocationGPSUpdates();
		
		zoomHandler.removeCallbacks(zoomIconChecker); // stop the map from updating
		trainDotUpdater.removeCallbacks(trainDotUpdater_func); // stop updating the train locations
		trainOverlay.clear();
	}
	
	
	private void update_station_gallery()
	{
		List<LocationDistance> items = dataInterface.getLocationDistances(locationsToDisplay);
		
		// too many items, clear set
		if (stationGalleryAdapter.getCount()> items.size())
			stationGalleryAdapter.clear();
		
		// add new items to fill gallery
		for (int i=stationGalleryAdapter.getCount();i<items.size();i++)
		{
			stationGalleryAdapter.add(items.get(i));
		}
		
		// update displayed information
		for(int i=0;i<stationGalleryAdapter.getCount();i++)
		{
			LocationDistance new_data = items.get(i);
			LocationDistance displayed = stationGalleryAdapter.getItem(i);
			
			displayed.distance = new_data.distance;
			displayed.location = new_data.location;
		}
		
		stationGalleryAdapter.notifyDataSetChanged();
	}
	

	
	private void setCurrentLocation(Geolocation currentLocation)
	{
		myLocation = currentLocation;
		//centerLocation(myLocation.getGeoPoint());
		
		update_station_gallery();
	}
	
	
	
	private void centerLocation(GeoPoint center)
	{
		mapController.animateTo(center);
	};
	
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	public void onHomeClick(View view)
	{
		UIUtils.goHome(this);
	}
	
	public void onOpenListViewClick(View view)
	{
		Context context = view.getContext();
		final Intent intent = new Intent(context, StationList.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(intent);
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
	
	
//	class MyLocationOverlay extends com.google.android.maps.Overlay
//	{
//		@Override
//		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
//		{
//			super.draw(canvas, mapView, shadow);
//			Paint paint = new Paint();
//			// Converts lat/lng-Point to OUR coordinates on the screen.
//			Point myScreenCoords = new Point();
//			mapView.getProjection().toPixels(myLocation.getGeoPoint(), myScreenCoords);
//			paint.setStrokeWidth(0.1f);
//			paint.setARGB(255, 255, 255, 255);
//			paint.setStyle(Paint.Style.STROKE);
//			paint.
//			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map_me_marker);
//			canvas.drawBitmap(bmp, myScreenCoords.x, myScreenCoords.y, paint);
//			
//			return true;
//		}
//	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo)
	{  
		super.onCreateContextMenu(menu, v, menuInfo);
		if (_map_SelectedItem instanceof Location)
		{
			UIUtils.createLocationMenu(menu, v, (Location) _map_SelectedItem);
		}
		else if (_map_SelectedItem instanceof Route)
		{
			short currentTime = dataInterface.getCurrentTime();
			UIUtils.createTrainMenu(menu, v, (Route) _map_SelectedItem, currentTime);
		}
		
	} 
	
	@Override  
    public boolean onContextItemSelected(MenuItem item)
	{  
		if (_map_SelectedItem instanceof Location)
		{
			UIUtils.handleLocationClickAction(this, item, (Location) _map_SelectedItem);
		}
		else if (_map_SelectedItem instanceof Route)
		{
			UIUtils.handleTrainClickAction(this, item, (Route) _map_SelectedItem);
		}
        return super.onContextItemSelected(item);  
    } 

}
	

