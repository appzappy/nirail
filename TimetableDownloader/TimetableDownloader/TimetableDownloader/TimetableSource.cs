using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class TimetableSource : ITimetableSource
    {
        public Timetable BuildTimetable(ITransportInterface data)
        {
            DateTime startLoad = DateTime.Now;

            Timetable timetable = new Timetable();
            int totalTrainsPerWeek = 0;
            List<string> timetableSources = data.TimetableUrls;

            // download all the webpages concurrently
            WebDownloader downloader = new WebDownloader();
            Dictionary<string, string> downloadedPages = downloader.downloadWebpagesConcurrently(timetableSources);

            foreach (string s in timetableSources)
            {
                string webPage = downloadedPages[s];
                ITimetableExtractor timetableExtractor = data.TimetableExtractor;
                Service service = timetableExtractor.ExtractTimetable(webPage);
                timetable.AddService(service);
                totalTrainsPerWeek += service.NumberRoutes;
            }

            DateTime finishLoad = DateTime.Now;
            TimeSpan diff = finishLoad - startLoad;
            Console.WriteLine("Timetable data downloaded in {0}s{1}ms", (int)diff.TotalSeconds, diff.Milliseconds);
            return timetable;
        }
    }
}
