package AppZappy.NIRailAndBus.mode;

import java.util.List;

import AppZappy.NIRailAndBus.data.enums.TransportType;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.pathfinding.IPathfindingAlgorithm;

public interface IProgramMode
{
	List<Location> reachableLocations (Location locations);
	IPathfindingAlgorithm pathfindingAlgorithm();
	TransportType getMode();
	
	String twitterURL();
	int twitterGUIDStartLength();

	/**
	 * Remove the nirailways: text from the start or translinkmetro
	 */
	int twitterNumberCharactersToRemoveFromDescription();
	
	String getDatabaseName();
	String getZipName();
	
	int getZoomThreshold();
}
