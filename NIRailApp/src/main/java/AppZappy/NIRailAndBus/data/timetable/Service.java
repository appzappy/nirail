package AppZappy.NIRailAndBus.data.timetable;

import java.util.ArrayList;
import java.util.List;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import AppZappy.NIRailAndBus.data.collections.DataPointer;
import AppZappy.NIRailAndBus.data.db.SQLFieldNames;
import AppZappy.NIRailAndBus.data.db.SQLiteHelper;
import AppZappy.NIRailAndBus.data.db.SQLiteManager;
import AppZappy.NIRailAndBus.data.enums.Direction;
import AppZappy.NIRailAndBus.data.enums.TransportType;
import AppZappy.NIRailAndBus.data.model.IID;
import AppZappy.NIRailAndBus.data.model.Route;


/**
 * Service class contains all the routes (an individual train/bus) that follows a common path
 */
public class Service implements IID
{
	private int _id = 0;
	/**
	 * Get the id of this route
	 */
	public int get_id()
	{
		return _id;
	}
	
	/**
	 * Load a service object from the database
	 * @param service_id The id of the service to load
	 * @return
	 */
	public static DataPointer<Service> from_SQL(int service_id)
	{
		SQLiteDatabase db =  SQLiteManager.getDb();
		
		String where = SQLFieldNames.SERVICES_ID + " = '"+service_id+"'";
		Cursor data = db.query(SQLFieldNames.SERVICES, null, where, null, null, null, null);
		
		data.moveToNext();
		
		int pos_name = data.getColumnIndex(SQLFieldNames.SERVICES_NAME);
		int pos_start = data.getColumnIndex(SQLFieldNames.SERVICES_STARTDATE);
		int pos_end = data.getColumnIndex(SQLFieldNames.SERVICES_ENDDATE);
		int pos_summary = data.getColumnIndex(SQLFieldNames.SERVICES_SUMMARY);
		int pos_direction = data.getColumnIndex(SQLFieldNames.SERVICES_DIRECTION);
		int pos_type = data.getColumnIndex(SQLFieldNames.SERVICES_TYPE);
		
		Service service = new Service(null, null, null);
		service._id = service_id;
		service._ServiceName = data.getString(pos_name);
		service._RouteSummary = data.getString(pos_summary);
		service._Direction = Direction.valueOf(data.getString(pos_direction));
		
		service._start_date = data.getInt(pos_start);
		service._end_date = data.getInt(pos_end);

		service._Routes = SQLiteHelper.get_Routes(db, service._id);
		service._Type = TransportType.valueOf(data.getString(pos_type));
		
		
		data.close();
		db.close();
		
		DataPointer.add_Object(Service.class, service);
		return new DataPointer<Service>(Service.class, service._id);
	}
	
	private boolean _fullyLoaded = false;

	/**
	 * Load the contents of this service. (ALL ROUTES AND ALL STOPS)
	 */
	public void loadServiceContents()
	{
		// load all the contents of this service
		if (!this._fullyLoaded)
		{ 
			// load all of the service
			SQLiteDatabase db = SQLiteManager.getDb();
			Route.from_SQL(db, this.get_id());
			db.close();
			this._fullyLoaded = true;
		}
	}
	
	private int _start_date = 0;
	/**
	 * Get the start date value for this route
	 * @return
	 */
	public int get_start_date()
	{
		return _start_date;
	}
	
	private int _end_date = 0;
	/**
	 * Get the end date value for this route
	 * @return
	 */
	public int get_end_date()
	{
		return _end_date;
	}
	
	private List<DataPointer<Route>> _Routes = new ArrayList<DataPointer<Route>>();

	/**
	 * Count the number of routes in this service
	 * @return The number of routes in this service
	 */
	public int countRoutes()
	{
		return _Routes.size();
	}

	/**
	 * Get a route from the service
	 * @param index The index of the route to get
	 * @return A route from this service
	 */
	public DataPointer<Route> getRoute(int index)
	{
		return _Routes.get(index);
	}

	private String _ServiceName = null;
	private String _RouteSummary = null;
	private Direction _Direction = null;
	private TransportType _Type = null;
	
	/**
	 * The type of this service
	 * @return Either Train or Bus
	 */
	public TransportType getType()
	{
		return _Type;
	}

	/**
	 * Get this services name
	 * @return The service name
	 */
	public String getServiceName()
	{
		return _ServiceName;
	}

	/**
	 * Get the route summary
	 * @return The route summary
	 */
	public String getRouteSummary()
	{
		return _RouteSummary;
	}

	/**
	 * Get the direction of this service
	 * @return The direction
	 */
	public Direction getDirection()
	{
		return _Direction;
	}

	/**
	 * Create a new Service object
	 * @param serviceName The service name
	 * @param routeSummary The route summary
	 * @param direction The direction of the service
	 */
	private Service(String serviceName, String routeSummary, Direction direction)
	{
		this._ServiceName = serviceName;
		this._RouteSummary = routeSummary;
		this._Direction = direction;
	}


}
