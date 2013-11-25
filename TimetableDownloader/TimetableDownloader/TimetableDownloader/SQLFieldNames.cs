using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class SQLFieldNames
    {
        public const string TIMETABLES = "timetable";
        public const string TIMETABLE_ID = "_id";
        public const string TIMETABLE_NAME = "name";

        public const string SERVICES = "services";
        public const string SERVICES_ID = "_id";
        public const string SERVICES_NAME = "name";
        public const string SERVICES_SUMMARY = "summary";
        public const string SERVICES_DIRECTION = "direction";
        public const string SERVICES_TIMETABLE_ID = "timetable_id";
        public const string SERVICES_STARTDATE = "start_date";
        public const string SERVICES_ENDDATE = "end_date";
        public const string SERVICES_TYPE = "type";

        public const string ROUTES = "routes";
        public const string ROUTES_ID = "_id";
        public const string ROUTES_SERVICE_ID = "service_id";
        public const string ROUTES_START_DATE = "start_date";
        public const string ROUTES_END_DATE = "end_date";
        public const string ROUTES_START_TIME = "start_time";
        public const string ROUTES_END_TIME = "end_time";
        public const string ROUTES_MON = "mon";
        public const string ROUTES_TUE = "tue";
        public const string ROUTES_WED = "wed";
        public const string ROUTES_THU = "thu";
        public const string ROUTES_FRI = "fri";
        public const string ROUTES_SAT = "sat";
        public const string ROUTES_SUN = "sun";
        

        public const string STOPS = "stops";
        public const string STOPS_ID = "_id";
        public const string STOPS_LOCATION_ID = "location_id";
        public const string STOPS_TIME = "time";
        public const string STOPS_ROUTE_ID = "route_id";
        public const string STOPS_PICKUP = "pickup";
        public const string STOPS_DROPOFF = "dropoff";



        public const string NETWORKS = "networks";
        public const string NETWORKS_ID = "_id";
        public const string NETWORKS_NAME = "name";

        public const string LOCATIONS = "locations";
        public const string LOCATIONS_ID = "_id";
        public const string LOCATIONS_REAL_NAME = "real_name";
        public const string LOCATIONS_LATITUDE = "latitude";
        public const string LOCATIONS_LONGITUDE = "longtitude";
        public const string LOCATIONS_TYPE = "type";

        public const string LOCATION_ALIASES = "location_aliases";
        public const string LOCATION_ALIASES_ID = "_id";
        public const string LOCATION_ALIASES_LOCATION_ID = "location_id";
        public const string LOCATION_ALIASES_NAME = "name";

        public const string TOUCHING_LOCATIONS = "touching_locations";
        public const string TOUCHING_LOCATIONS_ID = "_id";
        public const string TOUCHING_LOCATIONS_LOCATION1_ID = "location1_id";
        public const string TOUCHING_LOCATIONS_LOCATION2_ID = "location2_id";

        public const string SWAP_LOCATIONS = "swap_locations";
        public const string SWAP_LOCATIONS_ID = "_id";
        public const string SWAP_LOCATIONS_LOCATION1_ID = "location1_id";
        public const string SWAP_LOCATIONS_LOCATION2_ID = "location2_id";
    }
}
