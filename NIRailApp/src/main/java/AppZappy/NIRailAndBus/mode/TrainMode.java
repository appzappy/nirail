package AppZappy.NIRailAndBus.mode;

import java.util.List;

import AppZappy.NIRailAndBus.data.enums.TransportType;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.pathfinding.IPathfindingAlgorithm;
import AppZappy.NIRailAndBus.pathfinding.SQL_Pathfinding_Multijourney;

public class TrainMode implements IProgramMode
{
	private IUIInterface data = null;
	public TrainMode (IUIInterface data)
	{
		this.data = data;
	}
	public TrainMode()
	{
		this(UIInterfaceFactory.getInterface());
	}

	public List<Location> reachableLocations(Location location)
	{
		List<Location> locations = data.getAllLocations_Not(location);
		return locations;
	}

	public IPathfindingAlgorithm pathfindingAlgorithm()
	{
		return new SQL_Pathfinding_Multijourney();
	}

	public TransportType getMode()
	{
		return TransportType.Train;
	}

	
	public String twitterURL()
	{
		//http://idfromuser.org/
		return "http://api.twitter.com/1/statuses/user_timeline.rss?screen_name=nirailways";
	}

	
	/**
	 * Remove the nirailways: text from the start
	 */
	public int twitterNumberCharactersToRemoveFromDescription()
	{
		return 12;
	}

	public static final String TWITTER_GUID_START = "http://twitter.com/nirailways/statuses/";
	public static final int TWITTER_GUID_START_LENGTH = 39;
	public int twitterGUIDStartLength()
	{
		return TWITTER_GUID_START_LENGTH;
	}

	public static final String DATABASE_NAME = "rail_data.db";
	public String getDatabaseName()
	{
		return DATABASE_NAME;
	}
	
	public static final String ZIP_NAME = "rail_data.zip";
	public String getZipName()
	{
		return ZIP_NAME;
	}
	
	
	public int getZoomThreshold()
	{
		return 12;
	}

}
