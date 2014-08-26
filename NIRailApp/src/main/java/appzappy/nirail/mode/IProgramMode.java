package appzappy.nirail.mode;

import java.util.List;

import appzappy.nirail.data.enums.TransportType;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.pathfinding.IPathfindingAlgorithm;

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
