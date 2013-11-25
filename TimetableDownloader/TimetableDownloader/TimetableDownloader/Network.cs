using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class Network : IToXML
    {
        public string Name { get; set; }
        public List<Location> Locations = new List<Location>();

        public void AddLocation(Location location)
        {
            Locations.Add(location);
        }

        public bool ContainsLocation(string locationName)
        {
            foreach (Location l in Locations)
            {
                if (l.isAName(locationName))
                    return true;
            }
            return false;
        }



        public System.Xml.Linq.XElement ToXML()
        {
            System.Xml.Linq.XElement ele = new System.Xml.Linq.XElement("Network");

            foreach (Location l in Locations)
            {
                ele.Add(l.ToXML());
            }
            return ele;
        }

        public Network(string name)
        {
            this.Name = name;
        }


        public Network(System.Xml.Linq.XElement data, string name)
        {
            foreach (var v in data.Elements("Location"))
            {
                Locations.Add(new Location(v));
            }
            this.Name = name;
        }

    }
}
