package AppZappy.NIRailAndBus.pathfinding;

import java.util.List;

import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.data.timetable.Timetable;


public interface IPathfindingAlgorithm
{
	public List<List<Journey>> findPaths(Timetable timetable, Location source, Location destination);
}
