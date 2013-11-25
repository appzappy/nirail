using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class LocationPair
    {
        public int Point1
        {
            get; private set;
        }

        public int Point2
        {
            get;
            private set;
        }

        public LocationPair(int location_id1, int location_id2)
        {
            if (location_id1 < location_id2)
            {
                Point1 = location_id1;
                Point2 = location_id2;
            }
            else
            {
                Point1 = location_id2;
                Point2 = location_id1;
            }

        }
    }
}
