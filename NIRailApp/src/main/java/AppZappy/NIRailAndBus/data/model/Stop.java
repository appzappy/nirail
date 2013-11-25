package AppZappy.NIRailAndBus.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.collections.DataPointer;
import AppZappy.NIRailAndBus.data.db.SQLFieldNames;
import AppZappy.NIRailAndBus.data.db.SQLiteManager;
import AppZappy.NIRailAndBus.util.timing.TimeFormatter;
import AppZappy.NIRailAndBus.util.timing.Timer;



/**
 * A stop on a single route
 *
 */
public class Stop implements IID
{
	private int _id = 0;
	public int get_id()
	{
		return _id;
	}


	public static List<DataPointer<Stop>> from_SQL_Route(int route_id)
	{
		String where = SQLFieldNames.STOPS_ROUTE_ID + "=" + route_id;
		
		SQLiteDatabase db = SQLiteManager.getDb();
		Map<Integer, List<Stop>> data = from_SQL(db, where);
		
		List<Stop> set = data.get(route_id);
		
		List<DataPointer<Stop>> out = new ArrayList<DataPointer<Stop>>();
		for(Stop s: set)
		{
			out.add(new DataPointer<Stop>(Stop.class, s._id));
		}
		
		return out;
	}
	
	
	public static Map<Integer, List<Stop>> from_SQL(SQLiteDatabase db, Set<Integer> route_ids)
	{
		if (route_ids.size() == 0)
			return new HashMap<Integer, List<Stop>>();
		
		final int timer_id = Timer.start_timer();
		
		// make the cursor in where string
		int capacity = 100 + 8*route_ids.size();
		StringBuilder sb = new StringBuilder(capacity);
		
		sb.append(SQLFieldNames.STOPS_ROUTE_ID);
		sb.append(" IN (");
		
		Iterator<Integer> iterator = route_ids.iterator();
		sb.append(iterator.next());
		
		while (iterator.hasNext())
		{
			sb.append(", ");
			Integer value = iterator.next();
			sb.append(value);
		}
		
		sb.append(')');
		
				
		final String where = sb.toString();
		
		
		final Map<Integer, List<Stop>> out = from_SQL(db, where);
		
		Timer.report_timer("Loading stops for " + route_ids.size() + " routes.", timer_id);
		Timer.stop_timer(timer_id);
		return out;
	}
	
	public static Map<Integer, List<Stop>> from_SQL(SQLiteDatabase db, int min_route_id, int max_route_id)
	{
		String where = SQLFieldNames.STOPS_ROUTE_ID + ">='"+min_route_id+"' AND " + SQLFieldNames.STOPS_ROUTE_ID + "<='"+max_route_id+"'";
		Map<Integer, List<Stop>> items = from_SQL(db, where);
		return items;
	}
	
	public static DataPointer<Stop> from_SQL(int stop_id)
	{
		SQLiteDatabase db =  SQLiteManager.getDb();
		String where = SQLFieldNames.STOPS_ID + " = '" + stop_id + "'";
		from_SQL(db, where);
		return new DataPointer<Stop>(Stop.class, stop_id);
	}
	
	private static Map<Integer, List<Stop>> from_SQL(SQLiteDatabase db, String where)
	{
		Map<Integer, List<Stop>> out = new HashMap<Integer, List<Stop>>();
			
		Cursor data = db.query(SQLFieldNames.STOPS, null, where, null, null, null, null);
		
		final int pos_id = data.getColumnIndex(SQLFieldNames.STOPS_ID);
		final int pos_location_id = data.getColumnIndex(SQLFieldNames.STOPS_LOCATION_ID);
		final int pos_time = data.getColumnIndex(SQLFieldNames.STOPS_TIME);
		final int pos_pickup = data.getColumnIndex(SQLFieldNames.STOPS_PICKUP);
		final int pos_dropoff = data.getColumnIndex(SQLFieldNames.STOPS_DROPOFF);
		final int pos_route_id = data.getColumnIndex(SQLFieldNames.STOPS_ROUTE_ID);
		
		while(data.moveToNext())
		{
			final int id = data.getInt(pos_id);
			final int location_id = data.getInt(pos_location_id);
			final int route_id = data.getInt(pos_route_id);
			final short time = data.getShort(pos_time);
			final short properTime = TimeFormatter.timeFromPlainTime(time);
			
			final boolean pickup = data.getInt(pos_pickup) == 1;
			final boolean dropoff = data.getInt(pos_dropoff) == 1;
			
			Stop stop = new Stop(location_id, properTime, pickup, dropoff);
			stop._id = id;
			
			if (!out.containsKey(route_id))
			{
				out.put(route_id, new ArrayList<Stop>());
			}
			out.get(route_id).add(stop);
		}
		data.close();
		
		
		// check for stop objects already in memory
		for (Integer i : out.keySet())
		{
			List<Stop> stops = out.get(i);
			List<Stop> out_stops = DataPointer.add_Object(Stop.class, stops);
			
			out.put(i, out_stops);
		}
		return out;
	}
	
	
	private boolean _pickUp = false;

	/**
	 * Is this stop a pickup stop?
	 * @return True if is pickup
	 */
	public boolean isPickUp()
	{
		return _pickUp;
	}

	private boolean _dropOff = false;

	/**
	 * Is this stop a drop off stop?
	 * @return True if is drop off
	 */
	public boolean isDropOff()
	{
		return _dropOff;
	}

	/**
	 * Is this a normal stop? i.e Is it both a pickup AND dropoff?
	 * @return True if can both pickup and drop off
	 */
	public boolean isNormal()
	{
		return _dropOff && _pickUp;
	}

	private Location _location;

	/**
	 * Get the location this stop is at
	 * @return The location this stop is positioned
	 */
	public Location getLocation()
	{
		return _location;
	}

	private short _time = 0;

	/**
	 * Get the time for this stop
	 * @return Time of stop
	 */
	public short getTime()
	{
		return _time;
	}
	
	/**
	 * Get the time of this stop as formated
	 * Such as: 0000, 0015, 0512, 1740 etc
	 * @return Formated time. 4 characters long
	 */
	public String getFormatedTime()
	{
		return TimeFormatter.formattedStringFromTime(this._time);
	}

	private Stop (int location_id, short time, boolean pickUp, boolean dropOff)
	{
		_location = LoadData.getNetwork().get(location_id);
		_time = time;
		_pickUp = pickUp;
		_dropOff = dropOff;
	}
	
	@Override
	public String toString()
	{
		String out = "Stop: ID="+_id + " ";
		out += _location.getRealName() + " ";
		out += getFormatedTime() + " (" + _time + ")";
		return out;
	}
}
