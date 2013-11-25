using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    interface ISwappingSource
    {
        List<LocationPair> getSwappingLocations(Dictionary<string, int> items);
    }
}
