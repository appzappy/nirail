package AppZappy.NIRailAndBus.mode;

import java.util.List;
import java.util.Set;

import android.content.Context;
import android.location.LocationManager;
import android.view.MenuItem;

import AppZappy.NIRailAndBus.data.enums.DayOfWeek;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.model.LocationDistance;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.delays.DelayItem;
import AppZappy.NIRailAndBus.events.Event;
import AppZappy.NIRailAndBus.events.IEventListener;
import AppZappy.NIRailAndBus.geolocation.Geolocation;
import AppZappy.NIRailAndBus.notifications.TrainReminder;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.pathfinding.RouteStopPosition;
import AppZappy.NIRailAndBus.userdata.Favourite;

/**
 * A communication class for retrieving data from the data set
 */
public interface IUIInterface
{
	public Context getAndroidContext ();
	public void setAndroidContext (Context context);
	public void setTrainReminderService(TrainReminder service);
	public TrainReminder getTrainReminderService();
	public boolean isLocationFavourite(Location location);
	public void toggleLocationFavourite(Location location);
	public void setLocationFavourite(Location location, boolean fav);
	public List<Favourite> getFavourites();
	public void removeFavourite(Favourite favourite);
	
	public boolean isJourneyFavourite(Location source, Location destination);
	public void setJourneyFavourite(Location source, Location destination, boolean value);
	public void toggleLocationFavourite(Location source, Location destination);
	
	
	
	
	public Event getDelayLoadedEvent();
	public List<DelayItem> getLoadedDelays();
	public void startDownloadingDelays();
	
	
	
	
	public List<Location> getPossibleStations (String substring);
	public List<Location> getReachableLocations(Location source);
	public List<Location> moveFavouriteStationsToFront(List<Location> locations);
	public List<Location> getAllLocations();
	public List<Location> getAllLocations_Not(Location destination);
	public List<Location> sortLocationsByDistance(List<Location> locations);
	public List<String> locationToString(List<Location> locations);
	public Set<String> locationsIncludingAliasesToStrings(List<Location> locations);
	public Location getLocation(String locationName);
	public Location getLocation(int location_id);
	public List<LocationDistance> getLocationDistances(List<Location> locationsToDisplay);
	public List<LocationDistance> getLocationDistances_SortByName(List<Location> locationsToDisplay);
	public List<Location> filterLocations(List<Location> locationsToDisplay, String searchText);

	public List<RouteStopPosition> getUpcomingArrivals(int stations);
	public List<Journey> getUpcomingDepartures(int stations);
	
	
	public List<RouteStopPosition> getLocationRoutes(Location location, int numberItems);

	public List<Route> getUpcomingRoutes(short startTime, short endTime);
	public List<Route> getRunningRoutes(short currentTime, DayOfWeek currentDayOfWeek);


	public int getStartingDate(Route route);
	public int getEndingDate(Route route);
	
	
	public DayOfWeek getCurrentDayOfWeek();
	public short getCurrentTime();
	public double  getMinuteProgress();
	
	
	
	public List<List<Journey>> getJourneys(Location source, Location destination);
	
	
	
	
	public void startListeningForLocation(LocationManager locationManger);
	public void startListeningForLocation(Object locationManger);
	public Geolocation getCurrentLocation();
	public void registerLocationUpdates(IEventListener listener);
	public void removeLocationUpdates(IEventListener listener);
	public String getFormattedDistanceToLocation(Location location);
	
	public void startLocationGPSUpdates();
	public void stopLocationGPSUpdates();
	public void copyTrainToClipboard(Journey journey);
	
	public void setTrainReminder(Context context, final Journey journey);

	public void handleJourneyClickAction(Context context, MenuItem item, Journey journey);

}
