using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;


namespace TimetableDownloader
{
    class TrainSource : ITransportInterface
    {

        #region ITransportInterface Members

        List<string> ITransportInterface.TimetableUrls
        {
            get
            {
                string[] urls = new string[]
                {
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-1-Outbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-1-Inbound/",
                        //"http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-1-Outbound1/",
                        //"http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-1-Inbound1/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-2-Outbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-2-Inbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-3-Outbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-3-Inbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-4-Outbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Northern-Ireland-Railways-Service-4-Inbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Enterprise-Service-5-Outbound/",
                        "http://www.translink.co.uk/Services/NI-Railways/Routes--Timetables/All-Timetables/Enterprise-Service-5-Inbound/"
                };
                return new List<String>(urls);
            }
        }

        ITimetableExtractor ITransportInterface.TimetableExtractor
        {
            get { return new TrainTimetableExtractor(); }
        }

        TransportType ITransportInterface.Transport_Type
        {
            get { return TransportType.Train; }
        }


        FileInfo ITransportInterface.DefaultDBLocation
        {
            get { return new FileInfo("train/rail_data.db"); }
        }

        FileInfo ITransportInterface.DefaultNetworkLocation
        {
            get { return new FileInfo("train/TrainNetworkData.xml"); }
        }
        
        FileInfo ITransportInterface.DefaultZipLocation
        {
            get { return new FileInfo("train/rail_data.zip"); }
        }
        
        string ITransportInterface.TimetableName
        {
            get { return "NI Rail"; }
        }

        ITouchingSource ITransportInterface.TouchingLocationsSource
        {
            get { return new TrainTouchingGenerator(); }
        }

        ISwappingSource ITransportInterface.SwappingLocationsSource
        {
            get { return new TrainSwappingGenerator(); }
        }

        #endregion
    }
}
