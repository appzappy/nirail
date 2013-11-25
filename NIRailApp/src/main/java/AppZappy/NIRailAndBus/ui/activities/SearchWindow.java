package AppZappy.NIRailAndBus.ui.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.events.IEventListener;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.ProgramMode;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.Search_LocationAutoCompleteAdapter;
import AppZappy.NIRailAndBus.ui.adapters.Search_StationAdapter;
import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.userdata.Favourite.FavouriteType;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SearchWindow extends Activity
{
	public static final int EMPTY = 0, SELECT = 1, SELECTED = 2;
	public static final String SOURCE_BUNDLE_NAME = "SOURCE";
	public static final String DESTINATION_BUNDLE_NAME = "DESTINATION";
	public static final String BUNDLE_NAME = "SearchDetails";
	
	public static void openNew(Context context, String source, String destination)
	{
		Intent res = new Intent(context, SearchWindow.class);
		Bundle b = new Bundle();
		if (source != null)
			b.putCharSequence(SOURCE_BUNDLE_NAME, source);
		if (destination != null)
			b.putCharSequence(DESTINATION_BUNDLE_NAME, destination);
		res.putExtra(BUNDLE_NAME, b);
		context.startActivity(res);
	}
	public static void openNew(Context context, Location source, Location destination)
	{
		String s = (source == null) ? null : source.getRealName();
		String d = (destination == null) ? null : destination.getRealName();
		
		openNew(context, s, d);
	}
	public static void openNew(Context context)
	{
		Intent res = new Intent(context, SearchWindow.class);
		Bundle b = new Bundle();
		res.putExtra(BUNDLE_NAME, b);
		context.startActivity(res);
	}
	
	/**
	 * Used to retrieve raw data from the system
	 */
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();

	
	Search_StationAdapter sourceGalleryAdapter, destinationGalleryAdapter;
	TextView sourceDescriptionTextView, destinationDescriptionTextView;
	Gallery sourceGallery, destinationGallery;
	AutoCompleteTextView sourceStationAutoComplete, destinationStationAutoComplete;
	Search_LocationAutoCompleteAdapter sourceLocationsAdapter, destinationLocationsAdapter;
	
	TextView sourceSelectedTextView, destinationSelectedTextView;
	View sourceSelectedViewBox, destinationSelectedViewBox;
	ImageView sourceSelectedImageView, destinationSelectedImageView;
	
	LinearLayout searchButton;
	
	private ViewFlipper sourceFlipper, destinationFlipper;
	private LinearLayout sourceSelectionBox, destinationSelectionBox;
	
	Location source, destination;
	
	IEventListener locationUpdatedListener;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_search);
		
		sourceFlipper = (ViewFlipper) findViewById(R.id.searchUI_source_flipper);
		destinationFlipper = (ViewFlipper) findViewById(R.id.searchUI_destination_flipper);
		
		sourceSelectionBox = (LinearLayout) findViewById(R.id.searchUI_source_selection);
		destinationSelectionBox = (LinearLayout) findViewById(R.id.searchUI_destination_selection);
		
		// Description Bars
		sourceDescriptionTextView = (TextView) findViewById(R.id.searchUI_gallerySourceText);
		destinationDescriptionTextView = (TextView) findViewById(R.id.searchUI_galleryDestinationText);
		
		// source gallery
		sourceGallery = (Gallery) this.findViewById(R.id.searchUI_source_gallery_view);
		sourceGalleryAdapter = new Search_StationAdapter(this);
		sourceGallery.setAdapter(sourceGalleryAdapter);
		UIUtils.setGallerySize_Resource(this, sourceGallery, R.dimen.item_width, R.dimen.search_gallery_height);

		
		// source auto complete text view
		sourceStationAutoComplete = (AutoCompleteTextView) findViewById(R.id.searchUI_source_text_view);
		sourceLocationsAdapter = new Search_LocationAutoCompleteAdapter(this);
		sourceStationAutoComplete.setAdapter(sourceLocationsAdapter);
		sourceLocationsAdapter.actv = sourceStationAutoComplete;
		
		
		// destination gallery
		destinationGallery = (Gallery) this.findViewById(R.id.searchUI_destination_gallery_view);
		destinationGalleryAdapter = new Search_StationAdapter(this);
		destinationGallery.setAdapter(destinationGalleryAdapter);
		UIUtils.setGallerySize_Resource(this, destinationGallery, R.dimen.item_width, R.dimen.search_gallery_height);

        
		
		
		// destination auto complete text view
		destinationStationAutoComplete = (AutoCompleteTextView) findViewById(R.id.searchUI_destination_text_view);
		destinationLocationsAdapter = new Search_LocationAutoCompleteAdapter(this);
		destinationStationAutoComplete.setAdapter(destinationLocationsAdapter);
		destinationLocationsAdapter.actv = destinationStationAutoComplete;
		
		// DATA SELECTED FIELDS INITIALISING
		// data selected view boxes
		sourceSelectedViewBox = findViewById(R.id.searchUI_source_station_selected_box);
		destinationSelectedViewBox = findViewById(R.id.searchUI_destination_station_selected_box);
		
		// data selected text views
		sourceSelectedTextView = (TextView) findViewById(R.id.searchUI_source_station_selected_text);
		destinationSelectedTextView = (TextView) findViewById(R.id.searchUI_destination_station_selected_text);
		
		// data selected image views
		sourceSelectedImageView = (ImageView) findViewById(R.id.searchUI_source_station_selected_image);
		destinationSelectedImageView = (ImageView) findViewById(R.id.searchUI_destination_station_selected_image);
		
		// search button
		searchButton = (LinearLayout) findViewById(R.id.searchUI_buttons);
		
		// set up behaviour for the auto complete text views
		// SOURCE FIRST
		sourceStationAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				String name = sourceStationAutoComplete.getText().toString().split(";")[0].trim();
				setSourceLocation(name);
			}
		});
		
		// DESTINATION
		destinationStationAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				String name = destinationStationAutoComplete.getText().toString().split(";")[0].trim();
				setDestinationLocation(name);
			}
		});
		
		// set up clicking behaviour for the gallery's
		// SOURCE
		sourceGallery.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Location location = sourceGalleryAdapter.getItem(position);
				setSourceLocation(location);
			}
		});
		// DESTINATION
		destinationGallery.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Location location = destinationGalleryAdapter.getItem(position);
				setDestinationLocation(location);
			}
		});
		
		

		
		Bundle b = getIntent().getBundleExtra(BUNDLE_NAME);
		if (b != null)
		{
			String s = b.getString(SOURCE_BUNDLE_NAME);
			if (s != null)
				source = dataInterface.getLocation(s);
			String d = b.getString(DESTINATION_BUNDLE_NAME);
			if (d != null)
				destination = dataInterface.getLocation(d);
		}
		
		
		hideAllDetailsOnStage();
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		LoadData.initialise(this);
		
		if (source == null && destination == null)
		{
			displaySourceLocations();
			setStage(Stages.PICK_SOURCE);
		}
		else if (source != null && destination != null)
		{
			setSourceLocation(source);
			setDestinationLocation(destination);
		}
		else
		{
			if (source != null)
			{
				// SOURCE IS SET
				// DESTINATION IS NOT
				setSourceLocation(source);
			}
			else
			{
				// DESTINATION SET
				// SOURCE IS NOT
				setDestinationLocation(destination);
			}
			
		}
		
		
		
	}
	
	@Override
	public void finish()
	{
		super.finish();
		destinationLocationsAdapter.actv = null;
		sourceLocationsAdapter.actv = null;
	}
	
	private void setSourceLocation(String source)
	{
		Location sourceLocation = dataInterface.getLocation(source);
		setSourceLocation(sourceLocation);
	}
	
	/**
	 * Set the current source location
	 * @param source
	 */
	private void setSourceLocation(Location source)
	{
		this.source = source;
		
		sourceSelectedTextView.setText(source.getRealName());

		// set the icon according to if station is favourite or not
		if (dataInterface.isLocationFavourite(source))
		{
			sourceSelectedImageView.setImageResource(UIUtils.getImageForFavouriteType(FavouriteType.station));
		}
		else
		{
			sourceSelectedImageView.setImageResource(UIUtils.getImageForStationType(source.getLocationType()));
		}
		
		
		
		displayDestinationLocations();
		
		setStage(Stages.SET_SOURCE);
		
		if (this.destination == null)
			setStage(Stages.PICK_DESTINATION);
		
		
		if (this.source != null && this.destination != null)
			setStage(Stages.SEARCH);
	}
	
	private void clearSourceLocation()
	{
		boolean hide_search = false;
		if (this.source != null && this.destination != null)
			hide_search = true;
		
		this.source = null;
		
		displaySourceLocations();
		setStage(Stages.PICK_SOURCE);
		if (hide_search)
			setStage(Stages.HIDE_SEARCH);
		
		if (this.destination == null)
		{
			setStage(Stages.HIDE_DESTINATION);
		}
	}
	

	
	private void setDestinationLocation(String destination)
	{
		Location destinationLocation = dataInterface.getLocation(destination);
		setDestinationLocation(destinationLocation);
	}
	
	/**
	 * Set destination location
	 * @param destination
	 */
	private void setDestinationLocation(Location destination)
	{
		this.destination = destination;
		
		destinationSelectedTextView.setText(destination.getRealName());

		// set the icon according to if station is favourite or not
		if (dataInterface.isLocationFavourite(destination))
		{
			destinationSelectedImageView.setImageResource(UIUtils.getImageForFavouriteType(FavouriteType.station));
		}
		else
		{
			destinationSelectedImageView.setImageResource(UIUtils.getImageForStationType(destination.getLocationType()));
		}
		
		
		setStage(Stages.SET_DESTINATION);
		
		if (this.source == null)
		{
			displaySourceLocations();
			setStage(Stages.PICK_SOURCE);
		}
		
		if (this.source != null && this.destination != null)
			setStage(Stages.SEARCH);
	}
	
	private void clearDestinationLocation()
	{
		boolean hide_search = false;
		if (this.source != null && this.destination != null)
			hide_search = true;
		
		this.destination = null;
		
		displayDestinationLocations();
		setStage(Stages.PICK_DESTINATION);
		if (hide_search)
			setStage(Stages.HIDE_SEARCH);
		
		if (this.source == null)
		{
			displaySourceLocations();
			setStage(Stages.HIDE_DESTINATION);
		}
	}

	
	
	private void displaySourceLocations()
	{
		List<Location> locations = ProgramMode.singleton().reachableLocations(destination);
		
		locations = dataInterface.sortLocationsByDistance(locations);

		sourceStationAutoComplete.setText("");
		
		// clear adapters
		sourceGalleryAdapter.clear();
		sourceLocationsAdapter.clear();
		
		// add to the adapters
		for(Location l: locations)
		{
			sourceGalleryAdapter.add(l);
			
			
			sourceLocationsAdapter.add(l.getFullString(';'));
		}
		
		sourceGallery.setSelection(0);
	}
	
	
	
	private void displayDestinationLocations()
	{
		List<Location> locations = ProgramMode.singleton().reachableLocations(source);
		
		Location.sortLocationsByName(locations);
		
		locations = dataInterface.moveFavouriteStationsToFront(locations);
		// clear adapters
		destinationGalleryAdapter.clear();
		destinationLocationsAdapter.clear();
		
		// add to the adapters
		for(Location l: locations)
		{
			destinationGalleryAdapter.add(l);
			destinationLocationsAdapter.add(l.getFullString(';'));
		}
		
		destinationStationAutoComplete.setText("");
		destinationGallery.setSelection(0);
	}
	
	
	
	/**
	 * Used to manage the different stages for the Search UI page
	 */
	private enum Stages
	{
		/**
		 * Pick the source location
		 */
		PICK_SOURCE,
		/**
		 * HIDE THE SOURCE SELECTION FIELDS
		 */
		SET_SOURCE,
		/**
		 * Pick the destination location
		 */
		PICK_DESTINATION,
		/**
		 * Hide the destination selection fields
		 */
		SET_DESTINATION,
		/**
		 * Hide the destination fields
		 */
		HIDE_DESTINATION,
		/**
		 * Search is now valid
		 */
		SEARCH,
		/**
		 * Hide the search fields
		 */
		HIDE_SEARCH
	}
	
	
	public void hideAllDetailsOnStage()
	{
		
		sourceSelectionBox.setVisibility(View.GONE);
		sourceSelectedViewBox.setVisibility(View.GONE);
		
		destinationSelectionBox.setVisibility(View.GONE);
		destinationSelectedViewBox.setVisibility(View.GONE);
		
		searchButton.setVisibility(View.GONE);
	}

	
	private int big_height = -1;
	private int small_height = -1;
	/**
	 * Update the displayed elements according to the active stage
	 * @param stage The new stage
	 */
	private void setStage(Stages stage)
	{
		if (big_height == -1)
		{
			// dip values for big and small heights
			int big = 180; 
			int small = 80;
			
			Resources resources = getResources();
			DisplayMetrics metrics = resources.getDisplayMetrics();
			// convert from DIP to pixels for this device
			float size_big = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, big, metrics);
			float size_small = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, small, metrics);
			
			// save these height values
			big_height = (int) size_big;
			small_height = (int) size_small;
		}
		
		switch(stage)
		{
			case PICK_SOURCE:
				sourceFlipper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, big_height));
				sourceFlipper.setDisplayedChild(SELECT);
				sourceStationAutoComplete.requestFocus();
				UIUtils.hideKeyboard(this, sourceStationAutoComplete);
				break;
			case SET_SOURCE:
				sourceFlipper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, small_height));
				sourceFlipper.setDisplayedChild(SELECTED);
				UIUtils.hideKeyboard(this, sourceStationAutoComplete);
				break;
			case PICK_DESTINATION:
				destinationFlipper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, big_height));
				destinationFlipper.setDisplayedChild(SELECT);
				destinationStationAutoComplete.requestFocus();
				UIUtils.hideKeyboard(this, destinationStationAutoComplete);
				break;
			case SET_DESTINATION:
				destinationFlipper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, small_height));
				destinationFlipper.setDisplayedChild(SELECTED);
				UIUtils.hideKeyboard(this, destinationStationAutoComplete);
				break;
			case HIDE_DESTINATION:
				destinationFlipper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, small_height));
				destinationFlipper.setDisplayedChild(EMPTY);
				break;
			case SEARCH:
				searchButton.setVisibility(View.VISIBLE);
				Animation show = new AlphaAnimation(0.0f, 1.0f);
				show.setDuration(500);
				show.setFillEnabled(true);
				show.setFillAfter(true);
				searchButton.startAnimation(show);
				UIUtils.hideKeyboard(this, destinationStationAutoComplete);
				UIUtils.hideKeyboard(this, sourceStationAutoComplete);
				break;
			case HIDE_SEARCH:
				searchButton.setVisibility(View.GONE);
				Animation hide = new AlphaAnimation(1.0f, 0.0f);
				hide.setDuration(500);
				searchButton.startAnimation(hide);
				break;
		}
		
		findViewById(R.id.searchLayout).invalidate();
		findViewById(R.id.scrolling).invalidate();
		
	}
	
	/** Handle "Home" action */
	public void onHomeClick(View v) {
		UIUtils.goHome(this);
    }
			
	/** Handle "Search" action */
	public void onSearchClick(View v)
	{
		if (this.source == null || this.destination == null)
			return;
		
		JourneySet.openNew(this, this.source, this.destination);
    }
	
	/** Handle "Journey Planner" action */
	public void onJPClick(View v)
	{
		if (this.source == null || this.destination == null)
			return;
		
		String from = "departsearchquery";
		String to = "arrivesearchquery";
		Calendar now = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String date = sdf.format(now.getTime());
		
		String url = "http://www.translink.co.uk/Journey-Planner/?"+from+"=" + journeyPlannerFormat(this.source.getRealName() + " (NIR) Rail Stn") + "&" + to + "=" + journeyPlannerFormat(this.destination.getRealName() + " (NIR) Rail Stn") + "&bybus=True&bytrain=True&traveldate=" + date;
		Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));  
		  
		try {  
		 this.startActivity(intent);  
		} catch (ActivityNotFoundException ex) {  
		 // do something about the exception, or not ...  
		}  
	}
	
	String journeyPlannerFormat(String in)
	{
		String out = in.replace(' ', '+');
		return out;
	}
	
	/** Handle "Clear From" form click */
	public void clearSource(View v)
	{
		clearSourceLocation();
	}
	
	public void clearDestination(View v)
	{
		clearDestinationLocation();
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