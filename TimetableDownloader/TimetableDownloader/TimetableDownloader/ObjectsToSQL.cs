using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.IO;
using System.Data.SQLite;

namespace TimetableDownloader
{
    class ObjectsToSQL
    {
        public static void ClearNetwork(FileInfo sqlDB)
        {
            List<string> network_tables = new List<string>();
            network_tables.Add(SQLFieldNames.LOCATION_ALIASES);
            network_tables.Add(SQLFieldNames.LOCATIONS);
            network_tables.Add(SQLFieldNames.NETWORKS);
            network_tables.Add(SQLFieldNames.TOUCHING_LOCATIONS);
            network_tables.Add(SQLFieldNames.SWAP_LOCATIONS);
            
            SQLiteDatabase db = new SQLiteDatabase(sqlDB);
            db.ClearTable(network_tables);
        }


        public static void LoadNetworkDependants(FileInfo sqlDB, Network network, ITransportInterface data)
        {
            Dictionary<string, int> location_aliases = getNetworkAliases(sqlDB);
            List<LocationPair> touchingLocations = data.TouchingLocationsSource.getTouchingLocations(location_aliases); 
            List<LocationPair> swappingLocations = data.SwappingLocationsSource.getSwappingLocations(location_aliases); 
            
            DateTime startLoad = DateTime.Now;
            Dictionary<string, string> dic;
            //SQLiteDatabase db = new SQLiteDatabase(sqlDB);


            using (SQLiteConnection sql_connection = SQLiteDatabase.GetConnection(sqlDB))
            {
                sql_connection.Open();
                using (SQLiteTransaction sql_Transaction = sql_connection.BeginTransaction())
                {


                    // add the touching locations data

                    foreach (LocationPair tl in touchingLocations)
                    {
                        dic = new Dictionary<string, string>();
                        dic.Add(SQLFieldNames.TOUCHING_LOCATIONS_LOCATION1_ID, tl.Point1.ToString());
                        dic.Add(SQLFieldNames.TOUCHING_LOCATIONS_LOCATION2_ID, tl.Point2.ToString());

                        int tl_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.TOUCHING_LOCATIONS, dic);
                    }


                    // add the swapping locations data

                    foreach (LocationPair tl in swappingLocations)
                    {
                        dic = new Dictionary<string, string>();
                        dic.Add(SQLFieldNames.SWAP_LOCATIONS_LOCATION1_ID, tl.Point1.ToString());
                        dic.Add(SQLFieldNames.SWAP_LOCATIONS_LOCATION2_ID, tl.Point2.ToString());

                        int tl_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.SWAP_LOCATIONS, dic);
                    }


                    sql_Transaction.Commit();
                }

            }


            DateTime finishLoad = DateTime.Now;
            TimeSpan diff = finishLoad - startLoad;
            Console.WriteLine("Network Dependant Data Entered in {0}s{1}ms", (int)diff.TotalSeconds, diff.Milliseconds);

        }

        public static void LoadNetwork(FileInfo sqlDB, Network network)
        {
            DateTime startLoad = DateTime.Now;
            Dictionary<string, string> dic;
            //SQLiteDatabase db = new SQLiteDatabase(sqlDB);


            using (SQLiteConnection sql_connection = SQLiteDatabase.GetConnection(sqlDB))
            {
                sql_connection.Open();
                using (SQLiteTransaction sql_Transaction = sql_connection.BeginTransaction())
                {

                    // insert the network
                    dic = new Dictionary<string, string>();
                    dic.Add(SQLFieldNames.NETWORKS_NAME, network.Name);
                    int network_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.NETWORKS, dic);



                    
                    // insert the locations
                    foreach (Location loc in network.Locations)
                    {
                        dic = new Dictionary<string, string>();
                        dic.Add(SQLFieldNames.LOCATIONS_LATITUDE, loc.Latitude);
                        dic.Add(SQLFieldNames.LOCATIONS_LONGITUDE, loc.Longtitude);
                        dic.Add(SQLFieldNames.LOCATIONS_REAL_NAME, loc.RealName);
                        dic.Add(SQLFieldNames.LOCATIONS_TYPE, loc.LocationType.ToString());

                        int location_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.LOCATIONS, dic);

                        // build up the data to be inserted into the aliases
                        dic = new Dictionary<string, string>();
                        dic.Add(SQLFieldNames.LOCATION_ALIASES_LOCATION_ID, location_id.ToString());
                        dic.Add(SQLFieldNames.LOCATION_ALIASES_NAME, loc.RealName);
                        // uncomment to add realname to the aliases table
                        int location_alias_id_real_name = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.LOCATION_ALIASES, dic);
                        foreach (string alias in loc.aliases)
                        {
                            dic = new Dictionary<string, string>();
                            dic.Add(SQLFieldNames.LOCATION_ALIASES_LOCATION_ID, location_id.ToString());
                            dic.Add(SQLFieldNames.LOCATION_ALIASES_NAME, alias);

                            int location_alias_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.LOCATION_ALIASES, dic);

                        }
                    }


                    sql_Transaction.Commit();
                }

            }


            DateTime finishLoad = DateTime.Now;
            TimeSpan diff = finishLoad - startLoad;
            Console.WriteLine("Network Data Entered in {0}s{1}ms", (int)diff.TotalSeconds, diff.Milliseconds);


        }
        public static void ClearTimetable(FileInfo sqlDB)
        {
            List<string> network_tables = new List<string>();
            network_tables.Add(SQLFieldNames.TIMETABLES);
            network_tables.Add(SQLFieldNames.SERVICES);
            network_tables.Add(SQLFieldNames.ROUTES);
            network_tables.Add(SQLFieldNames.STOPS);

            SQLiteDatabase db = new SQLiteDatabase(sqlDB);
            db.ClearTable(network_tables);
        }

        public static Dictionary<string, int> getNetworkAliases(FileInfo sqlDB)
        {
            SQLiteDatabase db = new SQLiteDatabase(sqlDB);
            Dictionary<string, int> aliases = new Dictionary<string, int>();

            DataTable table = db.GetDataTable("SELECT * FROM " + SQLFieldNames.LOCATION_ALIASES);
            foreach (DataRow row in table.Rows)
            {
                string name = row[SQLFieldNames.LOCATION_ALIASES_NAME].ToString();
                string id = row[SQLFieldNames.LOCATION_ALIASES_LOCATION_ID].ToString();
                int id_value = int.Parse(id);
                if (!aliases.ContainsKey(name))
                {
                    aliases.Add(name, id_value);
                }
            }
            return aliases;
        }

       

        public static bool LoadTimetable(FileInfo sqlDB, Timetable timetable)
        {
            DateTime startLoad = DateTime.Now;


            // LOAD THE NETWORK
            Dictionary<string, int> aliases = getNetworkAliases(sqlDB);


            using (SQLiteConnection sql_connection = SQLiteDatabase.GetConnection(sqlDB))
            {
                sql_connection.Open();
                using (SQLiteTransaction sql_Transaction = sql_connection.BeginTransaction())
                {
                    Dictionary<string, string> dic;

                    // insert the timetable
                    dic = new Dictionary<string, string>();
                    dic.Add(SQLFieldNames.TIMETABLE_NAME, "NULL");
                    int timetable_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.TIMETABLES, dic);
                    timetable.ID = timetable_id;


                    // insert the services
                    foreach (Service s in timetable.services)
                    {
                        // get the date for this
                        int startdate = 0;
                        if (!s.StartDate.Equals(DateTime.MinValue))
                            startdate = GetSQLDate(s.StartDate);

                        int enddate = 0;
                        if (!s.EndDate.Equals(DateTime.MaxValue))
                            enddate = GetSQLDate(s.EndDate);

                        dic = new Dictionary<string, string>();
                        dic.Add(SQLFieldNames.SERVICES_NAME, s.Name);
                        dic.Add(SQLFieldNames.SERVICES_SUMMARY, s.Summary);
                        dic.Add(SQLFieldNames.SERVICES_TIMETABLE_ID, timetable_id.ToString());
                        dic.Add(SQLFieldNames.SERVICES_DIRECTION, s.Direction);
                        dic.Add(SQLFieldNames.SERVICES_ENDDATE, enddate.ToString());
                        dic.Add(SQLFieldNames.SERVICES_STARTDATE, startdate.ToString());
                        dic.Add(SQLFieldNames.SERVICES_TYPE, s.Type.ToString());

                        int service_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.SERVICES, dic);
                        s.ID = service_id;

                        // add the routes
                        foreach (Route route in s.routes)
                        {
                            int start_date = 0;
                            if (!route.StartDate.Equals(DateTime.MinValue))
                                start_date = GetSQLDate(route.StartDate);

                            int end_date = 0;
                            if (!route.EndDate.Equals(DateTime.MaxValue))
                                end_date = GetSQLDate(route.EndDate);

                            dic = new Dictionary<string, string>();
                            dic.Add(SQLFieldNames.ROUTES_SERVICE_ID, service_id.ToString());
                            dic.Add(SQLFieldNames.ROUTES_START_DATE, start_date.ToString());
                            dic.Add(SQLFieldNames.ROUTES_END_DATE, end_date.ToString());
                            dic.Add(SQLFieldNames.ROUTES_START_TIME, route.StartTime);
                            dic.Add(SQLFieldNames.ROUTES_END_TIME, route.EndTime);
                            dic.Add(SQLFieldNames.ROUTES_MON, BoolToInt(route.isRunning(DayOfWeek.Monday)).ToString());
                            dic.Add(SQLFieldNames.ROUTES_TUE, BoolToInt(route.isRunning(DayOfWeek.Tuesday)).ToString());
                            dic.Add(SQLFieldNames.ROUTES_WED, BoolToInt(route.isRunning(DayOfWeek.Wednesday)).ToString());
                            dic.Add(SQLFieldNames.ROUTES_THU, BoolToInt(route.isRunning(DayOfWeek.Thursday)).ToString());
                            dic.Add(SQLFieldNames.ROUTES_FRI, BoolToInt(route.isRunning(DayOfWeek.Friday)).ToString());
                            dic.Add(SQLFieldNames.ROUTES_SAT, BoolToInt(route.isRunning(DayOfWeek.Saturday)).ToString());
                            dic.Add(SQLFieldNames.ROUTES_SUN, BoolToInt(route.isRunning(DayOfWeek.Sunday)).ToString());


                            int route_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.ROUTES, dic);

                            // add the stops
                            foreach (Stop stop in route.stops)
                            {
                                int location_id = aliases[stop.Name];

                                dic = new Dictionary<string, string>();
                                dic.Add(SQLFieldNames.STOPS_ROUTE_ID, route_id.ToString());
                                dic.Add(SQLFieldNames.STOPS_LOCATION_ID, location_id.ToString());
                                dic.Add(SQLFieldNames.STOPS_TIME, stop.TimeValue.ToString());
                                dic.Add(SQLFieldNames.STOPS_DROPOFF, BoolToInt(stop.DropOff).ToString());
                                dic.Add(SQLFieldNames.STOPS_PICKUP, BoolToInt(stop.PickUp).ToString());

                                int stop_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.STOPS, dic);
                                stop.ID = stop_id;
                            }
                        }
                    }

                    sql_Transaction.Commit();
                }

            }

            DateTime finishLoad = DateTime.Now;
            TimeSpan diff = finishLoad - startLoad;
            Console.WriteLine("Timetable Data Entered in {0}s{1}ms", (int)diff.TotalSeconds, diff.Milliseconds);

            return true;
        }

        public static int BoolToInt(bool value)
        {
            if (value)
                return 1;
            else
                return 0;
        }

        //public static bool LoadTimetable2(FileInfo sqlDB, Timetable timetable)
        //{
        //    DateTime startLoad = DateTime.Now;


        //    // LOAD THE NETWORK
        //    SQLiteDatabase db = new SQLiteDatabase(sqlDB);
        //    Dictionary<string, int> aliases = new Dictionary<string, int>();

        //    DataTable table = db.GetDataTable("SELECT * FROM " + SQLFieldNames.LOCATION_ALIASES);
        //    foreach (DataRow row in table.Rows)
        //    {
        //        string name = row[SQLFieldNames.LOCATION_ALIASES_NAME].ToString();
        //        string id = row[SQLFieldNames.LOCATION_ALIASES_LOCATION_ID].ToString();
        //        int id_value = int.Parse(id);
        //        aliases.Add(name, id_value);
        //    }


        //    using (SQLiteConnection sql_connection = SQLiteDatabase.GetConnection(sqlDB))
        //    {
        //        sql_connection.Open();
        //        using (SQLiteTransaction sql_Transaction = sql_connection.BeginTransaction())
        //        {
        //            Dictionary<string, string> dic;

        //            // insert the timetable
        //            dic = new Dictionary<string, string>();
        //            dic.Add(SQLFieldNames.TIMETABLE_NAME, "NULL");
        //            int timetable_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.TIMETABLES, dic);



        //            // insert the services
        //            List<KeyValuePair<int, Route>> routes = new List<KeyValuePair<int, Route>>();
        //            foreach (Service s in timetable.services)
        //            {
        //                // get the date for this
        //                int startdate = 0;
        //                if (!s.StartDate.Equals(DateTime.MinValue))
        //                    startdate = GetSQLDate(s.StartDate);

        //                int enddate = 0;
        //                if (!s.EndDate.Equals(DateTime.MaxValue))
        //                    enddate = GetSQLDate(s.EndDate);

        //                dic = new Dictionary<string, string>();
        //                dic.Add(SQLFieldNames.SERVICES_NAME, s.Name);
        //                dic.Add(SQLFieldNames.SERVICES_SUMMARY, s.Summary);
        //                dic.Add(SQLFieldNames.SERVICES_TIMETABLE_ID, timetable_id.ToString());
        //                dic.Add(SQLFieldNames.SERVICES_DIRECTION, s.Direction);
        //                dic.Add(SQLFieldNames.SERVICES_ENDDATE, enddate.ToString());
        //                dic.Add(SQLFieldNames.SERVICES_STARTDATE, startdate.ToString());

        //                int service_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.SERVICES, dic);
        //                foreach (Route route in s.routes)
        //                {
        //                    routes.Add(new KeyValuePair<int, Route>(service_id, route));
        //                }
        //            }


        //            // insert the routes
        //            List<KeyValuePair<int, Stop>> stops = new List<KeyValuePair<int, Stop>>();
        //            foreach (KeyValuePair<int, Route> kvp_route in routes)
        //            {
        //                int sID = kvp_route.Key;
        //                Route route = kvp_route.Value;


        //                int start_date = 0;
        //                if (!route.StartDate.Equals(DateTime.MinValue))
        //                    start_date = GetSQLDate(route.StartDate);

        //                int end_date = 0;
        //                if (!route.EndDate.Equals(DateTime.MaxValue))
        //                    end_date = GetSQLDate(route.EndDate);

        //                dic = new Dictionary<string, string>();
        //                dic.Add(SQLFieldNames.ROUTES_SERVICE_ID, sID.ToString());
        //                dic.Add(SQLFieldNames.ROUTES_START_DATE, start_date.ToString());
        //                dic.Add(SQLFieldNames.ROUTES_END_DATE, end_date.ToString());
        //                dic.Add(SQLFieldNames.ROUTES_MON, route.isRunning(DayOfWeek.Monday).ToString());
        //                dic.Add(SQLFieldNames.ROUTES_TUE, route.isRunning(DayOfWeek.Tuesday).ToString());
        //                dic.Add(SQLFieldNames.ROUTES_WED, route.isRunning(DayOfWeek.Wednesday).ToString());
        //                dic.Add(SQLFieldNames.ROUTES_THU, route.isRunning(DayOfWeek.Thursday).ToString());
        //                dic.Add(SQLFieldNames.ROUTES_FRI, route.isRunning(DayOfWeek.Friday).ToString());
        //                dic.Add(SQLFieldNames.ROUTES_SAT, route.isRunning(DayOfWeek.Saturday).ToString());
        //                dic.Add(SQLFieldNames.ROUTES_SUN, route.isRunning(DayOfWeek.Sunday).ToString());


        //                int route_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.ROUTES, dic);

        //                foreach (Stop s in route.stops)
        //                {
        //                    stops.Add(new KeyValuePair<int, Stop>(route_id, s));
        //                }
        //            }




        //            // add stops
        //            List<Dictionary<string, string>> stop_data = new List<Dictionary<string, string>>();
        //            foreach (KeyValuePair<int, Stop> kvp_stops in stops)
        //            {
        //                int rID = kvp_stops.Key;
        //                Stop stop = kvp_stops.Value;

        //                int location_id = aliases[stop.Name];

        //                dic = new Dictionary<string, string>();
        //                dic.Add(SQLFieldNames.STOPS_ROUTE_ID, rID.ToString());
        //                dic.Add(SQLFieldNames.STOPS_LOCATION_ID, location_id.ToString());
        //                dic.Add(SQLFieldNames.STOPS_TIME, stop.TimeValue.ToString());
        //                dic.Add(SQLFieldNames.STOPS_DROPOFF, stop.DropOff.ToString());
        //                dic.Add(SQLFieldNames.STOPS_PICKUP, stop.PickUp.ToString());

        //                int stop_id = SQLiteDatabase.Insert(sql_connection, SQLFieldNames.STOPS, dic);

        //                //stop_data.Add(dic);
        //            }
        //            // use a bulk insert for these 10k items
        //            // db.Insert(SQLFieldNames.STOPS, stop_data);



        //            sql_Transaction.Commit();
        //        }

        //    }

        //    DateTime finishLoad = DateTime.Now;
        //    TimeSpan diff = finishLoad - startLoad;
        //    Console.WriteLine("Timetable Data Entered in {0}s{1}ms", (int)diff.TotalSeconds, diff.Milliseconds);

        //    return true;
        //}

        public static int GetSQLDate(DateTime date)
        {
            return date.Year * 10000 + date.Month * 100 + date.Day;
        }
        public static DateTime GetDateTime(int date)
        {
            int day = date % 100;
            date /= 100;
            int month = date % 100;
            date /= 100;
            int year = date;
            return new DateTime(year, month, day);
        }
    }
}
