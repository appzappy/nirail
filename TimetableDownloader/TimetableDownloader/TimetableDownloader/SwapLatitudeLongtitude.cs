using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;

namespace TimetableDownloader
{
    class SwapLatitudeLongtitude
    {
        public static void Process()
        {
            ITransportInterface data = new MetroSource();

            XElement networkXML = XElement.Load(data.DefaultNetworkLocation.FullName);
            Network network = new Network(networkXML, data.TimetableName);

            foreach (Location l in network.Locations)
            {
                String lat = l.Latitude;
                l.Latitude = l.Longtitude;
                l.Longtitude = lat;
            }

            XElement new_XML = network.ToXML();
            new_XML.Save(@"D:/metro_network.xml");
        }
        
    }
}
