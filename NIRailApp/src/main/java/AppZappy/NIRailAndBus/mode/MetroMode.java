package AppZappy.NIRailAndBus.mode;

import java.util.List;

import AppZappy.NIRailAndBus.data.enums.TransportType;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.pathfinding.IPathfindingAlgorithm;
import AppZappy.NIRailAndBus.pathfinding.SQL_Pathfinding_OnSameService;

public class MetroMode implements IProgramMode
{
	private IUIInterface data = null;
	public MetroMode (IUIInterface data)
	{
		this.data = data;
	}
	public MetroMode()
	{
		this(UIInterfaceFactory.getInterface());
	}
	
	public List<Location> reachableLocations(Location location)
	{
		List<Location> locations = null;
		if (location == null)
		{
			locations = data.getAllLocations();
		}
		else
		{
			locations = location.getReachableLocations();
		}
		
		return locations;
	}

	public IPathfindingAlgorithm pathfindingAlgorithm()
	{
		return new SQL_Pathfinding_OnSameService();
	}

	


	
	public String twitterURL()
	{
		//http://idfromuser.org/
		return "http://twitter.com/statuses/user_timeline/155909239.rss";
	}


	/**
	 * Remove the "TranslinkMetro: " text from the start
	 */
	public int twitterNumberCharactersToRemoveFromDescription()
	{
		return 16;
	}
	
	public static final String TWITTER_GUID_START = "http://twitter.com/TranslinkMetro/statuses/";
    public static final int TWITTER_GUID_START_LENGTH = 43;
	public int twitterGUIDStartLength()
	{
		return TWITTER_GUID_START_LENGTH;
	}

	public TransportType getMode()
	{
		return TransportType.Metro;
	}

	public static final String DATABASE_NAME = "metro_data.db";
	public String getDatabaseName()
	{
		return DATABASE_NAME;
	}
	
	public static final String ZIP_NAME = "metro_data.zip";
	public String getZipName()
	{
		return ZIP_NAME;
	}
	

	public int getZoomThreshold()
	{
		return 15;
	}
}
