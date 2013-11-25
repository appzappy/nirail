using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class Location : IToXML
    {
        public string RealName { get; private set; }
        public List<string> aliases = new List<string>();

        public string Latitude { get; set; }
        public string Longtitude { get; set; }

        public TransportType LocationType { get; private set; }

        public bool isAName(string name)
        {
            return aliases.Contains(name) || RealName == name;
        }


        public Location(string realName, string[] aliases, string latitude, string longtitude, TransportType type)
        {
            this.RealName = realName;
            this.Latitude = latitude;
            this.Longtitude = longtitude;
            this.aliases.AddRange(aliases);
            this.LocationType = type;
        }

        public System.Xml.Linq.XElement ToXML()
        {
            System.Xml.Linq.XElement ele = new System.Xml.Linq.XElement("Location");
            ele.Add(new System.Xml.Linq.XAttribute("RealName", this.RealName));
            ele.Add(new System.Xml.Linq.XAttribute("Latitude", this.Latitude));
            ele.Add(new System.Xml.Linq.XAttribute("Longtitude", this.Longtitude));
            ele.Add(new System.Xml.Linq.XAttribute("LocationType", this.LocationType));

            foreach (string s in aliases)
            {
                ele.Add(new System.Xml.Linq.XElement("Alias", s));
            }
            return ele;
        }

        public Location(System.Xml.Linq.XElement data)
        {
            this.RealName = data.Attribute("RealName").Value;
            this.Latitude = data.Attribute("Latitude").Value;
            this.Longtitude = data.Attribute("Longtitude").Value;
            this.LocationType = (TransportType)Enum.Parse(typeof(TransportType), data.Attribute("LocationType").Value);

            foreach (var x in data.Elements("Alias"))
            {
                this.aliases.Add(x.Value);
            }
        }
    }
}
