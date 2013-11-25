using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    public class Route : IToXML, IID
    {
        public int ID { get; set; }
        public Route(string Service)
        {
            this.Service = Service;
            this.StartDate = DateTime.MinValue;
            this.EndDate = DateTime.MaxValue;
        }

        public string Service { get; private set; }
        bool[] running = new bool[7];
        
        public bool isRunning(DayOfWeek day)
        {
            return running[(int)day];
        }
        public void setRunning(DayOfWeek day, bool value)
        {
            running[(int)day] = value;
        }

        public string[] AllStops
        {
            get
            {
                HashSet<string> hash = new HashSet<string>();
                foreach (Stop s in stops)
                {
                    hash.Add(s.Name);
                }
                return hash.ToArray();
            }
        }


        public List<Stop> stops = new List<Stop>();

        public void AddStop(string name, string time, bool pickUp, bool dropOff)
        {
            stops.Add(new Stop(name, time, pickUp, dropOff));

            if (stops.Count > 1)
            {
                // at least 2 items in the data set
                int count = stops.Count;

                int last = count - 1;
                int lastlast = count - 2;

                if (stops[lastlast].TimeValue > stops[last].TimeValue)
                {
                    // the later time is smaller than the earlier time
                    // day jump has occured
                    // Add on 24 hours to the time value
                    stops[last].Add24Hours();
                }
            }
        }
        

        public System.Xml.Linq.XElement ToXML()
        {
            System.Xml.Linq.XElement ele = new System.Xml.Linq.XElement("Route");
            ele.Add(new System.Xml.Linq.XAttribute("Service", this.Service));

            System.Xml.Linq.XElement days = new System.Xml.Linq.XElement("RunningDays");
            for (int i = 0; i < 7; i++)
            {
                days.Add(new System.Xml.Linq.XAttribute(((DayOfWeek)i).ToString(), running[i]));
            }

            ele.Add(days);
            foreach (Stop s in stops)
                ele.Add(s.ToXML());
            return ele;
        }

        public DateTime StartDate { get; set; }
        public DateTime EndDate { get; set; }

        public string StartTime
        {
            get 
            { 
                if (stops.Count > 0)
                    return stops[0].Time;
                return "-1";
            }
        }
        public string EndTime
        {
            get
            {
                if (stops.Count > 0)
                    return stops[stops.Count - 1].Time;
                return "-1";
            }
        }
    }
}
