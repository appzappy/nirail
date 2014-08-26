package appzappy.nirail.pathfinding;

import java.util.List;

import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.timetable.Timetable;


public interface IPathfindingAlgorithm
{
	public List<List<Journey>> findPaths(Timetable timetable, Location source, Location destination);
}
