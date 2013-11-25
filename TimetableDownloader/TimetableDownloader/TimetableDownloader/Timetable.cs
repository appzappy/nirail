using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;

namespace TimetableDownloader
{
    public class Timetable : IToXML, IID
    {
        public int ID { get; set; }
        public List<Service> services = new List<Service>();

        public void AddService(Service service)
        {
            services.Add(service);
        }

        public string[] AllStops
        {
            get
            {
                HashSet<string> stops = new HashSet<string>();
                foreach (Service s in services)
                {
                    stops.UnionWith(s.AllStops);
                }
                return stops.ToArray();
            }
        }

        public List<Pair<string, string>> LinkingLocations
        {
            get
            {
                List<Pair<string, string>> output = new List<Pair<string, string>>();
                HashSet<string> already_contained = new HashSet<string>();

                foreach (Service s in services)
                {
                    foreach (Route r in s.routes)
                    {
                        List<Stop> stops = r.stops;
                        for (int i = 1; i < stops.Count; i++)
                        {
                            Stop first = stops[i - 1];
                            Stop second = stops[i];

                            string key1 = first.Name + second.Name;
                            string key2 = second.Name + first.Name;

                            if (!already_contained.Contains(key1) && !already_contained.Contains(key2))
                            {
                                already_contained.Add(key1);
                                already_contained.Add(key2);
                                output.Add(new Pair<string, string>(first.Name, second.Name));
                            }
                        }
                    }
                }

                return output;
            }
        }


        public System.Xml.Linq.XElement ToXML()
        {
            System.Xml.Linq.XElement ele = new System.Xml.Linq.XElement("Timetable");
            foreach (Service s in services)
                ele.Add(s.ToXML());
            return ele;
        }

    }
}
