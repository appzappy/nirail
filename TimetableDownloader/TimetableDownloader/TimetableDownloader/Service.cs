using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    public class Service : IToXML, IID
    {
        public int ID { get; set; }
        public List<Route> routes = new List<Route>();

        public TransportType Type { get; set; }
        

        public string Name { get; private set; }
        public string Summary { get; private set; }
        public string Direction { get; private set; }
        public DateTime StartDate { get; set; }
        public DateTime EndDate { get; set; }

        public void AddRoute(Route route)
        {
            routes.Add(route);
        }
        public string[] AllStops
        {
            get
            {
                HashSet<string> stops = new HashSet<string>();
                foreach (Route r in routes)
                {
                    stops.UnionWith(r.AllStops);
                }
                return stops.ToArray();
            }
        }

        public Service(string name, string routeSummary, string direction)
        {
            this.Name = name;
            this.Summary = routeSummary;
            this.Direction = direction;
            this.StartDate = DateTime.MinValue;
            this.EndDate = DateTime.MaxValue;
        }

        public int NumberRoutes { get { return routes.Count; } }


        public System.Xml.Linq.XElement ToXML()
        {
            System.Xml.Linq.XElement ele = new System.Xml.Linq.XElement("Service");
            ele.Add(new System.Xml.Linq.XAttribute("ServiceName", this.Name));
            ele.Add(new System.Xml.Linq.XAttribute("RouteSummary", this.Summary));
            ele.Add(new System.Xml.Linq.XAttribute("Direction", this.Direction));
            foreach (Route r in routes)
                ele.Add(r.ToXML());
            return ele;
        }


    }
}
