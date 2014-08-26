package appzappy.nirail.data.db;

import appzappy.nirail.data.enums.DayOfWeek;

/**
 * The fields names used in the SQLite Database
 */
public class SQLFieldNames
{
	public static final String TIMETABLES = "timetable";
    public static final String TIMETABLE_ID = "_id";
    public static final String TIMETABLE_NAME = "name";

    public static final String SERVICES = "services";
    public static final String SERVICES_ID = "_id";
    public static final String SERVICES_NAME = "name";
    public static final String SERVICES_SUMMARY = "summary";
    public static final String SERVICES_DIRECTION = "direction";
    public static final String SERVICES_TIMETABLE_ID = "timetable_id";
    public static final String SERVICES_STARTDATE = "start_date";
    public static final String SERVICES_ENDDATE = "end_date";
    public static final String SERVICES_TYPE = "type";
    
    public static final String ROUTES = "routes";
    public static final String ROUTES_ID = "_id";
    public static final String ROUTES_SERVICE_ID = "service_id";
    public static final String ROUTES_START_DATE = "start_date";
    public static final String ROUTES_END_DATE = "end_date";
    public static final String ROUTES_START_TIME = "start_time";
    public static final String ROUTES_END_TIME = "end_time";
    public static final String ROUTES_MON = "mon";
    public static final String ROUTES_TUE = "tue";
    public static final String ROUTES_WED = "wed";
    public static final String ROUTES_THU = "thu";
    public static final String ROUTES_FRI = "fri";
    public static final String ROUTES_SAT = "sat";
    public static final String ROUTES_SUN = "sun";
    
    public static String getDayString(DayOfWeek day)
    {
    	switch(day)
    	{
    		case Monday : return ROUTES_MON;
    		case Tuesday: return ROUTES_TUE;
    		case Wednesday: return ROUTES_WED;
    		case Thursday : return ROUTES_THU;
    		case Friday : return ROUTES_FRI;
    		case Saturday: return ROUTES_SAT;
    		case Sunday: return ROUTES_SUN;
    		default:
    			throw new RuntimeException("Day value not valid. Day: " + day.toString());
    	}
    }
    

    public static final String STOPS = "stops";
    public static final String STOPS_ID = "_id";
    public static final String STOPS_LOCATION_ID = "location_id";
    public static final String STOPS_TIME = "time";
    public static final String STOPS_ROUTE_ID = "route_id";
    public static final String STOPS_PICKUP = "pickup";
    public static final String STOPS_DROPOFF = "dropoff";



    public static final String NETWORKS = "networks";
    public static final String NETWORKS_ID = "_id";
    public static final String NETWORKS_NAME = "name";

    public static final String LOCATIONS = "locations";
    public static final String LOCATIONS_ID = "_id";
    public static final String LOCATIONS_REAL_NAME = "real_name";
    public static final String LOCATIONS_LATITUDE = "latitude";
    public static final String LOCATIONS_LONGITUDE = "longtitude";
    public static final String LOCATIONS_TYPE = "type";

    public static final String LOCATION_ALIASES = "location_aliases";
    public static final String LOCATION_ALIASES_ID = "_id";
    public static final String LOCATION_ALIASES_LOCATION_ID = "location_id";
    public static final String LOCATION_ALIASES_NAME = "name";
    
    public static final String TOUCHING_LOCATION = "touching_locations";
    public static final String TOUCHING_LOCATION_ID = "_id";
    public static final String TOUCHING_LOCATION_LOCATION1_ID = "location1_id";
    public static final String TOUCHING_LOCATION_LOCATION2_ID = "location2_id";

    
    public static final String SWAP_LOCATIONS = "swap_locations";
    public static final String SWAP_LOCATIONS_ID = "_id";
    public static final String SWAP_LOCATIONS_LOCATION1_ID = "location1_id";
    public static final String SWAP_LOCATIONS_LOCATION2_ID = "location2_id";
}
