using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;


namespace TimetableDownloader
{
    interface ITransportInterface
    {
        List<String> TimetableUrls { get; }

        ITimetableExtractor TimetableExtractor { get; }
        TransportType Transport_Type { get; }

        FileInfo DefaultDBLocation { get; }
        FileInfo DefaultNetworkLocation { get; }
        FileInfo DefaultZipLocation { get; }

        String TimetableName { get; }

        ITouchingSource TouchingLocationsSource { get; }
        ISwappingSource SwappingLocationsSource { get; }
    }
}
