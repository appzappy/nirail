using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    interface ITouchingSource
    {
        List<LocationPair> getTouchingLocations(Dictionary<string, int> items);
    }
}
