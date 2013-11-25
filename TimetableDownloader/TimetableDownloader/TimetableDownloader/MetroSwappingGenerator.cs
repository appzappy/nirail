using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class MetroSwappingGenerator : ISwappingSource
    {

        public List<LocationPair> getSwappingLocations(Dictionary<string, int> items)
        {
            List<LocationPair> l = new List<LocationPair>();
            // TODO Write this
            //l.Add(getTL("Newry", "Portadown", items));

            //l.Add(getTL("Portadown", "Lisburn", items));
            //l.Add(getTL("Lisburn", "Great Victoria", items));
            //l.Add(getTL("Great Victoria", "Central Station", items));
            //l.Add(getTL("Central Station", "Ballymena", items));
            //l.Add(getTL("Ballymena", "Coleraine", items));

            //l.Add(getTL("Central Station", "Holywood", items));
            ////l.Add(getTL("Bangor West", "", items));

            //l.Add(getTL("Central Station", "Carrickfergus", items));





            return l;
        }

        private LocationPair getTL(string name, string name2, Dictionary<string, int> items)
        {
            int int1 = items[name];
            int int2 = items[name2];
            return new LocationPair(int1, int2);
        }
    }
}
