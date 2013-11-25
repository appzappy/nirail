using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace TimetableDownloader
{
    class MetroSource : ITransportInterface
    {

        #region ITransportInterface Members

        List<string> ITransportInterface.TimetableUrls
        {
            get
            {
                List<string> o = new List<string>();


                // Metro 1
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1D-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1D-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1E-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1E-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1F-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1F-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1G-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1G-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1J-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-1-Timetables/Metro-Service-1J-Outbound/");

                // Metro 2
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2D-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2D-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2E-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-2-Timetables/Metro-Service-2E-Outbound/");

                // Metro 3
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-3-Timetables/Metro-Service-3A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-3-Timetables/Metro-Service-3A-Inbound/");

                // Metro 4
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-4-Timetables/Metro-Service-4A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-4-Timetables/Metro-Service-4A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-4-Timetables/Metro-Service-4B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-4-Timetables/Metro-Service-4B-Outbound/");

                // Metro 5
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-5-Timetables/Metro-Service-5A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-5-Timetables/Metro-Service-5A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-5-Timetables/Metro-Service-5B-Inbound/");

                // Metro 6
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-6-Timetables/Metro-Service-6A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-6-Timetables/Metro-Service-6A-Inbound/");

                // Metro 7
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7D-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-7-Timetables/Metro-Service-7D-Outbound/");

                // Metro 8
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-8-Timetables/Metro-Service-8A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-8-Timetables/Metro-Service-8A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-8-Timetables/Metro-Service-8B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-8-Timetables/Metro-Service-8B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-8-Timetables/Metro-Service-8C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-8-Timetables/Metro-Service-8C-Outbound/");

                // Metro 9
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-9-Timetables/Metro-Service-9A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-9-Timetables/Metro-Service-9A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-9-Timetables/Metro-Service-9B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-9-Timetables/Metro-Service-9B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-9-Timetables/Metro-Service-9C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-9-Timetables/Metro-Service-9C-Outbound/");

                // Metro 10
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10D-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10D-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10E-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-910-Timetables/Metro-Service-10E-Outbound/");

                // Metro 11
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11D-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-11-Timetables/Metro-Service-11D-Outbound/");

                // Metro 12
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-12-Timetables/Metro-Service-12A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-12-Timetables/Metro-Service-12A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-12-Timetables/Metro-Service-12B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-12-Timetables/Metro-Service-12B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-12-Timetables/Metro-Service-12C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/Metro-12-Timetables/Metro-Service-12C-Outbound/");

                // Metro OTHER http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-13-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-13A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-13B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-13C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-14-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-14A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-14B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-14C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-18-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-18-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-19-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-19-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-20-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-20-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-20A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-20A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-23-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-23-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-26-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-26-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-26A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-26A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-26B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-26B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-26C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-26C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-27-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-27-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-28-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-28-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-29-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-29-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-29C-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-29C-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-30-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-30-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-31-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-31-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-57-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-57-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-57A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-61-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-61-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-64-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-64-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-64B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-64B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-76-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-76-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-77-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-77-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-78-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-78-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-79-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-79-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-80-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-80-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-80A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-81-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-81-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-81A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-81A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-82-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-82-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-82A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-89-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-90-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-90-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-91-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-91-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-92-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-92-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-92B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-93-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-93-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-95-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-95-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-96-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-96-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-600-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-600-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-600A-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-600A-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-600B-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-600B-Inbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-650-Inbound1/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-650-Outbound1/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB1-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB2-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB3-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB4-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB5-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB6-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB7-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB13-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB15-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB16-Outbound/");
                o.Add("http://www.translink.co.uk/Timetables/Metro-Timetables/All-Other-Metro-Timetables/Metro-Service-EB17-Outbound/");

                return o;
            }
        }

        ITimetableExtractor ITransportInterface.TimetableExtractor
        {
            get { return new TrainTimetableExtractor(); }
        }

        TransportType ITransportInterface.Transport_Type
        {
            get { return TransportType.Metro; }
        }


        FileInfo ITransportInterface.DefaultDBLocation
        {
            get { return new FileInfo("metro/metro_data.db"); }
        }


        FileInfo ITransportInterface.DefaultNetworkLocation
        {
            get { return new FileInfo("metro/MetroNetworkData.xml"); }
        }


        FileInfo ITransportInterface.DefaultZipLocation
        {
            get { return new FileInfo("metro/metro_data.zip"); }
        }

        string ITransportInterface.TimetableName
        {
            get { return "Metro Belfast"; }
        }


        ITouchingSource ITransportInterface.TouchingLocationsSource
        {
            get { return new MetroTouchingGenerator(); }
        }

        ISwappingSource ITransportInterface.SwappingLocationsSource
        {
            get { return new MetroSwappingGenerator(); }
        }

        #endregion
    }
}
