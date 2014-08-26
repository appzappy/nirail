package appzappy.nirail.mode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.text.ClipboardManager;
import android.view.MenuItem;
import android.widget.Toast;

import appzappy.nirail.data.LoadData;
import appzappy.nirail.data.enums.DayOfWeek;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.LocationDistance;
import appzappy.nirail.data.model.Route;
import appzappy.nirail.delays.DelayItem;
import appzappy.nirail.events.Event;
import appzappy.nirail.events.IEventListener;
import appzappy.nirail.geolocation.Geolocation;
import appzappy.nirail.geolocation.LocationSource;
import appzappy.nirail.notifications.TrainReminder;
import appzappy.nirail.pathfinding.IPathfindingAlgorithm;
import appzappy.nirail.pathfinding.Journey;
import appzappy.nirail.pathfinding.JourneyPortion;
import appzappy.nirail.pathfinding.JourneySorting;
import appzappy.nirail.pathfinding.RouteStopPosition;
import appzappy.nirail.pathfinding.SQL_UpcomingTrainsAtStation;
import appzappy.nirail.ui.UIUtils;
import appzappy.nirail.ui.activities.JourneyWindow;
import appzappy.nirail.userdata.Favourite;
import appzappy.nirail.userdata.FavouriteManager;
import appzappy.nirail.util.C;
import appzappy.nirail.util.L;
import appzappy.nirail.util.timing.TimeFormatter;



/**
 * Handles communications between UI and the data set. <b>ONLY CREATED BY A FACTORY</b>
 */
class UIInterfaceInitial implements IUIInterface
{
	private Context androidContext = null;
	public Context getAndroidContext () { return androidContext;}
	public void setAndroidContext (Context context) { androidContext = context;}
	
	private appzappy.nirail.delays.IDelaySource delays = null;
	
	private static IUIInterface _singleton = null;
	
	private static Object _lock = new Object();

	private TrainReminder _reminder;
	
	/**
	 * Get the instance of this object
	 * @return The instance for UI data retrieval
	 */
	public static IUIInterface getInstance()
	{
		if (_singleton == null)
		{
			synchronized (_lock)
			{
				if (_singleton == null)
				{
					_singleton = new UIInterfaceInitial();					
				}
			}
		}
		
		return _singleton;
	}
	
	public void setTrainReminderService(TrainReminder service)
	{
		_reminder = service;
	}
	
	public TrainReminder getTrainReminderService()
	{
		if (_reminder == null)
		{
			_reminder = new TrainReminder();
		}
		return _reminder;
	}
	
	/**
	 * Find if a Location is favourited
	 */
	public boolean isLocationFavourite(Location location)
	{
		return FavouriteManager.getInstance().isFavourite(null, location);
	}
	
	/**
	 * Set a Locations favourited status
	 */
	public void setLocationFavourite(Location location, boolean value)
	{
		FavouriteManager.getInstance().setFavourite(null, location, value);
	}
	
	/**
	 * Toggle a location's favourited status
	 */
	public void toggleLocationFavourite(Location location)
	{
		FavouriteManager.getInstance().toggleFavourite(null, location);
	}
	
	
	public boolean isJourneyFavourite(Location source, Location destination)
	{
		return FavouriteManager.getInstance().isFavourite(source, destination);
	}
	
	public void setJourneyFavourite(Location source, Location destination, boolean value)
	{
		FavouriteManager.getInstance().setFavourite(source, destination, value);
	}
	
	public void toggleLocationFavourite(Location source, Location destination)
	{
		FavouriteManager.getInstance().toggleFavourite(source, destination);
	}
	
	
	public List<Favourite> getFavourites()
	{
		return FavouriteManager.getInstance().getList();
	}
	
	public void removeFavourite(Favourite favourite)
	{
		FavouriteManager.getInstance().remove(favourite);
	}
	
	
	
	
	/**
	 * Get the Upcoming trains for the nearest X stations
	 */
	public List<RouteStopPosition> getUpcomingArrivals(int stations)
	{
		L.d("Getting Upcoming Arrivals");
		List<Location> nearbyLocations = LoadData.getNetwork().getNearbyLocations(stations, getCurrentLocation());
		short startTime = getCurrentTime();
		short endTime = (short)(startTime + 90); // end 2 hours after start
		SQL_UpcomingTrainsAtStation arr = new SQL_UpcomingTrainsAtStation();
	
		List<RouteStopPosition> routeStops = arr.getUpcomingTrains(LoadData.getTimetable(), nearbyLocations, startTime, endTime, getCurrentDayOfWeek(), 15);
		return routeStops;
	}
	
	public List<Journey> getUpcomingDepartures(int stations)
	{
		L.d("Getting Upcoming Departures: For " + stations + " stations");
		List<Location> nearbyLocations = LoadData.getNetwork().getNearbyLocations(stations, getCurrentLocation());
		short startTime = getCurrentTime();
		short endTime = (short)(startTime + 120); // end 2 hours after start
		SQL_UpcomingTrainsAtStation arr = new SQL_UpcomingTrainsAtStation();
	
		List<Journey> routeStops = arr.getUpcomingDepartures(LoadData.getTimetable(), nearbyLocations, startTime, endTime, getCurrentDayOfWeek(), 15);
		return routeStops;
	}
	


	public Event getDelayLoadedEvent()
	{
		return delays.getDownloadedEvent();
	}
	public List<DelayItem> getLoadedDelays()
	{
		return delays.getItems();
	}
	public void startDownloadingDelays()
	{
		delays.start();
	}
	
		
	/**
	 * Get the locations whose names contain the substring
	 * @param substring The substring to look for
	 * @return Collect of Locations whose name contains substrings
	 */
	public List<Location> getPossibleStations (String substring)
	{
		return LoadData.getNetwork().getMatchingLocations(substring);
	}
	
	public List<Location> getAllLocations()
	{
		return LoadData.getNetwork().getAllLocations();
	}

	public List<Location> getAllLocations_Not(Location location)
	{
		if (location == null)
			return getAllLocations();
		
		List<Location> locs = getAllLocations();
		locs.remove(location);
		return locs;
	}
	
	public List<Location> sortLocationsByDistance(List<Location> locations)
	{
		List<LocationDistance> data = Location.getLocationDistances(locations, this.getCurrentLocation());
		LocationDistance.sortLocationsByDistance(data);
		
		return LocationDistance.getLocations(data);
	}
	
	public List<String> locationToString(List<Location> locations)
	{
		int num = locations.size();
		List<String> out = new ArrayList<String>(num);
		for(int i=0;i<num;i++)
		{
			out.add(locations.get(i).getRealName());
		}
		return out;
	}
	
	public Set<String> locationsIncludingAliasesToStrings(List<Location> locations)
	{
		int num = locations.size();
		Set<String> out = new HashSet<String>();
		for(int i=0;i<num;i++)
		{
			Location l = locations.get(i); 
			out.add(l.getRealName());
			int aliasCount = l.countAliases();
			for (int j=0;j<aliasCount;j++)
			{
				out.add(l.getAlias(j));
			}
		}
		return out;
	}
	
	public List<Location> filterLocations(List<Location> locationsToDisplay, String searchText)
	{
		if (searchText == null || searchText.equals(""))
			return locationsToDisplay;
		
		List<Location> out = new ArrayList<Location>();
		
		for(Location l: locationsToDisplay)
		{
			if (l.isSubstringOfAlias(searchText))
				out.add(l);
		}
		return out;
	}
	
	
	

	public List<RouteStopPosition> getLocationRoutes(Location location, int numberItems)
	{
		L.d("Getting Location Routes");
		SQL_UpcomingTrainsAtStation trains = new SQL_UpcomingTrainsAtStation();
		
		List<Location> loc = new ArrayList<Location>();
		loc.add(location);
		
		short start = getCurrentTime();
		short end = (short) (start + 4*60); // within the next 4 hours
		
		List<RouteStopPosition> stops = trains.getUpcomingTrains(LoadData.getTimetable(), loc, start, end, getCurrentDayOfWeek(), 40);
		
		return stops;
	}
	
	
	
	public List<Route> getUpcomingRoutes(short startTime, short endTime)
	{
		return Route.from_SQL_Starting_Between(startTime, endTime, getCurrentDayOfWeek());
	}

	public List<Route> getRunningRoutes(short currentTime, DayOfWeek currentDayOfWeek)
	{
		return Route.from_SQL_Running_Currently(currentTime, currentDayOfWeek);
	}

	
	

	public int getStartingDate(Route route)
	{
		int routeStart = route.get_start_date();
		int serviceStart = route.get_service().get_start_date();
		
		if (serviceStart == 0 && routeStart == 0)
		{
			// both are 0
			return 0;
		}
		else if (serviceStart > 0 && routeStart > 0)
		{
			// both are possible
			// return the larger value
			return Math.max(serviceStart, routeStart);
		}
		else if (serviceStart > 0)
		{
			// only service has date set
			return serviceStart;
		}
		else
		{
			// only route has date set
			return routeStart;
		}
	}
	public int getEndingDate(Route route)
	{
		int routeEnd = route.get_end_date();
		int serviceEnd = route.get_service().get_end_date();
		
		if (serviceEnd == 0 && routeEnd == 0)
		{
			// both are 0
			return 0;
		}
		else if (serviceEnd > 0 && routeEnd > 0)
		{
			// both are possible
			// return the smaller value
			return Math.min(serviceEnd, routeEnd);
		}
		else if (serviceEnd > 0)
		{
			// only service has date set
			return serviceEnd;
		}
		else
		{
			// only route has date set
			return routeEnd;
		}
	}
	
	

	public List<Location> getReachableLocations(Location source)
	{
		return ProgramMode.singleton().reachableLocations(source);
	}
	
	public List<Location> moveFavouriteStationsToFront(List<Location> locations)
	{
		List<Favourite> favs = getFavourites();
		
		List<Location> sorted = new ArrayList<Location>(locations.size());
		
		int favCount = favs.size();
		for (int i=0;i<favCount;i++)
		{
			Favourite f = favs.get(i);
			if (f.isLinkedRoute()) // linked routes shouldn't be displayed
				continue;
			
			Location favouriteStation = getLocation(f.getDestination());
			if (favouriteStation == null) // station doesnt exist?
				continue;
			if(!locations.contains(favouriteStation)) // not in the list so don't add it
				continue;
			
			// add this favourite station to the front of the list
			sorted.add(favouriteStation);
		}
		
		for(int i=0;i<locations.size();i++)
		{
			Location l = locations.get(i);
			if (sorted.contains(l))
				continue;
			sorted.add(l);
		}
		return sorted;
	}

	public List<LocationDistance> getLocationDistances(List<Location> locationsToDisplay)
	{
		if(locationsToDisplay==null)
		{
			return new ArrayList<LocationDistance>();
		}
		List<LocationDistance> items = Location.getLocationDistances(locationsToDisplay, LocationSource.getLocationSource().getCurrentLocation());
		LocationDistance.sortLocationsByDistance(items);
		return items;
	}
	
	public List<LocationDistance> getLocationDistances_SortByName(List<Location> locationsToDisplay)
	{
		if(locationsToDisplay==null)
		{
			return new ArrayList<LocationDistance>();
		}
		List<LocationDistance> items = Location.getLocationDistances(locationsToDisplay, LocationSource.getLocationSource().getCurrentLocation());
		LocationDistance.sortLocationsDistancesByName(items);
		return items;
	}
	
	
	public DayOfWeek getCurrentDayOfWeek()
	{
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_WEEK);
		switch(day)
		{
			case Calendar.MONDAY:
				return DayOfWeek.Monday;
			case Calendar.TUESDAY:
				return DayOfWeek.Tuesday;
			case Calendar.WEDNESDAY:
				return DayOfWeek.Wednesday;
			case Calendar.THURSDAY:
				return DayOfWeek.Thursday;
			case Calendar.FRIDAY:
				return DayOfWeek.Friday;
			case Calendar.SATURDAY:
				return DayOfWeek.Saturday;
			default:
				return DayOfWeek.Sunday;
		}
	}
	public short getCurrentTime()
	{
//		if (true)
//			return TimeFormatter.getTimeFromHourMinutes(22, 7);
		Calendar now = Calendar.getInstance();
		short normal = TimeFormatter.getTimeFromHourMinutes(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
		if (normal <= 60)
			normal += 24*60;
		return normal;
	}

	public double getMinuteProgress()
	{
		Calendar now = Calendar.getInstance();
		double millisecond = now.get(Calendar.MILLISECOND); 
		double second = (double) now.get(Calendar.SECOND) + millisecond / 1000;
		return second / 60;
	}
	
	
	public List<List<Journey>> getJourneys(Location source, Location destination)
	{
		IPathfindingAlgorithm alg = ProgramMode.singleton().pathfindingAlgorithm();
		
		List<List<Journey>> journeys = alg.findPaths(LoadData.getTimetable(), source, destination);
		
		int total = 0;
		for(int i=0;i<journeys.size();i++)
		{
			total += journeys.get(i).size();
		}
		L.d("Total Journeys: " + total);
		for(int i=0;i<journeys.size();i++)
		{
			L.d("Day " + i + "\t# Journey Results: " + journeys.get(i).size());
		}
		for(List<Journey> j : journeys)
		{
			// Sorting by arrival time
			if (j != null && j.size() > 1)
				JourneySorting.sortByEndTime(j);
		}
		
		return journeys;
	}
	
	

	
	
	public void startListeningForLocation(Object locationManger)
	{
		startListeningForLocation((LocationManager) locationManger);
	}
	
	public void startListeningForLocation(LocationManager locationManger)
	{
		LocationSource.getLocationSource().start(locationManger);
	}
	
	public void startLocationGPSUpdates()
	{
		LocationSource.getLocationSource().startGPS();
	}
	public void stopLocationGPSUpdates()
	{
		LocationSource.getLocationSource().stopGPS();
	}
	
	public Geolocation getCurrentLocation()
	{
		return LocationSource.getLocationSource().getCurrentLocation();
	}

	public Location getLocation(String locationName)
	{
		return LoadData.getNetwork().get(locationName);
	}

	public Location getLocation(int location_id)
	{
		return LoadData.getNetwork().get(location_id);
	}
	
	public String getFormattedDistanceToLocation(Location location)
	{
		LocationDistance locD = new LocationDistance(location, this.getCurrentLocation());
		return locD.getFormatedDistance();
	}
	
	
	
	
	public void registerLocationUpdates(IEventListener listener)
	{
		LocationSource.getLocationSource().newLocation.addListener(listener);
	}
	public void removeLocationUpdates(IEventListener listener)
	{
		LocationSource.getLocationSource().newLocation.removeListener(listener);
	}

	
	
	
	
	private UIInterfaceInitial()
	{
		delays = new appzappy.nirail.delays.TwitterSource();
	}
	
	public void copyTrainToClipboard(Journey journey)
	{
		String transport = journey.getFirstPortion().getTransportType().toString();
		String message = "";
		
		for (int i=0;i<journey.size();i++)
		{
			JourneyPortion portion = journey.getPortion(i);
			String startTime = portion.getStartTimeFormatted();
			String startStation = portion.getStart().getRealName();
			String endTime = portion.getEndTimeFormatted();
			String endStation = portion.getEnd().getRealName();
			
			message += transport + " leaves " + startStation + " at " + startTime
				+ " and arrives at " + endStation + " at " + endTime + C.new_line();
			message += "-----------------" + C.new_line();
		}
		message += C.new_line();
		message += "Sent from NI Rail for Android";
		
		
		ClipboardManager clipboard = (ClipboardManager) getAndroidContext().getSystemService(Context.CLIPBOARD_SERVICE); 
		clipboard.setText(message);
		
		CharSequence toast_text = transport + " journey details copied to the clipboard.";
		Toast toast = Toast.makeText(this.getAndroidContext(), toast_text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void setTrainReminder(Context context, final Journey journey)
	{
		final CharSequence[] items = {"5 mins before", "10 mins before", "15 mins before", "20 mins before", "25 mins before", "30 mins before"};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Remind me");
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	getTrainReminderService().setReminder(journey, (item+1)*5);
		    	getTrainReminderService().fire();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void handleJourneyClickAction(Context context, MenuItem item, Journey journey) 
	{
		if (item.getTitle().equals(UIUtils.ROUTE_MENU_OPTION__COPY_TRAIN))
		{
			this.copyTrainToClipboard(journey);
		}
		
		if (item.getTitle().equals(UIUtils.ROUTE_MENU_OPTION__ROUTE_PAGE))
		{
			JourneyWindow.openNew(context, journey);
		}
		
		if (item.getTitle().equals(UIUtils.ROUTE_MENU_OPTION__TRAIN_REMINDER))
		{
			this.setTrainReminder(context, journey);
		}
	}
}
